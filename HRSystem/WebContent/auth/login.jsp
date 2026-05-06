<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>PeopleFlow - 로그인</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
    <style>
        body { background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); min-height: 100vh; display: flex; align-items: center; }
        .login-card { border-radius: 16px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); }
        .brand-title { color: #e94560; font-size: 2rem; font-weight: bold; }
        .btn-login { background-color: #e94560; border: none; padding: 12px; font-size: 1rem; }
        .btn-login:hover { background-color: #c73652; }
    </style>
</head>
<body>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-4">
            <div class="card login-card p-4">
                <div class="text-center mb-4">
                    <div class="brand-title">PeopleFlow</div>
                    <p class="text-muted">사내 인사관리 시스템</p>
                </div>
                <% if ("1".equals(request.getParameter("error"))) { %>
                <div class="alert alert-danger">사번 또는 비밀번호가 올바르지 않습니다.</div>
                <% } %>
                <form action="${pageContext.request.contextPath}/auth/login" method="post">
                    <div class="mb-3">
                        <label class="form-label">사번</label>
                        <input type="text" name="empNo" class="form-control" placeholder="사번을 입력하세요" required>
                    </div>
                    <div class="mb-4">
                        <label class="form-label">비밀번호</label>
                        <input type="password" name="password" class="form-control" placeholder="비밀번호를 입력하세요" required>
                    </div>
                    <button type="submit" class="btn btn-login btn-primary w-100 text-white">로그인</button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
