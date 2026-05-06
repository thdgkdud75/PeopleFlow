<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.Employee" %>
<%@ include file="/common/header.jsp" %>
<%
    Employee emp = (Employee) request.getAttribute("employee");
%>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <h4 class="mb-4">직원 상세</h4>
    <% if (emp != null) { %>
    <div class="card">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/employee/update" method="post">
                <input type="hidden" name="empId" value="<%= emp.getEmpId() %>">
                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label">사번</label>
                        <input type="text" class="form-control" value="<%= emp.getEmpNo() %>" readonly>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">이름</label>
                        <input type="text" name="name" class="form-control" value="<%= emp.getName() %>" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">이메일</label>
                        <input type="email" name="email" class="form-control" value="<%= emp.getEmail() %>">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">전화번호</label>
                        <input type="tel" name="phone" class="form-control" value="<%= emp.getPhone() %>">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">부서 ID</label>
                        <input type="number" name="deptId" class="form-control" value="<%= emp.getDeptId() %>">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">직급 ID</label>
                        <input type="number" name="posId" class="form-control" value="<%= emp.getPosId() %>">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">상태</label>
                        <select name="status" class="form-select">
                            <option value="ACTIVE" <%= "ACTIVE".equals(emp.getStatus()) ? "selected" : "" %>>재직</option>
                            <option value="ON_LEAVE" <%= "ON_LEAVE".equals(emp.getStatus()) ? "selected" : "" %>>휴직</option>
                            <option value="RESIGNED" <%= "RESIGNED".equals(emp.getStatus()) ? "selected" : "" %>>퇴직</option>
                        </select>
                    </div>
                </div>
                <div class="mt-4">
                    <button type="submit" class="btn btn-primary">수정</button>
                    <a href="${pageContext.request.contextPath}/employee/list" class="btn btn-secondary ms-2">목록</a>
                    <form action="${pageContext.request.contextPath}/employee/delete" method="post" class="d-inline ms-2"
                          onsubmit="return confirm('정말 퇴직 처리 하시겠습니까?')">
                        <input type="hidden" name="empId" value="<%= emp.getEmpId() %>">
                        <button type="submit" class="btn btn-danger">퇴직 처리</button>
                    </form>
                </div>
            </form>
        </div>
    </div>
    <% } %>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
