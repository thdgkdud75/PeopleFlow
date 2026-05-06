<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.Salary" %>
<%@ include file="/common/header.jsp" %>
<%
    Salary sal = (Salary) request.getAttribute("salary");
%>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <h4 class="mb-4">급여 상세</h4>
    <% if (sal != null) { %>
    <div class="card">
        <div class="card-body">
            <table class="table">
                <tr><th>직원명</th><td><%= sal.getEmpName() %></td></tr>
                <tr><th>급여월</th><td><%= sal.getSalMonth() %></td></tr>
                <tr><th>기본급</th><td><%= String.format("%,d", sal.getBasePay()) %>원</td></tr>
                <tr><th>수당</th><td><%= String.format("%,d", sal.getAllowance()) %>원</td></tr>
                <tr><th>공제액</th><td><%= String.format("%,d", sal.getDeduction()) %>원</td></tr>
                <tr><th class="text-primary">실수령액</th><td class="fw-bold text-primary"><%= String.format("%,d", sal.getNetPay()) %>원</td></tr>
                <tr><th>지급상태</th><td><span class="badge <%= "PAID".equals(sal.getPayStatus()) ? "bg-success" : "bg-warning" %>">
                    <%= "PAID".equals(sal.getPayStatus()) ? "지급완료" : "미지급" %></span></td></tr>
            </table>
            <a href="${pageContext.request.contextPath}/salary/list" class="btn btn-secondary">목록</a>
        </div>
    </div>
    <% } %>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
