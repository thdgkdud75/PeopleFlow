<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Employee, model.Department, model.Position" %>
<%
    List<Employee>   empList   = (List<Employee>)   request.getAttribute("employeeList");
    List<Department> deptList  = (List<Department>) request.getAttribute("deptList");
    List<Position>   posList   = (List<Position>)   request.getAttribute("posList");
    int  totalCount  = request.getAttribute("totalCount")  != null ? (Integer) request.getAttribute("totalCount")  : 0;
    int  totalPages  = request.getAttribute("totalPages")  != null ? (Integer) request.getAttribute("totalPages")  : 1;
    int  currentPage = request.getAttribute("currentPage") != null ? (Integer) request.getAttribute("currentPage") : 1;
    String keyword     = request.getAttribute("keyword")      != null ? (String) request.getAttribute("keyword")      : "";
    int    filterDept  = request.getAttribute("filterDept")   != null ? (Integer) request.getAttribute("filterDept")  : 0;
    int    filterPos   = request.getAttribute("filterPos")    != null ? (Integer) request.getAttribute("filterPos")   : 0;
    String filterStatus = request.getAttribute("filterStatus") != null ? (String) request.getAttribute("filterStatus") : "";

    // 페이징 URL에 검색 파라미터 유지
    StringBuilder baseQ = new StringBuilder("?");
    if (!keyword.isEmpty())      baseQ.append("keyword=").append(java.net.URLEncoder.encode(keyword, "UTF-8")).append("&");
    if (filterDept > 0)          baseQ.append("deptId=").append(filterDept).append("&");
    if (filterPos  > 0)          baseQ.append("posId=").append(filterPos).append("&");
    if (!filterStatus.isEmpty()) baseQ.append("status=").append(filterStatus).append("&");
    String bq = baseQ.toString();
%>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4>직원 관리 <span class="fs-6 text-muted fw-normal">총 <%= totalCount %>명</span></h4>
        <a href="${pageContext.request.contextPath}/employee/register" class="btn btn-primary">+ 직원 등록</a>
    </div>

    <%-- 검색 폼 --%>
    <div class="card mb-3">
        <div class="card-body py-3">
            <form method="get" action="${pageContext.request.contextPath}/employee/list" class="row g-2 align-items-end">
                <div class="col-md-3">
                    <label class="form-label small mb-1">이름 / 사번</label>
                    <input type="text" name="keyword" class="form-control form-control-sm"
                           placeholder="이름 또는 사번 검색" value="<%= keyword %>">
                </div>
                <div class="col-md-2">
                    <label class="form-label small mb-1">부서</label>
                    <select name="deptId" class="form-select form-select-sm">
                        <option value="0">전체 부서</option>
                        <% if (deptList != null) for (Department d : deptList) { %>
                        <option value="<%= d.getDeptId() %>" <%= d.getDeptId() == filterDept ? "selected" : "" %>><%= d.getDeptName() %></option>
                        <% } %>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label small mb-1">직급</label>
                    <select name="posId" class="form-select form-select-sm">
                        <option value="0">전체 직급</option>
                        <% if (posList != null) for (Position p : posList) { %>
                        <option value="<%= p.getPosId() %>" <%= p.getPosId() == filterPos ? "selected" : "" %>><%= p.getPosName() %></option>
                        <% } %>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label small mb-1">상태</label>
                    <select name="status" class="form-select form-select-sm">
                        <option value=""    <%= "".equals(filterStatus)        ? "selected" : "" %>>재직+휴직</option>
                        <option value="ACTIVE"   <%= "ACTIVE".equals(filterStatus)   ? "selected" : "" %>>재직</option>
                        <option value="ON_LEAVE" <%= "ON_LEAVE".equals(filterStatus) ? "selected" : "" %>>휴직</option>
                        <option value="ALL"      <%= "ALL".equals(filterStatus)      ? "selected" : "" %>>전체(퇴직포함)</option>
                    </select>
                </div>
                <div class="col-md-2 d-flex gap-1">
                    <button type="submit" class="btn btn-primary btn-sm">검색</button>
                    <a href="${pageContext.request.contextPath}/employee/list" class="btn btn-outline-secondary btn-sm">초기화</a>
                </div>
            </form>
        </div>
    </div>

    <%-- 직원 테이블 --%>
    <div class="card">
        <div class="card-body p-0">
            <table class="table table-hover mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-3">사번</th><th>이름</th><th>이메일</th><th>전화번호</th>
                        <th>부서</th><th>직급</th><th>입사일</th><th>상태</th><th>관리</th>
                    </tr>
                </thead>
                <tbody>
                <% if (empList == null || empList.isEmpty()) { %>
                <tr><td colspan="9" class="text-center text-muted py-4">검색 결과가 없습니다.</td></tr>
                <% } else { for (Employee emp : empList) {
                       String badge = "ACTIVE".equals(emp.getStatus()) ? "bg-success" :
                                      "ON_LEAVE".equals(emp.getStatus()) ? "bg-warning text-dark" : "bg-secondary";
                       String statusLabel = "ACTIVE".equals(emp.getStatus()) ? "재직" :
                                            "ON_LEAVE".equals(emp.getStatus()) ? "휴직" : "퇴직";
                %>
                <tr>
                    <td class="ps-3"><%= emp.getEmpNo() %></td>
                    <td><%= emp.getName() %></td>
                    <td><%= emp.getEmail() != null ? emp.getEmail() : "" %></td>
                    <td><%= emp.getPhone() != null ? emp.getPhone() : "" %></td>
                    <td><%= emp.getDeptName() != null ? emp.getDeptName() : "-" %></td>
                    <td><%= emp.getPosName()  != null ? emp.getPosName()  : "-" %></td>
                    <td><%= emp.getHireDate() %></td>
                    <td><span class="badge <%= badge %>"><%= statusLabel %></span></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/employee/detail?empId=<%= emp.getEmpId() %>"
                           class="btn btn-sm btn-outline-primary">상세</a>
                    </td>
                </tr>
                <% } } %>
                </tbody>
            </table>
        </div>
    </div>

    <%-- 페이징 --%>
    <% if (totalPages > 1) { %>
    <nav class="mt-3 d-flex justify-content-center">
        <ul class="pagination pagination-sm mb-0">
            <li class="page-item <%= currentPage == 1 ? "disabled" : "" %>">
                <a class="page-link" href="${pageContext.request.contextPath}/employee/list<%= bq %>page=<%= currentPage - 1 %>">이전</a>
            </li>
            <%
                int startPage = Math.max(1, currentPage - 2);
                int endPage   = Math.min(totalPages, startPage + 4);
                if (endPage - startPage < 4) startPage = Math.max(1, endPage - 4);
                for (int i = startPage; i <= endPage; i++) {
            %>
            <li class="page-item <%= i == currentPage ? "active" : "" %>">
                <a class="page-link" href="${pageContext.request.contextPath}/employee/list<%= bq %>page=<%= i %>"><%= i %></a>
            </li>
            <% } %>
            <li class="page-item <%= currentPage == totalPages ? "disabled" : "" %>">
                <a class="page-link" href="${pageContext.request.contextPath}/employee/list<%= bq %>page=<%= currentPage + 1 %>">다음</a>
            </li>
        </ul>
    </nav>
    <% } %>

</div>
<%@ include file="/common/footer.jsp" %>
