<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    int    activeCount      = request.getAttribute("activeCount")       != null ? (Integer) request.getAttribute("activeCount")       : 0;
    int    todayAttCount    = request.getAttribute("todayAttCount")     != null ? (Integer) request.getAttribute("todayAttCount")     : 0;
    int    pendingLeaveCount= request.getAttribute("pendingLeaveCount") != null ? (Integer) request.getAttribute("pendingLeaveCount") : 0;
    long   monthNetPay      = request.getAttribute("monthNetPay")       != null ? (Long)    request.getAttribute("monthNetPay")       : 0L;
    List<Map<String,Object>> pendingLeaves  = (List<Map<String,Object>>) request.getAttribute("pendingLeaves");
    Map<String,Integer>      attSummary     = (Map<String,Integer>)      request.getAttribute("attSummary");
    List<Map<String,Object>> deptHeadcount  = (List<Map<String,Object>>) request.getAttribute("deptHeadcount");
    String month = (String) request.getAttribute("month");

    int normal     = attSummary != null ? attSummary.getOrDefault("NORMAL",     0) : 0;
    int late       = attSummary != null ? attSummary.getOrDefault("LATE",       0) : 0;
    int earlyLeave = attSummary != null ? attSummary.getOrDefault("EARLY_LEAVE",0) : 0;
    int totalAtt   = normal + late + earlyLeave;

    // 도넛 차트 계산 (출근율 = todayAttCount / activeCount)
    double attRate = activeCount > 0 ? (double) todayAttCount / activeCount * 100.0 : 0;
    int attRateInt = (int) Math.round(attRate);
    // SVG 원 둘레 = 2πr, r=54 → ~339.3
    double circumference = 2 * Math.PI * 54;
    double dashOffset = circumference * (1 - attRate / 100.0);
