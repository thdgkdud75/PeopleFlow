<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.Employee, util.SessionUtil" %>
<%
    Employee loginUser = SessionUtil.getLoginUser(request);
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PeopleFlow HR</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        * { box-sizing: border-box; }
        body { background-color: #F0F2F5; font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; color: #1A1A2E; }

        /* ── 상단 네비바 ── */
        .top-navbar {
            height: 56px;
            background: #ffffff;
            border-bottom: 1px solid #E8EAED;
            display: flex;
            align-items: center;
            padding: 0 24px;
            position: sticky;
            top: 0;
            z-index: 100;
            box-shadow: 0 1px 4px rgba(0,0,0,0.06);
        }
        .brand-logo {
            font-size: 1.2rem;
            font-weight: 800;
            color: #1E6FFF;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 8px;
            letter-spacing: -0.5px;
        }
        .brand-logo:hover { color: #1558D6; }
        .brand-logo .logo-dot { color: #FF6B35; }
        .user-chip {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-left: auto;
        }
        .user-avatar {
            width: 32px; height: 32px;
            background: linear-gradient(135deg, #1E6FFF, #6CA8FF);
            border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            color: white; font-size: 0.8rem; font-weight: 700;
        }
        .user-name { font-size: 0.875rem; font-weight: 600; color: #374151; }
        .btn-logout {
            font-size: 0.8rem;
            color: #9CA3AF;
            background: none;
            border: 1px solid #E5E7EB;
            border-radius: 6px;
            padding: 4px 12px;
            cursor: pointer;
            transition: all 0.15s;
        }
        .btn-logout:hover { background: #F3F4F6; color: #374151; }

        /* ── 사이드바 ── */
        .sidebar {
            width: 220px;
            min-height: calc(100vh - 56px);
            background: #ffffff;
            border-right: 1px solid #E8EAED;
            padding: 16px 0;
            position: sticky;
            top: 56px;
            height: calc(100vh - 56px);
            overflow-y: auto;
        }
        .sidebar-section-label {
            font-size: 0.7rem;
            font-weight: 700;
            color: #9CA3AF;
            text-transform: uppercase;
            letter-spacing: 0.08em;
            padding: 12px 20px 6px;
        }
        .sidebar .nav-link {
            display: flex;
            align-items: center;
            gap: 10px;
            color: #6B7280;
            padding: 9px 20px;
            font-size: 0.875rem;
            font-weight: 500;
            border-radius: 0;
            transition: all 0.15s;
            border-left: 3px solid transparent;
        }
        .sidebar .nav-link i { font-size: 1rem; width: 18px; text-align: center; }
        .sidebar .nav-link:hover { color: #1E6FFF; background: #EEF4FF; }
        .sidebar .nav-link.active { color: #1E6FFF; background: #EEF4FF; border-left-color: #1E6FFF; font-weight: 600; }
        .sidebar hr { border-color: #F3F4F6; margin: 8px 16px; }

        /* ── 메인 컨텐츠 ── */
        .main-content { padding: 28px 32px; flex: 1; }
        .page-title { font-size: 1.25rem; font-weight: 700; color: #111827; margin-bottom: 20px; }

        /* ── 카드 ── */
        .card {
            border: none !important;
            border-radius: 14px !important;
            box-shadow: 0 1px 4px rgba(0,0,0,0.06), 0 4px 16px rgba(0,0,0,0.04) !important;
            background: #ffffff;
        }
        .card-header {
            background: #ffffff !important;
            border-bottom: 1px solid #F3F4F6 !important;
            border-radius: 14px 14px 0 0 !important;
            padding: 16px 20px !important;
            font-weight: 600;
            font-size: 0.9rem;
            color: #111827;
        }

        /* ── 스탯 카드 ── */
        .stat-card {
            border-radius: 14px;
            padding: 20px 22px;
            background: #fff;
            box-shadow: 0 1px 4px rgba(0,0,0,0.06), 0 4px 16px rgba(0,0,0,0.04);
        }
        .stat-card .stat-label { font-size: 0.78rem; color: #9CA3AF; font-weight: 500; margin-bottom: 6px; }
        .stat-card .stat-value { font-size: 1.75rem; font-weight: 800; color: #111827; line-height: 1; }
        .stat-card .stat-unit { font-size: 0.9rem; font-weight: 500; color: #6B7280; margin-left: 2px; }
        .stat-card .stat-icon {
            width: 44px; height: 44px;
            border-radius: 12px;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.3rem;
        }
        .stat-icon-blue   { background: #EEF4FF; color: #1E6FFF; }
        .stat-icon-green  { background: #ECFDF5; color: #10B981; }
        .stat-icon-yellow { background: #FFFBEB; color: #F59E0B; }
        .stat-icon-red    { background: #FEF2F2; color: #EF4444; }

        /* ── 버튼 ── */
        .btn-primary { background: #1E6FFF !important; border-color: #1E6FFF !important; border-radius: 8px; font-weight: 600; }
        .btn-primary:hover { background: #1558D6 !important; border-color: #1558D6 !important; }
        .btn-outline-primary { color: #1E6FFF !important; border-color: #1E6FFF !important; border-radius: 8px; font-weight: 600; }
        .btn-outline-primary:hover { background: #1E6FFF !important; color: #fff !important; }
        .btn-success { background: #10B981 !important; border-color: #10B981 !important; border-radius: 8px; font-weight: 600; }
        .btn-outline-success { color: #10B981 !important; border-color: #10B981 !important; border-radius: 8px; }
        .btn-outline-success:hover { background: #10B981 !important; color: #fff !important; }
        .btn-outline-secondary { border-radius: 8px; }
        .btn-outline-danger { border-radius: 8px; }

        /* ── 테이블 ── */
        .table { color: #374151; }
        .table thead th { font-size: 0.78rem; font-weight: 700; color: #9CA3AF; text-transform: uppercase; letter-spacing: 0.05em; background: #FAFAFA; border-bottom: 1px solid #F3F4F6; }
        .table tbody td { font-size: 0.875rem; vertical-align: middle; border-bottom: 1px solid #F9FAFB; }
        .table tbody tr:hover td { background: #F8FAFF; }
        .table-hover > tbody > tr:hover > * { --bs-table-color-state: #374151; }

        /* ── 뱃지 ── */
        .badge { border-radius: 6px; font-weight: 600; font-size: 0.75rem; padding: 4px 8px; }
        .badge.bg-success { background: #ECFDF5 !important; color: #059669 !important; }
        .badge.bg-warning { background: #FFFBEB !important; color: #D97706 !important; }
        .badge.bg-secondary { background: #F3F4F6 !important; color: #6B7280 !important; }
        .badge.bg-danger { background: #FEF2F2 !important; color: #DC2626 !important; }
        .badge.bg-primary { background: #EEF4FF !important; color: #1E6FFF !important; }
        .badge.bg-info { background: #EFF6FF !important; color: #2563EB !important; }

        /* ── 폼 ── */
        .form-control, .form-select {
            border: 1.5px solid #E5E7EB;
            border-radius: 8px;
            font-size: 0.875rem;
            color: #374151;
            padding: 8px 12px;
        }
        .form-control:focus, .form-select:focus {
            border-color: #1E6FFF;
            box-shadow: 0 0 0 3px rgba(30,111,255,0.12);
        }
        .form-label { font-size: 0.85rem; font-weight: 600; color: #374151; margin-bottom: 6px; }

        /* ── 레이아웃 ── */
        .app-body { display: flex; }

        /* ── 알림/모달 ── */
        .alert { border: none; border-radius: 10px; font-size: 0.875rem; }
        .alert-success { background: #ECFDF5; color: #065F46; }
        .alert-danger  { background: #FEF2F2; color: #991B1B; }
        .modal-content { border-radius: 16px; border: none; box-shadow: 0 20px 60px rgba(0,0,0,0.15); }
        .modal-header { border-bottom: 1px solid #F3F4F6; padding: 20px 24px; }
        .modal-footer { border-top: 1px solid #F3F4F6; padding: 16px 24px; }

        /* ── 페이지네이션 ── */
        .page-link { border-radius: 8px; color: #1E6FFF; border-color: #E5E7EB; margin: 0 2px; }
        .page-item.active .page-link { background: #1E6FFF; border-color: #1E6FFF; }

        /* ── 도넛 차트 (SVG) ── */
        .donut-wrap { position: relative; display: inline-flex; align-items: center; justify-content: center; }
        .donut-label { position: absolute; text-align: center; }
        .donut-label .pct { font-size: 1.5rem; font-weight: 800; color: #111827; line-height: 1; }
        .donut-label .sub { font-size: 0.7rem; color: #9CA3AF; margin-top: 2px; }

        /* ── 진행바 스타일 ── */
        .progress { border-radius: 99px; background: #F3F4F6; height: 8px; }
        .progress-bar { border-radius: 99px; background: #1E6FFF; }
    </style>
</head>
<body>
<div class="top-navbar">
    <a class="brand-logo" href="${pageContext.request.contextPath}/admin/dashboard">
        <i class="bi bi-people-fill"></i> PeopleFlow<span class="logo-dot">.</span>
    </a>
    <div class="user-chip">
        <div class="user-avatar"><%= loginUser.getName().charAt(0) %></div>
        <span class="user-name"><%= loginUser.getName() %></span>
        <form action="${pageContext.request.contextPath}/auth/logout" method="post" class="d-inline">
            <button type="submit" class="btn-logout">로그아웃</button>
        </form>
    </div>
</div>
<div class="app-body">
