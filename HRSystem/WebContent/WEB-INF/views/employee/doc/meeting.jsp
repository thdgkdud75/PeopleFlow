<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.MeetingMinutes" %>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4>회의록</h4>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#writeModal">
            <i class="bi bi-plus-lg"></i> 회의록 작성
        </button>
    </div>

    <div class="row g-3">
    <%
        List<MeetingMinutes> meetings = (List<MeetingMinutes>) request.getAttribute("meetings");
        if (meetings != null) for (MeetingMinutes m : meetings) {
    %>
    <div class="col-12">
        <div class="card">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <h6 class="mb-1"><%= m.getTitle() %></h6>
                        <span class="badge bg-secondary me-2"><%= m.getMeetingDate() %></span>
                        <% if (m.getAttendees() != null && !m.getAttendees().isBlank()) { %>
                        <small class="text-muted">참석자: <%= m.getAttendees() %></small>
                        <% } %>
                    </div>
                    <% if (m.getAiSummary() == null || m.getAiSummary().isBlank()) { %>
                    <form action="${pageContext.request.contextPath}/doc/meeting/summarize" method="post" class="d-inline">
                        <input type="hidden" name="meetingId" value="<%= m.getMeetingId() %>">
                        <button class="btn btn-sm btn-outline-primary">
                            <i class="bi bi-stars"></i> AI 요약
                        </button>
                    </form>
                    <% } %>
                </div>
                <div class="mt-2">
                    <p class="mb-1 fw-semibold">회의 내용</p>
                    <div class="p-2 bg-light rounded" style="white-space:pre-wrap;"><%= m.getContent() %></div>
                </div>
                <% if (m.getAiSummary() != null && !m.getAiSummary().isBlank()) { %>
                <div class="mt-2">
                    <p class="mb-1 fw-semibold text-primary"><i class="bi bi-stars"></i> AI 정리</p>
                    <div class="p-2 border border-primary rounded" style="white-space:pre-wrap;"><%= m.getAiSummary() %></div>
                </div>
                <% } %>
            </div>
        </div>
    </div>
    <% } %>
    <% if (meetings == null || meetings.isEmpty()) { %>
    <div class="col-12 text-center text-muted py-5">등록된 회의록이 없습니다.</div>
    <% } %>
    </div>
</div>

<!-- 작성 모달 -->
<div class="modal fade" id="writeModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/doc/meeting/save" method="post">
                <div class="modal-header">
                    <h5 class="modal-title">회의록 작성</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label">회의 제목</label>
                            <input type="text" name="title" class="form-control" required
                                   placeholder="예: 5월 개발팀 주간 회의">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">회의 날짜</label>
                            <input type="date" name="meetingDate" class="form-control" required
                                   value="<%= java.time.LocalDate.now() %>">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">참석자 <span class="text-muted small">(선택)</span></label>
                        <input type="text" name="attendees" class="form-control"
                               placeholder="예: 김철수, 이영희, 박민준">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">회의 내용</label>
                        <textarea name="content" class="form-control" rows="8" required
                                  placeholder="회의에서 논의된 내용을 상세히 입력하세요."></textarea>
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
