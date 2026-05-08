<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, model.Salary" %>
<%
    List<Salary> list    = (List<Salary>) request.getAttribute("salaryList");
    String month         = (String)  request.getAttribute("month");
    long totalBase       = request.getAttribute("totalBase")   != null ? (Long) request.getAttribute("totalBase")   : 0L;
    long totalAllow      = request.getAttribute("totalAllow")  != null ? (Long) request.getAttribute("totalAllow")  : 0L;
    long totalDeduct     = request.getAttribute("totalDeduct") != null ? (Long) request.getAttribute("totalDeduct") : 0L;
    long totalNet        = request.getAttribute("totalNet")    != null ? (Long) request.getAttribute("totalNet")    : 0L;
    int  pendingCount    = request.getAttribute("pendingCount")!= null ? (Integer) request.getAttribute("pendingCount") : 0;
    String msg = (String) session.getAttribute("msg");
    if (msg != null) session.removeAttribute("msg");
%>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">

    <%-- 상단 타이틀 + 월 선택 --%>
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4>급여 관리</h4>
        <form action="${pageContext.request.contextPath}/salary/list" method="get" class="d-flex gap-2">
            <input type="month" name="month" class="form-control" value="<%= month %>">
            <button type="submit" class="btn btn-outline-primary">조회</button>
        </form>
    </div>

    <%-- 알림 메시지 --%>
    <% if (msg != null) { %>
    <div class="alert alert-success alert-dismissible fade show">
        <%= msg %>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>

    <%-- 요약 카드 --%>
    <div class="row g-3 mb-3">
        <div class="col-md-3">
            <div class="card text-center p-3">
                <div class="text-muted small">대상 인원</div>
                <div class="fs-4 fw-bold"><%= list != null ? list.size() : 0 %>명</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center p-3">
                <div class="text-muted small">총 기본급 합계</div>
                <div class="fs-5 fw-bold"><%= String.format("%,d", totalBase) %>원</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center p-3">
                <div class="text-muted small">총 실수령액 합계</div>
                <div class="fs-5 fw-bold text-primary"><%= String.format("%,d", totalNet) %>원</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center p-3">
                <div class="text-muted small">지급 대기</div>
                <div class="fs-4 fw-bold text-warning"><%= pendingCount %>건</div>
            </div>
        </div>
    </div>

    <%-- 액션 버튼 --%>
    <div class="d-flex gap-2 mb-3">
        <form action="${pageContext.request.contextPath}/salary/generate" method="post">
            <input type="hidden" name="month" value="<%= month %>">
            <button type="submit" class="btn btn-primary"
                    onclick="return confirm('<%= month %> 급여를 직급 기본급 기준으로 일괄 생성하시겠습니까?')">
                ⚡ 이번달 급여 일괄 생성
            </button>
        </form>
        <% if (pendingCount > 0) { %>
        <form action="${pageContext.request.contextPath}/salary/pay-all" method="post">
            <input type="hidden" name="month" value="<%= month %>">
            <button type="submit" class="btn btn-success"
                    onclick="return confirm('<%= pendingCount %>건을 전체 지급 확정하시겠습니까?')">
                ✔ 전체 지급 확정 (<%= pendingCount %>건)
            </button>
        </form>
        <% } %>
    </div>

    <%-- 급여 테이블 --%>
    <div class="card">
        <div class="card-body p-0">
            <table class="table table-hover mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-3">이름</th>
                        <th>부서</th>
                        <th>직급</th>
                        <th class="text-end">기본급</th>
                        <th class="text-end">수당</th>
                        <th class="text-end">공제</th>
                        <th class="text-end">실수령액</th>
                        <th class="text-center">상태</th>
                        <th class="text-center">관리</th>
                    </tr>
                </thead>
                <tbody>
                <% if (list == null || list.isEmpty()) { %>
                <tr>
                    <td colspan="9" class="text-center text-muted py-4">
                        <%= month %> 급여 데이터가 없습니다. 일괄 생성 버튼을 눌러 생성하세요.
                    </td>
                </tr>
                <% } else { for (Salary sal : list) { %>
                <tr>
                    <td class="ps-3"><%= sal.getEmpName() %></td>
                    <td><%= sal.getDeptName() != null ? sal.getDeptName() : "-" %></td>
                    <td><%= sal.getPosName()  != null ? sal.getPosName()  : "-" %></td>
                    <td class="text-end"><%= String.format("%,d", sal.getBasePay()) %>원</td>
                    <td class="text-end text-success">+<%= String.format("%,d", sal.getAllowance()) %>원</td>
                    <td class="text-end text-danger">-<%= String.format("%,d", sal.getDeduction()) %>원</td>
                    <td class="text-end fw-bold"><%= String.format("%,d", sal.getNetPay()) %>원</td>
                    <td class="text-center">
                        <span class="badge <%= "PAID".equals(sal.getPayStatus()) ? "bg-success" : "bg-warning text-dark" %>">
                            <%= "PAID".equals(sal.getPayStatus()) ? "지급완료" : "대기" %>
                        </span>
                    </td>
                    <td class="text-center">
                        <% if ("PENDING".equals(sal.getPayStatus())) { %>
                        <form action="${pageContext.request.contextPath}/salary/pay" method="post" class="d-inline">
                            <input type="hidden" name="salId"  value="<%= sal.getSalId() %>">
                            <input type="hidden" name="month"  value="<%= month %>">
                            <button type="submit" class="btn btn-sm btn-outline-success">지급 확정</button>
                        </form>
                        <% } else { %>
                        <span class="text-muted small">-</span>
                        <% } %>
                    </td>
                </tr>
                <% } } %>
                </tbody>
                <% if (list != null && !list.isEmpty()) { %>
                <tfoot class="table-light fw-bold">
                    <tr>
                        <td colspan="3" class="ps-3">합계</td>
                        <td class="text-end"><%= String.format("%,d", totalBase) %>원</td>
                        <td class="text-end text-success">+<%= String.format("%,d", totalAllow) %>원</td>
                        <td class="text-end text-danger">-<%= String.format("%,d", totalDeduct) %>원</td>
                        <td class="text-end text-primary"><%= String.format("%,d", totalNet) %>원</td>
                        <td colspan="2"></td>
                    </tr>
                </tfoot>
                <% } %>
            </table>
        </div>
    </div>

    <%-- 공제 기준 안내 --%>
    <div class="text-muted small mt-2">
        * 자동계산 기준: 수당 = 식비 100,000 + 교통비 50,000 / 공제 = 기본급 × 9% (국민연금 4.5% + 건강보험 3.545% + 고용보험 0.9%)
    </div>
</div>
<%@ include file="/common/footer.jsp" %>
