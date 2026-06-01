package service;

import dao.AttendanceDAO;
import dao.EvalDAO;
import dao.LeaveDAO;
import dao.SalaryDAO;
import model.Attendance;
import model.Evaluation;
import model.Salary;
import util.AIUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AIService {
    private final AttendanceDAO attDao  = new AttendanceDAO();
    private final EvalDAO       evalDao = new EvalDAO();
    private final LeaveDAO      leaveDao  = new LeaveDAO();
    private final SalaryDAO     salaryDao = new SalaryDAO();

    // 1번: HR 챗봇 - 실시간 DB 컨텍스트를 포함하여 로컬 EXAONE 모델로 답변
    public String chatbot(String userMessage, String empName, int empId) throws Exception {
        String context = buildEmployeeContext(empId);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
        String system = "당신은 " + empName + " 직원을 돕는 사내 챗봇입니다. " +
                "오늘 날짜는 " + today + "입니다. " +
                "HR 관련 질문은 아래 실시간 데이터를 활용해 답변하고, 일반 질문에도 대화하세요. " +
                "답변은 핵심만 짧고 정확하게 작성하세요. 불필요한 설명이나 추가 안내는 하지 마세요.\n\n" +
                context;
        return AIUtil.callLocalModel(system, userMessage, 250);
    }

    private String buildEmployeeContext(int empId) throws SQLException {
        StringBuilder sb = new StringBuilder("[직원 실시간 HR 정보]\n");

        // 잔여 연차
        int remain = leaveDao.getRemainLeave(empId);
        sb.append("- 잔여 연차: ").append(remain).append("일\n");

        // 이번 달 근태 현황
        String thisMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<Attendance> attList = attDao.findByEmpIdAndMonth(empId, thisMonth);
        if (!attList.isEmpty()) {
            long late      = attList.stream().filter(a -> "LATE".equals(a.getStatus())).count();
            long absent    = attList.stream().filter(a -> "ABSENT".equals(a.getStatus())).count();
            long early     = attList.stream().filter(a -> "EARLY_LEAVE".equals(a.getStatus())).count();
            long normal    = attList.size() - late - absent - early;
            sb.append("- 이번 달 근태(").append(thisMonth).append("): ")
              .append("정상출근 ").append(normal).append("일")
              .append(", 지각 ").append(late).append("건")
              .append(", 결근 ").append(absent).append("건")
              .append(", 조기퇴근 ").append(early).append("건\n");
        } else {
            sb.append("- 이번 달 근태: 기록 없음\n");
        }

        // 최근 급여 내역
        List<Salary> salaries = salaryDao.findByEmpId(empId);
        if (!salaries.isEmpty()) {
            Salary latest = salaries.get(0);
            sb.append(String.format("- 최근 급여(%s): 기본급 %,d원, 수당 %,d원, 공제 %,d원, 실수령액 %,d원\n",
                latest.getSalMonth(), latest.getBasePay(),
                latest.getAllowance(), latest.getDeduction(), latest.getNetPay()));
            if (salaries.size() >= 2) {
                Salary prev = salaries.get(1);
                long diff = latest.getNetPay() - prev.getNetPay();
                String sign = diff >= 0 ? "+" : "";
                sb.append(String.format("- 전월 대비 실수령액: %s%,d원 (%s)\n",
                    sign, diff, diff >= 0 ? "증가" : "감소"));
            }
        } else {
            sb.append("- 급여 내역: 없음\n");
        }

        return sb.toString();
    }

    // 4번: AI 근태 분석 - 월별 근태 데이터를 분석하여 리포트 생성
    public String analyzeAttendance(String yearMonth) throws Exception, SQLException {
        List<Attendance> list = attDao.findByMonth(yearMonth);
        if (list.isEmpty()) return "해당 월의 근태 데이터가 없습니다.";

        long late = list.stream().filter(a -> "LATE".equals(a.getStatus())).count();
        long absent = list.stream().filter(a -> "ABSENT".equals(a.getStatus())).count();
        long earlyLeave = list.stream().filter(a -> "EARLY_LEAVE".equals(a.getStatus())).count();
        long total = list.size();

        String dataStr = String.format(
            "[%s 근태 요약]\n총 기록: %d건\n지각: %d건\n결근: %d건\n조기퇴근: %d건\n정상출근: %d건",
            yearMonth, total, late, absent, earlyLeave, total - late - absent - earlyLeave
        );

        return AIUtil.callLocalModel(null, dataStr, 500);
    }

    public void saveEvalReport(int evalId, String aiReport) throws Exception {
        evalDao.updateAiReport(evalId, aiReport);
    }

    // 5번: AI 인사 평가서 생성 - 평가 데이터 기반으로 AI가 평가 보고서 작성
    public String generateEvalReport(int evalId) throws Exception, SQLException {
        Evaluation eval = evalDao.findById(evalId);
        if (eval == null) throw new IllegalArgumentException("평가 데이터를 찾을 수 없습니다.");

        String workSummary = (eval.getWorkSummary() != null && !eval.getWorkSummary().isBlank())
            ? eval.getWorkSummary() : "미입력";
        String dataStr = String.format(
            "[인사 평가 데이터]\n직원명: %s\n평가 기간: %s\n점수: %d점\n등급: %s\n주요 업무 내용: %s\n평가자 코멘트: %s",
            eval.getEmpName(), eval.getEvalPeriod(), eval.getScore(), eval.getGrade(), workSummary, eval.getComments()
        );

        return AIUtil.callLocalModel(null, dataStr, 500);
    }
}
