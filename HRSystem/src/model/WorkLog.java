package model;

import java.time.LocalDate;

public class WorkLog {
    private int logId;
    private int empId;
    private String empName;
    private LocalDate logDate;
    private String content;
    private String aiSummary;

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }
    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }
    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
}
