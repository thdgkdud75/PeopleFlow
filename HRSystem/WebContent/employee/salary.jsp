<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Salary" %>
<%@ include file="/common/header.jsp" %>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <h4 class="mb-4">내 급여 내역</h4>
    <div class="card">
        <div class="card-body">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr><th>급여월</th><th>기본급</th><th>수당</th><th>공제</th><th>실수령액</th><th>상태</th></tr>
                </thead>
                <tbody>
                <%
                    List<Salary> list = (List<Salary>) request.getAttribute("salaryList");
                    if (list != null) for (Salary sal : list) {
                %>
                <tr>
                    <td><%= sal.getSalMonth() %></td>
                    <td><%= String.format("%,d", sal.getBasePay()) %>원</td>
                    <td><%= String.format("%,d", sal.getAllowance()) %>원</td>
                    <td><%= String.format("%,d", sal.getDeduction()) %>원</td>
                    <td class="fw-bold text-primary"><%= String.format("%,d", sal.getNetPay()) %>원</td>
                    <td><span class="badge <%= "PAID".equals(sal.getPayStatus()) ? "bg-success" : "bg-warning" %>">
                        <%= "PAID".equals(sal.getPayStatus()) ? "지급완료" : "대기중" %></span></td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
