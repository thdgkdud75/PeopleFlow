package service;

import dao.SalaryDAO;
import model.Salary;

import java.sql.SQLException;
import java.util.List;

public class SalaryService {
    private final SalaryDAO dao = new SalaryDAO();

    public void createSalary(Salary sal) throws SQLException {
        sal.setNetPay(sal.getBasePay() + sal.getAllowance() - sal.getDeduction());
        dao.insert(sal);
    }

    public void confirmPayment(int salId) throws SQLException {
        dao.updateStatus(salId, "PAID");
    }

    public List<Salary> getMySalaries(int empId) throws SQLException {
        return dao.findByEmpId(empId);
    }

    public List<Salary> getMonthSalaries(String yearMonth) throws SQLException {
        return dao.findByMonth(yearMonth);
    }
}
