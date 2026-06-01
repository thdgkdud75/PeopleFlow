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
import java.util.logging.Logger;

public class DocumentService {

    private static final Logger log = Logger.getLogger(DocumentService.class.getName());
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
        log.info("[DEBUG] summarizeWorkLog called, logId=" + logId);
        WorkLog w = workLogDao.findById(logId);
        if (w == null) throw new IllegalArgumentException("업무일지를 찾을 수 없습니다.");
        String content = "[업무일지]\n날짜: " + w.getLogDate() + "\n내용:\n" + w.getContent();
        log.info("[DEBUG] calling callSummarize, content length=" + content.length());
        try {
            String summary = AIUtil.callSummarize("worklog", content);
            log.info("[DEBUG] callSummarize returned: " + (summary == null ? "NULL" : summary.substring(0, Math.min(50, summary.length()))));
            workLogDao.updateSummary(logId, summary);
            return summary;
        } catch (Exception e) {
            log.severe("[DEBUG] callSummarize EXCEPTION: " + e.getClass().getName() + " - " + e.getMessage());
            throw e;
        }
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
        String content = "[회의록]\n제목: " + m.getTitle() +
                         "\n날짜: " + m.getMeetingDate() +
                         "\n참석자: " + (m.getAttendees() != null ? m.getAttendees() : "-") +
                         "\n내용:\n" + m.getContent();
        String summary = AIUtil.callSummarize("meeting", content);
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
        String content = "[출장보고서]\n기간: " + t.getTripStart() + " ~ " + t.getTripEnd() +
                         "\n출장지: " + t.getDestination() +
                         "\n목적: " + t.getPurpose() +
                         "\n내용:\n" + t.getContent();
        String summary = AIUtil.callSummarize("trip", content);
        tripDao.updateSummary(tripId, summary);
        return summary;
    }
}
