<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.LeaveRequest" %>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">
    <h4 class="mb-4">휴가 신청 내역</h4>
    <div class="card">
        <div class="card-body">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr><th>직원</th><th>유형</th><th>시작일</th><th>종료일</th><th>일수</th><th>사유</th><th>상태</th></tr>
                </thead>
                <tbody>
                <%
                    List<LeaveRequest> list = (List<LeaveRequest>) request.getAttribute("leaveList");
                    if (list != null) for (LeaveRequest lr : list) {
                        String badge = "APPROVED".equals(lr.getStatus()) ? "bg-success" :
                                       "REJECTED".equals(lr.getStatus()) ? "bg-danger" : "bg-warning";
                %>
                <tr>
                    <td><%= lr.getEmpName() %></td>
                    <td><%= lr.getLeaveType() %></td>
                    <td><%= lr.getStartDate() %></td>
                    <td><%= lr.getEndDate() %></td>
                    <td><%= lr.getLeaveDays() %>일</td>
                    <td><%= lr.getReason() %></td>
                    <td><span class="badge <%= badge %>"><%= lr.getStatus() %></span></td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
