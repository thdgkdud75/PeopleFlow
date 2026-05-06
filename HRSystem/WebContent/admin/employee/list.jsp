<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Employee" %>
<%@ include file="/common/header.jsp" %>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4>직원 관리</h4>
        <a href="${pageContext.request.contextPath}/admin/employee/register.jsp" class="btn btn-primary">+ 직원 등록</a>
    </div>
    <div class="card">
        <div class="card-body">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>사번</th><th>이름</th><th>이메일</th><th>전화번호</th>
                        <th>부서</th><th>직급</th><th>입사일</th><th>상태</th><th>관리</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    List<Employee> list = (List<Employee>) request.getAttribute("employeeList");
                    if (list != null) for (Employee emp : list) {
                %>
                <tr>
                    <td><%= emp.getEmpNo() %></td>
                    <td><%= emp.getName() %></td>
                    <td><%= emp.getEmail() %></td>
                    <td><%= emp.getPhone() %></td>
                    <td><%= emp.getDeptId() %></td>
                    <td><%= emp.getPosId() %></td>
                    <td><%= emp.getHireDate() %></td>
                    <td><span class="badge bg-success"><%= emp.getStatus() %></span></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/employee/detail?empId=<%= emp.getEmpId() %>"
                           class="btn btn-sm btn-outline-primary">상세</a>
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
