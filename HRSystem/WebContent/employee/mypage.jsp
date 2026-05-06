<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.Employee" %>
<%@ include file="/common/header.jsp" %>
<%
    Employee me = SessionUtil.getLoginUser(request);
%>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <h4 class="mb-4">마이페이지</h4>
    <div class="row g-4">
        <div class="col-md-4">
            <div class="card p-4 text-center">
                <div class="rounded-circle bg-primary d-flex align-items-center justify-content-center mx-auto mb-3"
                     style="width:80px;height:80px;font-size:2rem;color:white;">
                    <%= me.getName().charAt(0) %>
                </div>
                <h5><%= me.getName() %></h5>
                <p class="text-muted"><%= me.getEmpNo() %></p>
                <span class="badge bg-primary"><%= me.getRole() %></span>
            </div>
        </div>
        <div class="col-md-8">
            <div class="card">
                <div class="card-body">
                    <h6 class="mb-3">내 정보</h6>
                    <table class="table">
                        <tr><th>이메일</th><td><%= me.getEmail() %></td></tr>
                        <tr><th>전화번호</th><td><%= me.getPhone() %></td></tr>
                        <tr><th>입사일</th><td><%= me.getHireDate() %></td></tr>
                        <tr><th>상태</th><td><%= me.getStatus() %></td></tr>
                    </table>
                </div>
            </div>
            <div class="row mt-3 g-3">
                <div class="col-4">
                    <a href="${pageContext.request.contextPath}/attendance/my" class="card p-3 text-center text-decoration-none text-dark d-block">
                        <div class="fs-4">📋</div>
                        <small>내 근태</small>
                    </a>
                </div>
                <div class="col-4">
                    <a href="${pageContext.request.contextPath}/salary/my" class="card p-3 text-center text-decoration-none text-dark d-block">
                        <div class="fs-4">💰</div>
                        <small>내 급여</small>
                    </a>
                </div>
                <div class="col-4">
                    <a href="${pageContext.request.contextPath}/leave/history" class="card p-3 text-center text-decoration-none text-dark d-block">
                        <div class="fs-4">🏖️</div>
                        <small>휴가 내역</small>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
