<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/header.jsp" %>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <h4 class="mb-4">AI 인사 평가서</h4>
    <div class="card">
        <div class="card-body">
            <div class="d-flex align-items-center mb-3">
                <span class="badge bg-info me-2">AI 생성</span>
                <small class="text-muted">Claude AI가 평가 데이터를 분석하여 작성한 평가서입니다</small>
            </div>
            <div class="p-4 bg-light rounded" style="white-space: pre-wrap; line-height: 1.9; font-size: 0.95rem;">
${aiReport}
            </div>
            <div class="mt-3">
                <form action="${pageContext.request.contextPath}/ai/eval-report/save" method="post">
                    <input type="hidden" name="evalId" value="${evalId}">
                    <input type="hidden" name="aiReport" value="${aiReport}">
                    <button type="submit" class="btn btn-primary">평가서 저장</button>
                    <a href="${pageContext.request.contextPath}/eval/list" class="btn btn-secondary ms-2">목록으로</a>
                </form>
            </div>
        </div>
    </div>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
