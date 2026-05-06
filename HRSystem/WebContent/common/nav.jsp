<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.Employee, util.SessionUtil" %>
<%
    Employee navUser = SessionUtil.getLoginUser(request);
    boolean isAdmin = navUser != null && "ADMIN".equals(navUser.getRole());
%>
<div class="sidebar col-md-2 p-0">
    <nav class="nav flex-column pt-3">
        <% if (isAdmin) { %>
        <span class="nav-link text-muted small px-3 pb-1">관리자</span>
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard.jsp">대시보드</a>
        <a class="nav-link" href="${pageContext.request.contextPath}/employee/list">직원 관리</a>
        <a class="nav-link" href="${pageContext.request.contextPath}/attendance/list">근태 관리</a>
        <a class="nav-link" href="${pageContext.request.contextPath}/salary/list">급여 관리</a>
        <a class="nav-link" href="${pageContext.request.contextPath}/leave/pending">휴가 승인</a>
        <a class="nav-link" href="${pageContext.request.contextPath}/eval/list">인사 평가</a>
        <% } %>
        <hr class="text-muted">
        <span class="nav-link text-muted small px-3 pb-1">직원</span>
        <a class="nav-link" href="${pageContext.request.contextPath}/employee/mypage.jsp">마이페이지</a>
        <a class="nav-link" href="${pageContext.request.contextPath}/attendance/my">내 근태</a>
        <a class="nav-link" href="${pageContext.request.contextPath}/salary/my">내 급여</a>
        <a class="nav-link" href="${pageContext.request.contextPath}/leave/history">휴가 내역</a>
        <a class="nav-link" href="${pageContext.request.contextPath}/employee/chatbot.jsp">HR 챗봇</a>
    </nav>
</div>
