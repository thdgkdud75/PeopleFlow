package model;

import java.time.LocalDate;

public class Employee implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private int empId;
    private String empNo;
    private String name;
    private String email;
    private String phone;
    private int deptId;
    private int posId;
    private LocalDate hireDate;
    private String status; // ACTIVE, RESIGNED, ON_LEAVE
    private String role;   // ADMIN, EMPLOYEE
    private String password;
    private String deptName;
    private String posName;
    private int remainLeave;

    public Employee() {}

    public Employee(int empId, String empNo, String name, String email, String phone,
                    int deptId, int posId, LocalDate hireDate, String status, String role) {
        this.empId = empId;
        this.empNo = empNo;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.deptId = deptId;
        this.posId = posId;
        this.hireDate = hireDate;
        this.status = status;
        this.role = role;
    }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getEmpNo() { return empNo; }
    public void setEmpNo(String empNo) { this.empNo = empNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }

    public int getPosId() { return posId; }
    public void setPosId(int posId) { this.posId = posId; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public String getPosName() { return posName; }
    public void setPosName(String posName) { this.posName = posName; }

    public int getRemainLeave() { return remainLeave; }
    public void setRemainLeave(int remainLeave) { this.remainLeave = remainLeave; }
}
