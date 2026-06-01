package service;

import dao.BusinessTripDAO;
import dao.MeetingMinutesDAO;
import dao.WorkLogDAO;
import model.BusinessTrip;
import model.MeetingMinutes;
import model.WorkLog;
import util.AIUtil;

import java.sql.SQLException;
import java.util.List;

public class DocumentService {

    private final WorkLogDAO workLogDao           = new WorkLogDAO();
    private final MeetingMinutesDAO meetingDao    = new MeetingMinutesDAO();
    private final BusinessTripDAO tripDao         = new BusinessTripDAO();

    // ── 업무일지 ──────────────────────────────────────────────
    public void saveWorkLog(WorkLog w) throws SQLException {
        workLogDao.insert(w);
    }

    public List<WorkLog> getMyWorkLogs(int empId) throws SQLException {
        return workLogDao.findByEmpId(empId);
    }

    public List<WorkLog> getAllWorkLogs() throws SQLException {
        return workLogDao.findAll();
    }

    public String summarizeWorkLog(int logId) throws Exception {
        WorkLog w = workLogDao.findById(logId);
        if (w == null) throw new IllegalArgumentException("업무일지를 찾을 수 없습니다.");
        String input = "[업무일지]\n날짜: " + w.getLogDate() + "\n내용:\n" + w.getContent();
        String summary = AIUtil.callLocalModel(
            "당신은 업무 문서 요약 AI입니다. 핵심 업무 내용을 간결하게 정리하세요.",
            input, 300);
        workLogDao.updateSummary(logId, summary);
        return summary;
    }

    // ── 회의록 ────────────────────────────────────────────────
    public void saveMeeting(MeetingMinutes m) throws SQLException {
        meetingDao.insert(m);
    }

    public List<MeetingMinutes> getMyMeetings(int empId) throws SQLException {
        return meetingDao.findByEmpId(empId);
    }

    public List<MeetingMinutes> getAllMeetings() throws SQLException {
        return meetingDao.findAll();
    }

    public String summarizeMeeting(int meetingId) throws Exception {
        MeetingMinutes m = meetingDao.findById(meetingId);
        if (m == null) throw new IllegalArgumentException("회의록을 찾을 수 없습니다.");
        String input = "[회의록]\n제목: " + m.getTitle() +
                       "\n날짜: " + m.getMeetingDate() +
                       "\n참석자: " + (m.getAttendees() != null ? m.getAttendees() : "-") +
                       "\n내용:\n" + m.getContent();
        String summary = AIUtil.callLocalModel(
            "당신은 회의록 정리 AI입니다. 회의 핵심 내용·결정사항·액션아이템을 간결하게 정리하세요.",
            input, 400);
        meetingDao.updateSummary(meetingId, summary);
        return summary;
    }

    // ── 출장보고서 ────────────────────────────────────────────
    public void saveTrip(BusinessTrip t) throws SQLException {
        tripDao.insert(t);
    }

    public List<BusinessTrip> getMyTrips(int empId) throws SQLException {
        return tripDao.findByEmpId(empId);
    }

    public List<BusinessTrip> getAllTrips() throws SQLException {
        return tripDao.findAll();
    }

    public String summarizeTrip(int tripId) throws Exception {
        BusinessTrip t = tripDao.findById(tripId);
        if (t == null) throw new IllegalArgumentException("출장보고서를 찾을 수 없습니다.");
        String input = "[출장보고서]\n기간: " + t.getTripStart() + " ~ " + t.getTripEnd() +
                       "\n출장지: " + t.getDestination() +
                       "\n목적: " + t.getPurpose() +
                       "\n내용:\n" + t.getContent();
        String summary = AIUtil.callLocalModel(
            "당신은 출장보고서 요약 AI입니다. 출장 목적·주요 활동·결과를 간결하게 요약하세요.",
            input, 400);
        tripDao.updateSummary(tripId, summary);
        return summary;
    }
}
