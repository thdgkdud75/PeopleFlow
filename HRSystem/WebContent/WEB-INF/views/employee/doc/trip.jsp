<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.BusinessTrip" %>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4>출장보고서</h4>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#writeModal">
            <i class="bi bi-plus-lg"></i> 출장보고서 작성
        </button>
    </div>

    <div class="row g-3">
    <%
        List<BusinessTrip> trips = (List<BusinessTrip>) request.getAttribute("trips");
        if (trips != null) for (BusinessTrip t : trips) {
    %>
    <div class="col-12">
        <div class="card">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <h6 class="mb-1"><%= t.getDestination() %></h6>
                        <span class="badge bg-secondary me-2"><%= t.getTripStart() %> ~ <%= t.getTripEnd() %></span>
                        <small class="text-muted">목적: <%= t.getPurpose() %></small>
                    </div>
                    <% if (t.getAiSummary() == null || t.getAiSummary().isBlank()) { %>
                    <form action="${pageContext.request.contextPath}/doc/trip/summarize" method="post" class="d-inline">
                        <input type="hidden" name="tripId" value="<%= t.getTripId() %>">
                        <button class="btn btn-sm btn-outline-primary">
                            <i class="bi bi-stars"></i> AI 요약
                        </button>
                    </form>
                    <% } %>
                </div>
                <div class="mt-2">
                    <p class="mb-1 fw-semibold">출장 내용</p>
                    <div class="p-2 bg-light rounded" style="white-space:pre-wrap;"><%= t.getContent() %></div>
                </div>
                <% if (t.getAiSummary() != null && !t.getAiSummary().isBlank()) { %>
                <div class="mt-2">
                    <p class="mb-1 fw-semibold text-primary"><i class="bi bi-stars"></i> AI 요약</p>
                    <div class="p-2 border border-primary rounded" style="white-space:pre-wrap;"><%= t.getAiSummary() %></div>
                </div>
                <% } %>
            </div>
        </div>
    </div>
    <% } %>
    <% if (trips == null || trips.isEmpty()) { %>
    <div class="col-12 text-center text-muted py-5">등록된 출장보고서가 없습니다.</div>
    <% } %>
    </div>
</div>

<!-- 작성 모달 -->
<div class="modal fade" id="writeModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/doc/trip/save" method="post">
                <div class="modal-header">
                    <h5 class="modal-title">출장보고서 작성</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label">출장 시작일</label>
                            <input type="date" name="tripStart" class="form-control" required
                                   value="<%= java.time.LocalDate.now() %>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">출장 종료일</label>
                            <input type="date" name="tripEnd" class="form-control" required
                                   value="<%= java.time.LocalDate.now() %>">
                        </div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label">출장지</label>
                            <input type="text" name="destination" class="form-control" required
                                   placeholder="예: 부산 본사">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">출장 목적</label>
                            <input type="text" name="purpose" class="form-control" required
                                   placeholder="예: 고객사 미팅 및 계약 협의">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">출장 내용</label>
                        <textarea name="content" class="form-control" rows="8" required
                                  placeholder="출장 중 수행한 업무 내용을 상세히 입력하세요."></textarea>
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
