package model;

public class Position {
    private int posId;
    private String posName;
    private int level;
    private long baseSalary;

    public Position() {}

    public Position(int posId, String posName, int level, long baseSalary) {
        this.posId = posId;
        this.posName = posName;
        this.level = level;
        this.baseSalary = baseSalary;
    }

    public int getPosId() { return posId; }
    public void setPosId(int posId) { this.posId = posId; }

    public String getPosName() { return posName; }
    public void setPosName(String posName) { this.posName = posName; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public long getBaseSalary() { return baseSalary; }
    public void setBaseSalary(long baseSalary) { this.baseSalary = baseSalary; }
}
