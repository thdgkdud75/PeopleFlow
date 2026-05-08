<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Department, model.Position" %>
<%
    List<Department> deptList = (List<Department>) request.getAttribute("deptList");
    List<Position> posList = (List<Position>) request.getAttribute("posList");
%>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">
    <h4 class="mb-4">직원 등록</h4>
    <div class="card">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/employee/register" method="post">
                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label">사번</label>
                        <input type="text" name="empNo" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">이름</label>
                        <input type="text" name="name" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">이메일</label>
                        <input type="email" name="email" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">전화번호</label>
                        <input type="tel" name="phone" class="form-control">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">부서</label>
                        <select name="deptId" class="form-select" required>
                            <option value="">선택하세요</option>
                            <% if (deptList != null) for (Department dept : deptList) { %>
                            <option value="<%= dept.getDeptId() %>"><%= dept.getDeptName() %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">직급</label>
                        <select name="posId" class="form-select" required>
                            <option value="">선택하세요</option>
                            <% if (posList != null) for (Position pos : posList) { %>
                            <option value="<%= pos.getPosId() %>"><%= pos.getPosName() %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">입사일</label>
                        <input type="date" name="hireDate" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">권한</label>
                        <select name="role" class="form-select">
                            <option value="EMPLOYEE">직원</option>
                            <option value="ADMIN">관리자</option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">초기 비밀번호</label>
                        <input type="password" name="password" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">상태</label>
                        <select name="status" class="form-select">
                            <option value="ACTIVE">재직</option>
                            <option value="ON_LEAVE">휴직</option>
                        </select>
                    </div>
                </div>
                <div class="mt-4">
                    <button type="submit" class="btn btn-primary">등록</button>
                    <a href="${pageContext.request.contextPath}/employee/list" class="btn btn-secondary ms-2">취소</a>
                </div>
            </form>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
