<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/header.jsp" %>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <h4 class="mb-4">대시보드</h4>
    <div class="row g-4">
        <div class="col-md-3">
            <div class="card p-3 text-center">
                <h6 class="text-muted">전체 직원</h6>
                <h2 class="text-primary fw-bold">-</h2>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card p-3 text-center">
                <h6 class="text-muted">금일 출근</h6>
                <h2 class="text-success fw-bold">-</h2>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card p-3 text-center">
                <h6 class="text-muted">대기중 휴가</h6>
                <h2 class="text-warning fw-bold">-</h2>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card p-3 text-center">
                <h6 class="text-muted">이번달 급여</h6>
                <h2 class="text-danger fw-bold">-</h2>
            </div>
        </div>
    </div>
    <div class="row mt-4">
        <div class="col-md-6">
            <div class="card p-3">
                <h6 class="mb-3">빠른 메뉴</h6>
                <div class="d-grid gap-2">
                    <a href="${pageContext.request.contextPath}/employee/list" class="btn btn-outline-primary">직원 목록</a>
                    <a href="${pageContext.request.contextPath}/leave/pending" class="btn btn-outline-warning">휴가 승인 대기</a>
                    <a href="${pageContext.request.contextPath}/eval/list" class="btn btn-outline-info">인사 평가 관리</a>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
