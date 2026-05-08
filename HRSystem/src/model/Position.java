package model;

public class Position {
    private int posId;
    private String posName;
    private int level;
    private long baseSalary;
    private int empCount;

    public Position() {}

    public int getPosId() { return posId; }
    public void setPosId(int posId) { this.posId = posId; }

    public String getPosName() { return posName; }
    public void setPosName(String posName) { this.posName = posName; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public long getBaseSalary() { return baseSalary; }
    public void setBaseSalary(long baseSalary) { this.baseSalary = baseSalary; }

    public int getEmpCount() { return empCount; }
    public void setEmpCount(int empCount) { this.empCount = empCount; }
}
