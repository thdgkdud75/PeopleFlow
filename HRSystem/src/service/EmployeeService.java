package service;

import dao.EmployeeDAO;
import model.Employee;

import java.sql.SQLException;
import java.util.List;

public class EmployeeService {
    private final EmployeeDAO dao = new EmployeeDAO();

    public Employee login(String empNo, String password) throws SQLException {
        Employee emp = dao.findByEmpNo(empNo);
        if (emp == null) return null;
        // 실제 운영 시 BCrypt 등으로 해시 비교 필요
        return emp.getPassword().equals(password) ? emp : null;
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return dao.findAll();
    }

    public Employee getEmployee(int empId) throws SQLException {
        return dao.findById(empId);
    }

    public void registerEmployee(Employee emp) throws SQLException {
        dao.insert(emp);
    }

    public void updateEmployee(Employee emp) throws SQLException {
        dao.update(emp);
    }

    public void deleteEmployee(int empId) throws SQLException {
        dao.delete(empId);
    }
}
