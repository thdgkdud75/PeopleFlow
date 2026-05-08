package model;

public class Department {
    private int deptId;
    private String deptName;
    private int managerId;
    private String managerName;
    private int empCount;

    public Department() {}

    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public int getManagerId() { return managerId; }
    public void setManagerId(int managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public int getEmpCount() { return empCount; }
    public void setEmpCount(int empCount) { this.empCount = empCount; }
}
