<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.Employee, util.SessionUtil" %>
<%
    Employee navUser = SessionUtil.getLoginUser(request);
    boolean isAdmin = navUser != null && "ADMIN".equals(navUser.getRole());
    String currentUri = request.getRequestURI();
%>
<div class="sidebar">
    <% if (isAdmin) { %>
    <div class="sidebar-section-label">관리자</div>
    <a class="nav-link <%= currentUri.contains("/admin/dashboard") ? "active" : "" %>"
       href="${pageContext.request.contextPath}/admin/dashboard">
        <i class="bi bi-grid-1x2"></i> 대시보드
    </a>
    <a class="nav-link <%= currentUri.contains("/employee/list") || currentUri.contains("/employee/register") || currentUri.contains("/employee/detail") ? "active" : "" %>"
       href="${pageContext.request.contextPath}/employee/list">
        <i class="bi bi-people"></i> 직원 관리
    </a>
    <a class="nav-link <%= currentUri.contains("/dept/") ? "active" : "" %>"
       href="${pageContext.request.contextPath}/dept/list">
        <i class="bi bi-diagram-3"></i> 부서 관리
    </a>
    <a class="nav-link <%= currentUri.contains("/position/") ? "active" : "" %>"
       href="${pageContext.request.contextPath}/position/list">
        <i class="bi bi-award"></i> 직급 관리
    </a>
    <a class="nav-link <%= currentUri.contains("/attendance/list") ? "active" : "" %>"
       href="${pageContext.request.contextPath}/attendance/list">
        <i class="bi bi-clock-history"></i> 근태 관리
    </a>
    <a class="nav-link <%= currentUri.contains("/salary/list") ? "active" : "" %>"
       href="${pageContext.request.contextPath}/salary/list">
        <i class="bi bi-cash-stack"></i> 급여 관리
    </a>
    <a class="nav-link <%= currentUri.contains("/leave/pending") || currentUri.contains("/leave/list") ? "active" : "" %>"
       href="${pageContext.request.contextPath}/leave/pending">
        <i class="bi bi-calendar-check"></i> 휴가 승인
    </a>
    <a class="nav-link <%= currentUri.contains("/eval/") ? "active" : "" %>"
       href="${pageContext.request.contextPath}/eval/list">
        <i class="bi bi-star"></i> 인사 평가
    </a>
    <% } %>
    <hr>
    <div class="sidebar-section-label">직원</div>
    <a class="nav-link <%= currentUri.contains("/employee/mypage") ? "active" : "" %>"
       href="${pageContext.request.contextPath}/employee/mypage">
        <i class="bi bi-person-circle"></i> 마이페이지
    </a>
    <a class="nav-link <%= currentUri.contains("/attendance/my") ? "active" : "" %>"
       href="${pageContext.request.contextPath}/attendance/my">
        <i class="bi bi-clock"></i> 내 근태
    </a>
    <a class="nav-link <%= currentUri.contains("/salary/my") ? "active" : "" %>"
       href="${pageContext.request.contextPath}/salary/my">
        <i class="bi bi-wallet2"></i> 내 급여
    </a>
    <a class="nav-link <%= currentUri.contains("/leave/history") || currentUri.contains("/leave/apply") ? "active" : "" %>"
       href="${pageContext.request.contextPath}/leave/history">
        <i class="bi bi-umbrella"></i> 휴가 내역
    </a>
</div>
