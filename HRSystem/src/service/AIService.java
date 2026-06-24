package service;

import dao.AttendanceDAO;
import dao.EvalDAO;
import model.Attendance;
import model.Evaluation;
import util.AIUtil;

import java.sql.SQLException;
import java.util.List;

public class AIService {
    private final AttendanceDAO attDao  = new AttendanceDAO();
    private final EvalDAO       evalDao = new EvalDAO();

    // 1번: HR 챗봇 - Agent가 필요한 Tool을 직접 호출하여 실시간 데이터로 답변
    public String chatbot(String userMessage, String empName, int empId) throws Exception {
        String role = "ADMIN".equals(empName) || empId == 1 ? "ADMIN" : "EMPLOYEE";
        return AIUtil.callAgentChat(userMessage, empId, empName, role, 400);
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

        String system =
            "당신은 HR 근태 분석 담당자입니다. 아래 월간 근태 요약 데이터를 바탕으로 "
            + "근태 현황을 분석하는 간결한 리포트를 한국어로 작성하세요. 다음 형식을 따르세요.\n\n"
            + "📊 근태 분석 리포트\n\n▶ 종합 현황\n- ...\n\n⚠️ 특이사항\n- ...\n\n💡 개선 제안\n- ...";

        return AIUtil.callGenerate(system, dataStr, 500);
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

        String system =
            "당신은 HR 인사평가 전문가입니다. 아래 평가 데이터를 바탕으로 객관적이고 전문적인 "
            + "인사 평가서를 한국어로 작성하세요. 다음 형식을 따르세요.\n\n"
            + "📋 인사 평가서\n\n▶ 종합 의견\n- ...\n\n✅ 강점\n- ...\n\n📈 개선 영역\n- ...\n\n🎯 향후 기대\n- ...";

        return AIUtil.callGenerate(system, dataStr, 500);
    }
}
