package model;

import java.time.LocalDate;

public class MeetingMinutes {
    private int meetingId;
    private int empId;
    private String empName;
    private LocalDate meetingDate;
    private String title;
    private String attendees;
    private String content;
    private String aiSummary;

    public int getMeetingId() { return meetingId; }
    public void setMeetingId(int meetingId) { this.meetingId = meetingId; }
    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }
    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }
    public LocalDate getMeetingDate() { return meetingDate; }
    public void setMeetingDate(LocalDate meetingDate) { this.meetingDate = meetingDate; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAttendees() { return attendees; }
    public void setAttendees(String attendees) { this.attendees = attendees; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
}
