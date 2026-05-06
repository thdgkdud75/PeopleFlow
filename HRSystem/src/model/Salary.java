package model;

public class Salary {
    private int salId;
    private int empId;
    private String empName;
    private String salMonth; // YYYY-MM
    private long basePay;
    private long allowance;
    private long deduction;
    private long netPay;
    private String payStatus; // PENDING, PAID

    public Salary() {}

    public int getSalId() { return salId; }
    public void setSalId(int salId) { this.salId = salId; }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }

    public String getSalMonth() { return salMonth; }
    public void setSalMonth(String salMonth) { this.salMonth = salMonth; }

    public long getBasePay() { return basePay; }
    public void setBasePay(long basePay) { this.basePay = basePay; }

    public long getAllowance() { return allowance; }
    public void setAllowance(long allowance) { this.allowance = allowance; }

    public long getDeduction() { return deduction; }
    public void setDeduction(long deduction) { this.deduction = deduction; }

    public long getNetPay() { return netPay; }
    public void setNetPay(long netPay) { this.netPay = netPay; }

    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }
}
