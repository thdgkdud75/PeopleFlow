<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Attendance" %>
<%@ include file="/common/header.jsp" %>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <h4 class="mb-4">내 근태 현황</h4>
    <div class="mb-3 d-flex gap-2">
        <form action="${pageContext.request.contextPath}/attendance/checkin" method="post">
            <button type="submit" class="btn btn-success">출근 체크</button>
        </form>
        <form action="${pageContext.request.contextPath}/attendance/checkout" method="post">
            <button type="submit" class="btn btn-warning">퇴근 체크</button>
        </form>
    </div>
    <div class="card">
        <div class="card-body">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr><th>날짜</th><th>출근</th><th>퇴근</th><th>상태</th></tr>
                </thead>
                <tbody>
                <%
                    List<Attendance> list = (List<Attendance>) request.getAttribute("attendanceList");
                    if (list != null) for (Attendance att : list) {
                        String badge = "NORMAL".equals(att.getStatus()) ? "bg-success" :
                                       "LATE".equals(att.getStatus()) ? "bg-warning" : "bg-danger";
                %>
                <tr>
                    <td><%= att.getAttDate() %></td>
                    <td><%= att.getCheckIn() != null ? att.getCheckIn() : "-" %></td>
                    <td><%= att.getCheckOut() != null ? att.getCheckOut() : "-" %></td>
                    <td><span class="badge <%= badge %>"><%= att.getStatus() %></span></td>
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
