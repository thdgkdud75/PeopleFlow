<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Salary" %>
<%@ include file="/common/header.jsp" %>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4>급여 관리</h4>
        <form action="${pageContext.request.contextPath}/salary/list" method="get" class="d-flex gap-2">
            <input type="month" name="month" class="form-control" value="${month}">
            <button type="submit" class="btn btn-primary">조회</button>
        </form>
    </div>
    <div class="card">
        <div class="card-body">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr><th>이름</th><th>기본급</th><th>수당</th><th>공제</th><th>실수령액</th><th>상태</th><th>관리</th></tr>
                </thead>
                <tbody>
                <%
                    List<Salary> list = (List<Salary>) request.getAttribute("salaryList");
                    if (list != null) for (Salary sal : list) {
                %>
                <tr>
                    <td><%= sal.getEmpName() %></td>
                    <td><%= String.format("%,d", sal.getBasePay()) %>원</td>
                    <td><%= String.format("%,d", sal.getAllowance()) %>원</td>
                    <td><%= String.format("%,d", sal.getDeduction()) %>원</td>
                    <td class="fw-bold"><%= String.format("%,d", sal.getNetPay()) %>원</td>
                    <td><span class="badge <%= "PAID".equals(sal.getPayStatus()) ? "bg-success" : "bg-warning" %>">
                        <%= "PAID".equals(sal.getPayStatus()) ? "지급완료" : "대기" %></span></td>
                    <td>
                        <% if ("PENDING".equals(sal.getPayStatus())) { %>
                        <form action="${pageContext.request.contextPath}/salary/pay" method="post" class="d-inline">
                            <input type="hidden" name="salId" value="<%= sal.getSalId() %>">
                            <button type="submit" class="btn btn-sm btn-success">지급 확정</button>
                        </form>
                        <% } %>
                    </td>
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