%>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h4 class="page-title mb-0">대시보드</h4>
            <p class="text-muted small mb-0 mt-1"><%= month %> 기준</p>
        </div>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/employee/register" class="btn btn-primary btn-sm">
                <i class="bi bi-person-plus me-1"></i>직원 등록
            </a>
            <a href="${pageContext.request.contextPath}/salary/list" class="btn btn-outline-primary btn-sm">
                <i class="bi bi-cash-stack me-1"></i>급여 관리
            </a>
        </div>
    </div>

    <%-- ─── 요약 카드 4개 ─── --%>
    <div class="row g-3 mb-4">
        <div class="col-md-3">
            <div class="stat-card d-flex align-items-center gap-3">
                <div class="stat-icon stat-icon-blue"><i class="bi bi-people-fill"></i></div>
                <div>
                    <div class="stat-label">재직 직원</div>
                    <div class="stat-value"><%= activeCount %><span class="stat-unit">명</span></div>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stat-card d-flex align-items-center gap-3">
                <div class="stat-icon stat-icon-green"><i class="bi bi-check-circle-fill"></i></div>
                <div>
                    <div class="stat-label">오늘 출근</div>
                    <div class="stat-value"><%= todayAttCount %><span class="stat-unit">명</span></div>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stat-card d-flex align-items-center gap-3">
                <div class="stat-icon stat-icon-yellow"><i class="bi bi-hourglass-split"></i></div>
                <div>
                    <div class="stat-label">미결 휴가 신청</div>
                    <div class="stat-value"><%= pendingLeaveCount %><span class="stat-unit">건</span></div>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stat-card d-flex align-items-center gap-3">
                <div class="stat-icon stat-icon-red"><i class="bi bi-currency-dollar"></i></div>
                <div>
                    <div class="stat-label"><%= month %> 급여 총액</div>
                    <div class="stat-value" style="font-size:1.2rem;"><%= String.format("%,d", monthNetPay / 10000) %><span class="stat-unit">만원</span></div>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-3 mb-3">
        <%-- ─── 출근율 도넛 차트 ─── --%>
        <div class="col-md-4">
            <div class="card h-100">
                <div class="card-header">오늘 출퇴근 현황</div>
                <div class="card-body d-flex flex-column align-items-center justify-content-center py-4">
                    <div class="donut-wrap mb-3">
                        <svg width="140" height="140" viewBox="0 0 140 140">
                            <circle cx="70" cy="70" r="54" fill="none" stroke="#F0F2F5" stroke-width="14"/>
                            <circle cx="70" cy="70" r="54" fill="none" stroke="#1E6FFF" stroke-width="14"
                                stroke-dasharray="<%= String.format("%.1f", circumference) %>"
                                stroke-dashoffset="<%= String.format("%.1f", dashOffset) %>"
                                stroke-linecap="round"
                                transform="rotate(-90 70 70)"/>
                        </svg>
                        <div class="donut-label">
                            <div class="pct"><%= attRateInt %>%</div>
                            <div class="sub">출근율</div>
                        </div>
                    </div>
                    <div class="w-100 px-2">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="small text-muted">출근</span>
                            <span class="small fw-bold text-primary"><%= todayAttCount %>명</span>
                        </div>
                        <div class="progress mb-3" style="height:6px;">
                            <div class="progress-bar" style="width:<%= attRateInt %>%; background:#1E6FFF;"></div>
                        </div>
                        <div class="row text-center g-2">
                            <div class="col-4">
                                <div class="p-2 rounded-3" style="background:#FFFBEB;">
                                    <div class="fw-bold text-warning" style="font-size:1.1rem;"><%= late %></div>
                                    <div class="text-muted" style="font-size:0.7rem;">지각</div>
                                </div>
                            </div>
                            <div class="col-4">
                                <div class="p-2 rounded-3" style="background:#EFF6FF;">
                                    <div class="fw-bold text-info" style="font-size:1.1rem; color:#0EA5E9 !important;"><%= earlyLeave %></div>
                                    <div class="text-muted" style="font-size:0.7rem;">조퇴</div>
                                </div>
                            </div>
                            <div class="col-4">
                                <div class="p-2 rounded-3" style="background:#F3F4F6;">
                                    <div class="fw-bold text-secondary" style="font-size:1.1rem;"><%= activeCount - todayAttCount %></div>
                                    <div class="text-muted" style="font-size:0.7rem;">미출근</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <%-- ─── 미결 휴가 신청 ─── --%>
        <div class="col-md-8">
            <div class="card h-100">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span>미결 휴가 신청</span>
                    <a href="${pageContext.request.contextPath}/leave/pending" class="btn btn-sm btn-outline-primary">전체보기</a>
                </div>
                <div class="card-body p-0">
                    <table class="table mb-0">
                        <thead>
                            <tr>
                                <th class="ps-4">신청자</th>
                                <th>유형</th>
                                <th>기간</th>
                                <th class="text-center">일수</th>
                                <th class="text-center">처리</th>
                            </tr>
                        </thead>
                        <tbody>
                        <% if (pendingLeaves == null || pendingLeaves.isEmpty()) { %>
                        <tr>
                            <td colspan="5" class="text-center py-5">
                                <i class="bi bi-calendar-check text-muted" style="font-size:2rem;display:block;margin-bottom:8px;"></i>
                                <span class="text-muted small">미결 신청이 없습니다.</span>
                            </td>
                        </tr>
                        <% } else { for (Map<String,Object> lr : pendingLeaves) {
                            String lt = (String) lr.get("leaveType");
                            String ltLabel = "ANNUAL".equals(lt) ? "연차" : "SICK".equals(lt) ? "병가" : "SPECIAL".equals(lt) ? "특별" : "무급";
                            String ltColor = "ANNUAL".equals(lt) ? "bg-primary" : "SICK".equals(lt) ? "bg-danger" : "SPECIAL".equals(lt) ? "bg-success" : "bg-secondary";
                        %>
                        <tr>
                            <td class="ps-4">
                                <div class="d-flex align-items-center gap-2">
                                    <div style="width:30px;height:30px;border-radius:50%;background:linear-gradient(135deg,#1E6FFF,#6CA8FF);display:flex;align-items:center;justify-content:center;color:white;font-size:0.75rem;font-weight:700;">
                                        <%= ((String)lr.get("empName")).charAt(0) %>
                                    </div>
                                    <span class="fw-500"><%= lr.get("empName") %></span>
                                </div>
                            </td>
                            <td><span class="badge <%= ltColor %>"><%= ltLabel %></span></td>
                            <td class="text-muted small"><%= lr.get("startDate") %> ~ <%= lr.get("endDate") %></td>
                            <td class="text-center"><span class="fw-bold"><%= lr.get("leaveDays") %>일</span></td>
                            <td class="text-center">
                                <form action="${pageContext.request.contextPath}/leave/approve" method="post" class="d-inline">
                                    <input type="hidden" name="leaveId" value="<%= lr.get("leaveId") %>">
                                    <input type="hidden" name="action" value="APPROVE">
                                    <button type="submit" class="btn btn-sm btn-outline-success py-0 px-2" style="font-size:0.78rem;">승인</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/leave/approve" method="post" class="d-inline">
                                    <input type="hidden" name="leaveId" value="<%= lr.get("leaveId") %>">
                                    <input type="hidden" name="action" value="REJECT">
                                    <button type="submit" class="btn btn-sm btn-outline-danger py-0 px-2 ms-1" style="font-size:0.78rem;">반려</button>
                                </form>
                            </td>
                        </tr>
                        <% } } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-3">
        <%-- ─── 이번달 근태 현황 ─── --%>
        <div class="col-md-4">
            <div class="card">
                <div class="card-header"><i class="bi bi-bar-chart-line me-2 text-primary"></i><%= month %> 근태 현황</div>
                <div class="card-body py-3">
                    <div class="mb-3">
                        <div class="d-flex justify-content-between mb-1">
                            <span class="small text-muted">정상 출근</span>
                            <span class="small fw-bold text-success"><%= normal %>건</span>
                        </div>
                        <div class="progress">
                            <div class="progress-bar" style="width:<%= totalAtt > 0 ? normal*100/totalAtt : 0 %>%; background:#10B981;"></div>
                        </div>
                    </div>
                    <div class="mb-3">
                        <div class="d-flex justify-content-between mb-1">
                            <span class="small text-muted">지각</span>
                            <span class="small fw-bold text-warning"><%= late %>건</span>
                        </div>
                        <div class="progress">
                            <div class="progress-bar" style="width:<%= totalAtt > 0 ? late*100/totalAtt : 0 %>%; background:#F59E0B;"></div>
                        </div>
                    </div>
                    <div class="mb-1">
                        <div class="d-flex justify-content-between mb-1">
                            <span class="small text-muted">조퇴</span>
                            <span class="small fw-bold" style="color:#0EA5E9;"><%= earlyLeave %>건</span>
                        </div>
                        <div class="progress">
                            <div class="progress-bar" style="width:<%= totalAtt > 0 ? earlyLeave*100/totalAtt : 0 %>%; background:#0EA5E9;"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <%-- ─── 부서별 인원 ─── --%>
        <div class="col-md-4">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span><i class="bi bi-diagram-3 me-2 text-primary"></i>부서별 인원</span>
                    <a href="${pageContext.request.contextPath}/dept/list" class="btn btn-sm btn-outline-secondary" style="font-size:0.78rem;">관리</a>
                </div>
                <div class="card-body py-3">
                <% if (deptHeadcount != null) { for (Map<String,Object> d : deptHeadcount) {
                    int cnt = (Integer) d.get("cnt");
                    int pct = activeCount > 0 ? cnt * 100 / activeCount : 0;
                %>
                    <div class="mb-3">
                        <div class="d-flex justify-content-between mb-1">
                            <span class="small fw-500"><%= d.get("deptName") %></span>
                            <span class="small fw-bold text-primary"><%= cnt %>명</span>
                        </div>
                        <div class="progress">
                            <div class="progress-bar" style="width:<%= pct %>%;"></div>
                        </div>
                    </div>
                <% } } %>
                </div>
            </div>
        </div>

        <%-- ─── 빠른 메뉴 ─── --%>
        <div class="col-md-4">
            <div class="card">
                <div class="card-header"><i class="bi bi-lightning-charge me-2 text-primary"></i>빠른 메뉴</div>
                <div class="card-body">
                    <div class="row g-2">
                        <div class="col-6">
                            <a href="${pageContext.request.contextPath}/employee/register"
                               class="d-flex flex-column align-items-center justify-content-center p-3 rounded-3 text-decoration-none"
                               style="background:#F8FAFF; border:1.5px solid #EEF4FF; transition:all 0.15s;"
                               onmouseover="this.style.background='#EEF4FF'" onmouseout="this.style.background='#F8FAFF'">
                                <i class="bi bi-person-plus-fill mb-1" style="color:#1E6FFF; font-size:1.3rem;"></i>
                                <span class="small fw-600" style="color:#374151; font-size:0.78rem;">직원 등록</span>
                            </a>
                        </div>
                        <div class="col-6">
                            <a href="${pageContext.request.contextPath}/salary/list"
                               class="d-flex flex-column align-items-center justify-content-center p-3 rounded-3 text-decoration-none"
                               style="background:#F0FDF4; border:1.5px solid #D1FAE5; transition:all 0.15s;"
                               onmouseover="this.style.background='#D1FAE5'" onmouseout="this.style.background='#F0FDF4'">
                                <i class="bi bi-cash-stack mb-1" style="color:#10B981; font-size:1.3rem;"></i>
                                <span class="small fw-600" style="color:#374151; font-size:0.78rem;">급여 관리</span>
                            </a>
                        </div>
                        <div class="col-6">
                            <a href="${pageContext.request.contextPath}/leave/pending"
                               class="d-flex flex-column align-items-center justify-content-center p-3 rounded-3 text-decoration-none"
                               style="background:#FFFBF0; border:1.5px solid #FEF3C7; transition:all 0.15s;"
                               onmouseover="this.style.background='#FEF3C7'" onmouseout="this.style.background='#FFFBF0'">
                                <i class="bi bi-calendar-check-fill mb-1" style="color:#F59E0B; font-size:1.3rem;"></i>
                                <span class="small fw-600" style="color:#374151; font-size:0.78rem;">휴가 승인</span>
                            </a>
                        </div>
                        <div class="col-6">
                            <a href="${pageContext.request.contextPath}/attendance/list"
                               class="d-flex flex-column align-items-center justify-content-center p-3 rounded-3 text-decoration-none"
                               style="background:#FFF1F0; border:1.5px solid #FFE0DD; transition:all 0.15s;"
                               onmouseover="this.style.background='#FFE0DD'" onmouseout="this.style.background='#FFF1F0'">
                                <i class="bi bi-clock-history mb-1" style="color:#EF4444; font-size:1.3rem;"></i>
                                <span class="small fw-600" style="color:#374151; font-size:0.78rem;">근태 조회</span>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

</div>
<%@ include file="/common/footer.jsp" %>
