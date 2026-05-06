<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Attendance" %>
<%@ include file="/common/header.jsp" %>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4>근태 관리</h4>
        <form action="${pageContext.request.contextPath}/attendance/list" method="get" class="d-flex gap-2">
            <input type="month" name="month" class="form-control" value="${month}">
            <button type="submit" class="btn btn-primary">조회</button>
        </form>
    </div>
    <div class="mb-3">
        <form action="${pageContext.request.contextPath}/ai/attendance-analysis" method="post">
            <input type="hidden" name="month" value="${month}">
            <button type="submit" class="btn btn-outline-info">AI 근태 분석 리포트 생성</button>
        </form>
    </div>
    <div class="card">
        <div class="card-body">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr><th>이름</th><th>날짜</th><th>출근</th><th>퇴근</th><th>상태</th></tr>
                </thead>
                <tbody>
                <%
                    List<Attendance> list = (List<Attendance>) request.getAttribute("attendanceList");
                    if (list != null) for (Attendance att : list) {
                        String badgeClass = "NORMAL".equals(att.getStatus()) ? "bg-success" :
                                            "LATE".equals(att.getStatus()) ? "bg-warning" : "bg-danger";
                %>
                <tr>
                    <td><%= att.getEmpName() %></td>
                    <td><%= att.getAttDate() %></td>
                    <td><%= att.getCheckIn() != null ? att.getCheckIn() : "-" %></td>
                    <td><%= att.getCheckOut() != null ? att.getCheckOut() : "-" %></td>
                    <td><span class="badge <%= badgeClass %>"><%= att.getStatus() %></span></td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
