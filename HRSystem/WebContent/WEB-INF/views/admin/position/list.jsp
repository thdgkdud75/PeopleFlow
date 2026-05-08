<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Position" %>
<%
    List<Position> posList = (List<Position>) request.getAttribute("posList");
    String error = request.getParameter("error");
%>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4>직급 관리</h4>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addModal">+ 직급 추가</button>
    </div>

    <% if ("hasEmployees".equals(error)) { %>
    <div class="alert alert-danger alert-dismissible fade show">
        소속 직원이 있는 직급은 삭제할 수 없습니다.
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>

    <div class="card">
        <div class="card-body">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr><th>직급명</th><th>레벨</th><th>기본급</th><th>직원 수</th><th>관리</th></tr>
                </thead>
                <tbody>
                <% if (posList != null) for (Position pos : posList) { %>
                <tr>
                    <td><%= pos.getPosName() %></td>
                    <td><%= pos.getLevel() %></td>
                    <td><%= String.format("%,d", pos.getBaseSalary()) %>원</td>
                    <td><%= pos.getEmpCount() %>명</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary btn-edit"
                            data-pos-id="<%= pos.getPosId() %>"
                            data-pos-name="<%= pos.getPosName() %>"
                            data-level="<%= pos.getLevel() %>"
                            data-base-salary="<%= pos.getBaseSalary() %>"
                            data-bs-toggle="modal" data-bs-target="#editModal">수정</button>
                        <form action="${pageContext.request.contextPath}/position/delete" method="post" class="d-inline"
                              onsubmit="return confirm('<%= pos.getPosName() %>을(를) 삭제하시겠습니까?')">
                            <input type="hidden" name="posId" value="<%= pos.getPosId() %>">
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
        <h5 class="modal-title">직급 추가</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form action="${pageContext.request.contextPath}/position/create" method="post">
      <div class="modal-body">
          <div class="mb-3">
              <label class="form-label">직급명</label>
              <input type="text" name="posName" class="form-control" required>
          </div>
          <div class="mb-3">
              <label class="form-label">레벨 (숫자, 낮을수록 하위)</label>
              <input type="number" name="level" class="form-control" min="1" required>
          </div>
          <div class="mb-3">
              <label class="form-label">기본급 (원)</label>
              <input type="number" name="baseSalary" class="form-control" min="0" required>
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
        <h5 class="modal-title">직급 수정</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form action="${pageContext.request.contextPath}/position/update" method="post">
      <input type="hidden" name="posId" id="editPosId">
      <div class="modal-body">
          <div class="mb-3">
              <label class="form-label">직급명</label>
              <input type="text" name="posName" id="editPosName" class="form-control" required>
          </div>
          <div class="mb-3">
              <label class="form-label">레벨</label>
              <input type="number" name="level" id="editLevel" class="form-control" min="1" required>
          </div>
          <div class="mb-3">
              <label class="form-label">기본급 (원)</label>
              <input type="number" name="baseSalary" id="editBaseSalary" class="form-control" min="0" required>
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
        document.getElementById('editPosId').value = this.dataset.posId;
        document.getElementById('editPosName').value = this.dataset.posName;
        document.getElementById('editLevel').value = this.dataset.level;
        document.getElementById('editBaseSalary').value = this.dataset.baseSalary;
    });
});
</script>
<%@ include file="/common/footer.jsp" %>
