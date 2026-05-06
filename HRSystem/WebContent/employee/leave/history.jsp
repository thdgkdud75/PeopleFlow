<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.LeaveRequest" %>
<%@ include file="/common/header.jsp" %>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4>내 휴가 내역</h4>
        <a href="${pageContext.request.contextPath}/employee/leave/apply.jsp" class="btn btn-primary">+ 휴가 신청</a>
    </div>
    <div class="card">
        <div class="card-body">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr><th>유형</th><th>시작일</th><th>종료일</th><th>일수</th><th>사유</th><th>상태</th></tr>
                </thead>
                <tbody>
                <%
                    List<LeaveRequest> list = (List<LeaveRequest>) request.getAttribute("leaveList");
                    if (list != null && list.isEmpty()) {
                %>
                <tr><td colspan="6" class="text-center text-muted">휴가 신청 내역이 없습니다.</td></tr>
                <%
                    } else if (list != null) for (LeaveRequest lr : list) {
                        String badge = "APPROVED".equals(lr.getStatus()) ? "bg-success" :
                                       "REJECTED".equals(lr.getStatus()) ? "bg-danger" : "bg-warning";
                        String label = "APPROVED".equals(lr.getStatus()) ? "승인" :
                                       "REJECTED".equals(lr.getStatus()) ? "반려" : "대기";
                %>
                <tr>
                    <td><%= lr.getLeaveType() %></td>
                    <td><%= lr.getStartDate() %></td>
                    <td><%= lr.getEndDate() %></td>
                    <td><%= lr.getLeaveDays() %>일</td>
                    <td><%= lr.getReason() %></td>
                    <td><span class="badge <%= badge %>"><%= label %></span></td>
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
