<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Evaluation" %>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4 class="page-title mb-0">인사 평가</h4>
        <div class="d-flex gap-2">
            <form action="${pageContext.request.contextPath}/eval/list" method="get" class="d-flex gap-2">
                <input type="text" name="period" class="form-control form-control-sm"
                       placeholder="기간 (예: 2025-H2)" value="${period}" style="width:160px;">
                <button type="submit" class="btn btn-outline-primary btn-sm">조회</button>
                <a href="${pageContext.request.contextPath}/eval/list" class="btn btn-outline-secondary btn-sm">전체</a>
            </form>
            <button class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#evalModal">
                <i class="bi bi-plus-lg me-1"></i>평가 등록
            </button>
        </div>
    </div>

    <%-- AI 평가서 결과 표시 --%>
    <%
        String aiReport = (String) request.getAttribute("aiReport");
        Integer savedEvalId = (Integer) request.getAttribute("evalId");
        if (aiReport != null) {
    %>
    <div class="card mb-4" style="border-left: 4px solid #1E6FFF !important;">
        <div class="card-body">
            <div class="d-flex align-items-center mb-3">
                <span class="badge bg-primary me-2"><i class="bi bi-stars"></i> Gemini AI 생성</span>
                <span class="text-muted small">AI가 평가 데이터를 분석하여 작성한 인사 평가서입니다.</span>
                <form action="${pageContext.request.contextPath}/ai/eval-report/save" method="post" class="ms-auto">
                    <input type="hidden" name="evalId" value="${evalId}">
                    <input type="hidden" name="aiReport" value="${aiReport}">
                    <button type="submit" class="btn btn-sm btn-primary">
                        <i class="bi bi-save me-1"></i>평가서 저장
                    </button>
                </form>
            </div>
            <div class="p-4 rounded-3" style="background:#F8FAFF; white-space:pre-wrap; line-height:1.9; font-size:0.9rem; border:1px solid #EEF4FF;">
${aiReport}
            </div>
        </div>
    </div>
    <% } %>

    <div class="card">
        <div class="card-body p-0">
            <table class="table mb-0">
                <thead>
                    <tr>
                        <th class="ps-4">직원</th>
                        <th>기간</th>
                        <th class="text-center">점수</th>
                        <th class="text-center">등급</th>
                        <th>평가 코멘트</th>
                        <th class="text-center">AI 평가서</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    List<Evaluation> list = (List<Evaluation>) request.getAttribute("evalList");
                    if (list == null || list.isEmpty()) {
                %>
                <tr>
                    <td colspan="6" class="text-center py-5">
                        <i class="bi bi-clipboard-x text-muted" style="font-size:2rem;display:block;margin-bottom:8px;"></i>
                        <span class="text-muted">평가 데이터가 없습니다.</span>
                    </td>
                </tr>
                <%
                    } else { for (Evaluation ev : list) {
                        String gradeBadge = "S".equals(ev.getGrade()) ? "bg-success" :
                                            "A".equals(ev.getGrade()) ? "bg-primary" :
                                            "B".equals(ev.getGrade()) ? "bg-info" :
                                            "C".equals(ev.getGrade()) ? "bg-warning" : "bg-danger";
                %>
                <tr>
                    <td class="ps-4">
                        <div class="d-flex align-items-center gap-2">
                            <div style="width:30px;height:30px;border-radius:50%;background:linear-gradient(135deg,#1E6FFF,#6CA8FF);display:flex;align-items:center;justify-content:center;color:white;font-size:0.75rem;font-weight:700;">
                                <%= ev.getEmpName().charAt(0) %>
                            </div>
                            <%= ev.getEmpName() %>
                        </div>
                    </td>
                    <td><span class="badge bg-secondary"><%= ev.getEvalPeriod() %></span></td>
                    <td class="text-center">
                        <span class="fw-bold" style="font-size:1rem;"><%= ev.getScore() %></span>
                        <span class="text-muted small">점</span>
                    </td>
                    <td class="text-center">
                        <span class="badge <%= gradeBadge %>" style="font-size:0.85rem;padding:5px 10px;">
                            <%= ev.getGrade() %>
                        </span>
                    </td>
                    <td class="text-muted small" style="max-width:300px;">
                        <%= ev.getComments() != null ? ev.getComments() : "-" %>
                    </td>
                    <td class="text-center">
                        <% if (ev.getAiReport() != null && !ev.getAiReport().isBlank()) { %>
                        <span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>완료</span>
                        <% } else { %>
                        <form action="${pageContext.request.contextPath}/ai/eval-report" method="post" class="d-inline">
                            <input type="hidden" name="evalId" value="<%= ev.getEvalId() %>">
                            <button type="submit" class="btn btn-sm btn-outline-primary">
                                <i class="bi bi-stars me-1"></i>AI 생성
                            </button>
                        </form>
                        <% } %>
                    </td>
                </tr>
                <% } } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%-- 평가 등록 모달 --%>
<div class="modal fade" id="evalModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">인사 평가 등록</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <form action="${pageContext.request.contextPath}/eval/create" method="post">
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label">직원 ID</label>
          <input type="number" name="empId" class="form-control" placeholder="직원 ID" required>
        </div>
        <div class="mb-3">
          <label class="form-label">평가 기간</label>
          <input type="text" name="evalPeriod" class="form-control" placeholder="예: 2026-H1" required>
        </div>
        <div class="row g-2 mb-3">
          <div class="col-6">
            <label class="form-label">점수 (0~100)</label>
            <input type="number" name="score" class="form-control" min="0" max="100" required>
          </div>
          <div class="col-6">
            <label class="form-label">등급</label>
            <select name="grade" class="form-select" required>
              <option value="S">S</option>
              <option value="A">A</option>
              <option value="B" selected>B</option>
              <option value="C">C</option>
              <option value="D">D</option>
            </select>
          </div>
        </div>
        <div class="mb-3">
          <label class="form-label">평가 코멘트</label>
          <textarea name="comments" class="form-control" rows="3"></textarea>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">취소</button>
        <button type="submit" class="btn btn-primary">등록</button>
      </div>
      </form>
    </div>
  </div>
</div>

<%@ include file="/common/footer.jsp" %>
