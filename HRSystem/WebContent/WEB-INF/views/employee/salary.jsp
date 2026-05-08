<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Salary" %>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">
    <h4 class="mb-4">내 급여 내역</h4>
    <div class="card">
        <div class="card-body p-0">
            <table class="table table-hover mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-3">급여월</th>
                        <th class="text-end">기본급</th>
                        <th class="text-end">수당</th>
                        <th class="text-end">공제</th>
                        <th class="text-end">실수령액</th>
                        <th class="text-center">상태</th>
                        <th class="text-center">명세서</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    List<Salary> list = (List<Salary>) request.getAttribute("salaryList");
                    if (list == null || list.isEmpty()) {
                %>
                <tr><td colspan="7" class="text-center text-muted py-4">급여 내역이 없습니다.</td></tr>
                <%
                    } else { for (Salary sal : list) {
                        long pension    = Math.round(sal.getBasePay() * 0.045);
                        long health     = Math.round(sal.getBasePay() * 0.03545);
                        long employment = Math.round(sal.getBasePay() * 0.009);
                %>
                <tr>
                    <td class="ps-3"><%= sal.getSalMonth() %></td>
                    <td class="text-end"><%= String.format("%,d", sal.getBasePay()) %>원</td>
                    <td class="text-end text-success">+<%= String.format("%,d", sal.getAllowance()) %>원</td>
                    <td class="text-end text-danger">-<%= String.format("%,d", sal.getDeduction()) %>원</td>
                    <td class="text-end fw-bold text-primary"><%= String.format("%,d", sal.getNetPay()) %>원</td>
                    <td class="text-center">
                        <span class="badge <%= "PAID".equals(sal.getPayStatus()) ? "bg-success" : "bg-warning text-dark" %>">
                            <%= "PAID".equals(sal.getPayStatus()) ? "지급완료" : "대기중" %>
                        </span>
                    </td>
                    <td class="text-center">
                        <button class="btn btn-sm btn-outline-secondary btn-payslip"
                            data-month="<%= sal.getSalMonth() %>"
                            data-base="<%= sal.getBasePay() %>"
                            data-allowance="<%= sal.getAllowance() %>"
                            data-deduction="<%= sal.getDeduction() %>"
                            data-net="<%= sal.getNetPay() %>"
                            data-pension="<%= pension %>"
                            data-health="<%= health %>"
                            data-employment="<%= employment %>"
                            data-bs-toggle="modal" data-bs-target="#payslipModal">
                            명세서
                        </button>
                    </td>
                </tr>
                <% } } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</div>
</div>

<%-- 급여 명세서 모달 --%>
<div class="modal fade" id="payslipModal" tabindex="-1">
  <div class="modal-dialog modal-md">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">급여 명세서 — <span id="ps-month"></span></h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        <table class="table table-bordered table-sm">
          <tbody>
            <tr class="table-light"><td colspan="2" class="fw-bold">지급 항목</td></tr>
            <tr>
              <td>기본급</td>
              <td class="text-end" id="ps-base"></td>
            </tr>
            <tr>
              <td>식비</td>
              <td class="text-end">100,000원</td>
            </tr>
            <tr>
              <td>교통비</td>
              <td class="text-end">50,000원</td>
            </tr>
            <tr class="fw-bold">
              <td>지급 합계</td>
              <td class="text-end" id="ps-gross"></td>
            </tr>
            <tr class="table-light"><td colspan="2" class="fw-bold">공제 항목</td></tr>
            <tr>
              <td>국민연금 (4.5%)</td>
              <td class="text-end text-danger" id="ps-pension"></td>
            </tr>
            <tr>
              <td>건강보험 (3.545%)</td>
              <td class="text-end text-danger" id="ps-health"></td>
            </tr>
            <tr>
              <td>고용보험 (0.9%)</td>
              <td class="text-end text-danger" id="ps-employment"></td>
            </tr>
            <tr class="fw-bold">
              <td>공제 합계</td>
              <td class="text-end text-danger" id="ps-deduct"></td>
            </tr>
            <tr class="table-primary">
              <td class="fw-bold fs-5">실수령액</td>
              <td class="text-end fw-bold fs-5" id="ps-net"></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<script>
function fmt(n) {
    return Number(n).toLocaleString('ko-KR') + '원';
}
document.querySelectorAll('.btn-payslip').forEach(function(btn) {
    btn.addEventListener('click', function() {
        var base       = parseInt(this.dataset.base);
        var allowance  = parseInt(this.dataset.allowance);
        var deduction  = parseInt(this.dataset.deduction);
        var pension    = parseInt(this.dataset.pension);
        var health     = parseInt(this.dataset.health);
        var employment = parseInt(this.dataset.employment);

        document.getElementById('ps-month').textContent      = this.dataset.month;
        document.getElementById('ps-base').textContent       = fmt(base);
        document.getElementById('ps-gross').textContent      = fmt(base + allowance);
        document.getElementById('ps-pension').textContent    = '-' + fmt(pension);
        document.getElementById('ps-health').textContent     = '-' + fmt(health);
        document.getElementById('ps-employment').textContent = '-' + fmt(employment);
        document.getElementById('ps-deduct').textContent     = '-' + fmt(deduction);
        document.getElementById('ps-net').textContent        = fmt(parseInt(this.dataset.net));
    });
});
</script>
<%@ include file="/common/footer.jsp" %>
