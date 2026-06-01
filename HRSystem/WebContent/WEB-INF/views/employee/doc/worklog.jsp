<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.WorkLog" %>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4>업무일지</h4>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#writeModal">
            <i class="bi bi-plus-lg"></i> 업무일지 작성
        </button>
    </div>

    <div class="row g-3">
    <%
        List<WorkLog> logs = (List<WorkLog>) request.getAttribute("logs");
        if (logs != null) for (WorkLog log : logs) {
    %>
    <div class="col-12">
        <div class="card">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <span class="badge bg-secondary me-2"><%= log.getLogDate() %></span>
                        <small class="text-muted">작성일</small>
                    </div>
                    <% if (log.getAiSummary() == null || log.getAiSummary().isBlank()) { %>
                    <form action="${pageContext.request.contextPath}/doc/worklog/summarize" method="post" class="d-inline">
                        <input type="hidden" name="logId" value="<%= log.getLogId() %>">
                        <button class="btn btn-sm btn-outline-primary">
                            <i class="bi bi-stars"></i> AI 요약
                        </button>
                    </form>
                    <% } %>
                </div>
                <div class="mt-2">
                    <p class="mb-1 fw-semibold">원본 내용</p>
                    <div class="p-2 bg-light rounded" style="white-space:pre-wrap;"><%= log.getContent() %></div>
                </div>
                <% if (log.getAiSummary() != null && !log.getAiSummary().isBlank()) { %>
                <div class="mt-2">
                    <p class="mb-1 fw-semibold text-primary"><i class="bi bi-stars"></i> AI 요약</p>
                    <div class="p-2 border border-primary rounded" style="white-space:pre-wrap;"><%= log.getAiSummary() %></div>
                </div>
                <% } %>
            </div>
        </div>
    </div>
    <% } %>
    <% if (logs == null || logs.isEmpty()) { %>
    <div class="col-12 text-center text-muted py-5">등록된 업무일지가 없습니다.</div>
    <% } %>
    </div>
</div>

<!-- 작성 모달 -->
<div class="modal fade" id="writeModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/doc/worklog/save" method="post">
                <div class="modal-header">
                    <h5 class="modal-title">업무일지 작성</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">날짜</label>
                        <input type="date" name="logDate" class="form-control" required
                               value="<%= java.time.LocalDate.now() %>">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">업무 내용</label>
                        <textarea name="content" class="form-control" rows="8" required
                                  placeholder="오늘 수행한 업무 내용을 자세히 입력하세요."></textarea>
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
<%@ include file="/common/footer.jsp" %>
