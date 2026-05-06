package service;

import dao.AttendanceDAO;
import dao.EvalDAO;
import model.Attendance;
import model.Evaluation;
import util.AIUtil;

import java.sql.SQLException;
import java.util.List;

public class AIService {
    private final AttendanceDAO attDao = new AttendanceDAO();
    private final EvalDAO evalDao = new EvalDAO();

    // 1번: HR 챗봇 - 직원이 HR 관련 질문 시 AI가 답변
    public String chatbot(String userMessage, String empName) throws Exception {
        String system = "당신은 " + empName + " 직원을 돕는 사내 HR 챗봇입니다. " +
                "연차, 급여, 출퇴근, 복지제도 등 HR 관련 질문에 친절하고 정확하게 답변하세요. " +
                "사내 규정에 대해서는 '인사팀에 문의하세요'라고 안내할 수 있습니다.";
        return AIUtil.callClaude(system, userMessage);
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

        String system = "당신은 HR 전문가입니다. 주어진 근태 데이터를 분석하여 관리자에게 보고서를 작성하세요. " +
                "문제점과 개선 방안을 포함하고, 전문적이고 간결하게 작성하세요.";
        return AIUtil.callClaude(system, dataStr);
    }

    // 5번: AI 인사 평가서 생성 - 평가 데이터 기반으로 AI가 평가 보고서 작성
    public String generateEvalReport(int evalId) throws Exception, SQLException {
        Evaluation eval = evalDao.findById(evalId);
        if (eval == null) throw new IllegalArgumentException("평가 데이터를 찾을 수 없습니다.");

        String dataStr = String.format(
            "[인사 평가 데이터]\n직원명: %s\n평가 기간: %s\n점수: %d점\n등급: %s\n평가자 코멘트: %s",
            eval.getEmpName(), eval.getEvalPeriod(), eval.getScore(), eval.getGrade(), eval.getComments()
        );

        String system = "당신은 HR 전문 컨설턴트입니다. 주어진 인사 평가 데이터를 바탕으로 " +
                "직원의 역량과 성과를 분석한 공식 인사 평가서를 작성하세요. " +
                "강점, 개선 필요 역량, 향후 발전 방향을 포함하여 전문적으로 작성하세요.";
        return AIUtil.callClaude(system, dataStr);
    }
}
