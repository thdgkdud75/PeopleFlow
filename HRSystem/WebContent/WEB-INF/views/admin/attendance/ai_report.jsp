<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4>AI 근태 분석 리포트</h4>
        <span class="badge bg-info fs-6">${month}</span>
    </div>
    <div class="card">
        <div class="card-body">
            <div class="d-flex align-items-center mb-3">
                <span class="badge bg-primary me-2">AI 분석</span>
                <small class="text-muted">EXAONE AI가 분석한 결과입니다</small>
            </div>
            <div class="p-3 bg-light rounded" style="white-space: pre-wrap; line-height: 1.8;">
${aiReport}
            </div>
        </div>
    </div>
    <a href="${pageContext.request.contextPath}/attendance/list" class="btn btn-secondary mt-3">목록으로</a>
</div>
<%@ include file="/common/footer.jsp" %>
