<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Evaluation" %>
<%@ include file="/common/header.jsp" %>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4>인사 평가</h4>
        <form action="${pageContext.request.contextPath}/eval/list" method="get" class="d-flex gap-2">
            <input type="text" name="period" class="form-control" placeholder="e.g. 2025-H1" value="${period}">
            <button type="submit" class="btn btn-primary">조회</button>
        </form>
    </div>
    <div class="card">
        <div class="card-body">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr><th>직원</th><th>기간</th><th>점수</th><th>등급</th><th>코멘트</th><th>AI 평가서</th></tr>
                </thead>
                <tbody>
                <%
                    List<Evaluation> list = (List<Evaluation>) request.getAttribute("evalList");
                    if (list != null) for (Evaluation ev : list) {
                        String gradeBadge = "S".equals(ev.getGrade()) || "A".equals(ev.getGrade()) ? "bg-success" :
                                            "B".equals(ev.getGrade()) ? "bg-primary" :
                                            "C".equals(ev.getGrade()) ? "bg-warning" : "bg-danger";
                %>
                <tr>
                    <td><%= ev.getEmpName() %></td>
                    <td><%= ev.getEvalPeriod() %></td>
                    <td><%= ev.getScore() %></td>
                    <td><span class="badge <%= gradeBadge %>"><%= ev.getGrade() %></span></td>
                    <td><%= ev.getComments() != null ? ev.getComments() : "-" %></td>
                    <td>
                        <form action="${pageContext.request.contextPath}/ai/eval-report" method="post" class="d-inline">
                            <input type="hidden" name="evalId" value="<%= ev.getEvalId() %>">
                            <button type="submit" class="btn btn-sm btn-outline-info">AI 생성</button>
                        </form>
                    </td>
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
