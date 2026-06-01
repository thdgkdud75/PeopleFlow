package model;

import java.time.LocalDate;

public class BusinessTrip {
    private int tripId;
    private int empId;
    private String empName;
    private LocalDate tripStart;
    private LocalDate tripEnd;
    private String destination;
    private String purpose;
    private String content;
    private String aiSummary;

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }
    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }
    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }
    public LocalDate getTripStart() { return tripStart; }
    public void setTripStart(LocalDate tripStart) { this.tripStart = tripStart; }
    public LocalDate getTripEnd() { return tripEnd; }
    public void setTripEnd(LocalDate tripEnd) { this.tripEnd = tripEnd; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
}
