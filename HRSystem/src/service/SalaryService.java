package service;

import dao.SalaryDAO;
import model.Salary;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SalaryService {
    private final SalaryDAO dao = new SalaryDAO();

    // 수당 고정액: 식비 100,000 + 교통비 50,000
    public static final long FIXED_ALLOWANCE = 150_000L;

    // 공제율: 국민연금 4.5% + 건강보험 3.545% + 고용보험 0.9% = 8.945% ≈ 9%
    public static final double DEDUCTION_RATE = 0.09;

    public void createSalary(Salary sal) throws SQLException {
        sal.setNetPay(sal.getBasePay() + sal.getAllowance() - sal.getDeduction());
        dao.insert(sal);
    }

    // 월 급여 일괄 생성 - 직급 기본급 기반 자동계산
    public int generateMonthlySalaries(String month) throws SQLException {
        Map<Integer, Long> employees = dao.findEmployeesNeedingSalary(month);
        int count = 0;
        for (Map.Entry<Integer, Long> entry : employees.entrySet()) {
            long basePay   = entry.getValue();
            long allowance = FIXED_ALLOWANCE;
            long deduction = Math.round(basePay * DEDUCTION_RATE);
            long netPay    = basePay + allowance - deduction;

            Salary sal = new Salary();
            sal.setEmpId(entry.getKey());
            sal.setSalMonth(month);
            sal.setBasePay(basePay);
            sal.setAllowance(allowance);
            sal.setDeduction(deduction);
            sal.setNetPay(netPay);
            dao.insert(sal);
            count++;
        }
        return count;
    }

    public void confirmPayment(int salId) throws SQLException {
        dao.updateStatus(salId, "PAID");
    }

    public int confirmAllPayments(String month) throws SQLException {
        return dao.confirmAllByMonth(month);
    }

    public Salary getSalary(int salId) throws SQLException {
        return dao.findById(salId);
    }

    public List<Salary> getMySalaries(int empId) throws SQLException {
        return dao.findByEmpId(empId);
    }

    public List<Salary> getMonthSalaries(String yearMonth) throws SQLException {
        return dao.findByMonth(yearMonth);
    }
}
