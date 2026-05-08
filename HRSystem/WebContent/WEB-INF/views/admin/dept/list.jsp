<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Department, model.Employee" %>
<%
    List<Department> deptList = (List<Department>) request.getAttribute("deptList");
    List<Employee> empList = (List<Employee>) request.getAttribute("empList");
    String error = request.getParameter("error");
%>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4>부서 관리</h4>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addModal">+ 부서 추가</button>
    </div>

    <% if ("hasEmployees".equals(error)) { %>
    <div class="alert alert-danger alert-dismissible fade show">
        소속 직원이 있는 부서는 삭제할 수 없습니다.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>

    <div class="card">
        <div class="card-body">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr><th>부서명</th><th>부서장</th><th>직원 수</th><th>관리</th></tr>
                </thead>
                <tbody>
                <% if (deptList != null) for (Department dept : deptList) { %>
                <tr>
                    <td><%= dept.getDeptName() %></td>
                    <td><%= dept.getManagerName() != null ? dept.getManagerName() : "-" %></td>
                    <td><%= dept.getEmpCount() %>명</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary btn-edit"
                            data-dept-id="<%= dept.getDeptId() %>"
                            data-dept-name="<%= dept.getDeptName() %>"
                            data-manager-id="<%= dept.getManagerId() %>"
                            data-bs-toggle="modal" data-bs-target="#editModal">수정</button>
                        <form action="${pageContext.request.contextPath}/dept/delete" method="post" class="d-inline"
                              onsubmit="return confirm('<%= dept.getDeptName() %>을(를) 삭제하시겠습니까?')">
                            <input type="hidden" name="deptId" value="<%= dept.getDeptId() %>">
                            <button type="submit" class="btn btn-sm btn-outline-danger">삭제</button>
                        </form>
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

<!-- 추가 모달 -->
<div class="modal fade" id="addModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">부서 추가</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form action="${pageContext.request.contextPath}/dept/create" method="post">
      <div class="modal-body">
          <div class="mb-3">
              <label class="form-label">부서명</label>
              <input type="text" name="deptName" class="form-control" required>
          </div>
          <div class="mb-3">
              <label class="form-label">부서장</label>
              <select name="managerId" class="form-select">
                  <option value="0">선택 안 함</option>
                  <% if (empList != null) for (Employee emp : empList) { %>
                  <option value="<%= emp.getEmpId() %>"><%= emp.getName() %> (<%= emp.getEmpNo() %>)</option>
                  <% } %>
              </select>
          </div>
      </div>
      <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
          <button type="submit" class="btn btn-primary">추가</button>
      </div>
      </form>
    </div>
  </div>
</div>

<!-- 수정 모달 -->
<div class="modal fade" id="editModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">부서 수정</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form action="${pageContext.request.contextPath}/dept/update" method="post">
      <input type="hidden" name="deptId" id="editDeptId">
      <div class="modal-body">
          <div class="mb-3">
              <label class="form-label">부서명</label>
              <input type="text" name="deptName" id="editDeptName" class="form-control" required>
          </div>
          <div class="mb-3">
              <label class="form-label">부서장</label>
              <select name="managerId" id="editManagerId" class="form-select">
                  <option value="0">선택 안 함</option>
                  <% if (empList != null) for (Employee emp : empList) { %>
                  <option value="<%= emp.getEmpId() %>"><%= emp.getName() %> (<%= emp.getEmpNo() %>)</option>
                  <% } %>
              </select>
          </div>
      </div>
      <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
          <button type="submit" class="btn btn-primary">저장</button>
      </div>
      </form>
    </div>
  </div>
</div>

<script>
document.querySelectorAll('.btn-edit').forEach(function(btn) {
    btn.addEventListener('click', function() {
        document.getElementById('editDeptId').value = this.dataset.deptId;
        document.getElementById('editDeptName').value = this.dataset.deptName;
        document.getElementById('editManagerId').value = this.dataset.managerId;
    });
});
</script>
<%@ include file="/common/footer.jsp" %>
