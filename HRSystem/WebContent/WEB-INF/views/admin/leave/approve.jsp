<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.LeaveRequest" %>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">
    <h4 class="mb-4">휴가 승인 대기</h4>
    <div class="card">
        <div class="card-body">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr><th>직원</th><th>유형</th><th>기간</th><th>일수</th><th>사유</th><th>신청일</th><th>처리</th></tr>
                </thead>
                <tbody>
                <%
                    List<LeaveRequest> list = (List<LeaveRequest>) request.getAttribute("leaveList");
                    if (list != null && list.isEmpty()) {
                %>
                <tr><td colspan="7" class="text-center text-muted">승인 대기 중인 휴가가 없습니다.</td></tr>
                <%
                    } else if (list != null) for (LeaveRequest lr : list) {
                %>
                <tr>
                    <td><%= lr.getEmpName() %></td>
                    <td><%= lr.getLeaveType() %></td>
                    <td><%= lr.getStartDate() %> ~ <%= lr.getEndDate() %></td>
                    <td><%= lr.getLeaveDays() %>일</td>
                    <td><%= lr.getReason() %></td>
                    <td><%= lr.getAppliedAt() %></td>
                    <td>
                        <form action="${pageContext.request.contextPath}/leave/approve" method="post" class="d-inline">
                            <input type="hidden" name="leaveId" value="<%= lr.getLeaveId() %>">
                            <input type="hidden" name="action" value="APPROVE">
                            <button type="submit" class="btn btn-sm btn-success">승인</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/leave/approve" method="post" class="d-inline ms-1">
                            <input type="hidden" name="leaveId" value="<%= lr.getLeaveId() %>">
                            <input type="hidden" name="action" value="REJECT">
                            <button type="submit" class="btn btn-sm btn-danger">반려</button>
                        </form>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
