package model;

import java.time.LocalDate;

public class Evaluation {
    private int evalId;
    private int empId;
    private String empName;
    private String evalPeriod; // e.g. "2025-H1"
    private int score;
    private String grade; // S, A, B, C, D
    private String comments;
    private String workSummary;
    private String aiReport;
    private int evaluatorId;
    private String evaluatorName;
    private LocalDate evalDate;

    public Evaluation() {}

    public int getEvalId() { return evalId; }
    public void setEvalId(int evalId) { this.evalId = evalId; }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }

    public String getEvalPeriod() { return evalPeriod; }
    public void setEvalPeriod(String evalPeriod) { this.evalPeriod = evalPeriod; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getWorkSummary() { return workSummary; }
    public void setWorkSummary(String workSummary) { this.workSummary = workSummary; }

    public String getAiReport() { return aiReport; }
    public void setAiReport(String aiReport) { this.aiReport = aiReport; }

    public int getEvaluatorId() { return evaluatorId; }
    public void setEvaluatorId(int evaluatorId) { this.evaluatorId = evaluatorId; }

    public String getEvaluatorName() { return evaluatorName; }
    public void setEvaluatorName(String evaluatorName) { this.evaluatorName = evaluatorName; }

    public LocalDate getEvalDate() { return evalDate; }
    public void setEvalDate(LocalDate evalDate) { this.evalDate = evalDate; }
}
