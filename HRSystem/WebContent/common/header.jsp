<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.Employee, util.SessionUtil" %>
<%
    Employee loginUser = SessionUtil.getLoginUser(request);
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
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
    <style>
        body { background-color: #f8f9fa; }
        .navbar { background-color: #1a1a2e; }
        .navbar-brand { color: #e94560 !important; font-weight: bold; }
        .nav-link { color: #ffffff !important; }
        .sidebar { min-height: calc(100vh - 56px); background-color: #16213e; }
        .sidebar .nav-link { color: #a8b2d8; padding: 10px 20px; }
        .sidebar .nav-link:hover { color: #e94560; background-color: rgba(233,69,96,0.1); }
        .sidebar .nav-link.active { color: #e94560; }
        .main-content { padding: 24px; }
        .card { border: none; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
        .btn-primary { background-color: #e94560; border-color: #e94560; }
        .btn-primary:hover { background-color: #c73652; border-color: #c73652; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard.jsp">PeopleFlow</a>
        <div class="ms-auto d-flex align-items-center">
            <span class="text-white me-3"><%= loginUser.getName() %> 님</span>
            <form action="${pageContext.request.contextPath}/auth/logout" method="post" class="d-inline">
                <button type="submit" class="btn btn-outline-light btn-sm">로그아웃</button>
            </form>
        </div>
    </div>
</nav>
