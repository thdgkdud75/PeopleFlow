package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Attendance {
    private int attId;
    private int empId;
    private String empName;
    private LocalDate attDate;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private String status; // NORMAL, LATE, EARLY_LEAVE, ABSENT, HALF_DAY

    public Attendance() {}

    public int getAttId() { return attId; }
    public void setAttId(int attId) { this.attId = attId; }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }

    public LocalDate getAttDate() { return attDate; }
    public void setAttDate(LocalDate attDate) { this.attDate = attDate; }

    public LocalTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalTime checkIn) { this.checkIn = checkIn; }

    public LocalTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalTime checkOut) { this.checkOut = checkOut; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
