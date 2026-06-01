<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>PeopleFlow - 로그인</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        * { box-sizing: border-box; }
        body {
            background: #F0F2F5;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
        }
        .login-wrap {
            display: flex;
            width: 900px;
            min-height: 540px;
            border-radius: 20px;
            overflow: hidden;
            box-shadow: 0 8px 40px rgba(0,0,0,0.12);
        }
        .login-left {
            width: 380px;
            background: linear-gradient(145deg, #1E6FFF 0%, #4D95FF 100%);
            padding: 56px 44px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            color: white;
        }
        .login-left .brand {
            font-size: 1.6rem;
            font-weight: 800;
            letter-spacing: -0.5px;
        }
        .login-left .brand span { opacity: 0.7; }
        .login-left .tagline {
            font-size: 1.4rem;
            font-weight: 700;
            line-height: 1.5;
            margin-top: 48px;
        }
        .login-left .desc {
            font-size: 0.9rem;
            opacity: 0.8;
            margin-top: 12px;
            line-height: 1.6;
        }
        .login-left .features {
            margin-top: auto;
        }
        .feature-item {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 12px;
            font-size: 0.875rem;
            opacity: 0.9;
        }
        .feature-item i {
            width: 28px; height: 28px;
            background: rgba(255,255,255,0.2);
            border-radius: 8px;
            display: flex; align-items: center; justify-content: center;
            font-size: 0.85rem;
            flex-shrink: 0;
        }
        .login-right {
            flex: 1;
            background: white;
            padding: 56px 48px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }
        .login-right h2 {
            font-size: 1.5rem;
            font-weight: 800;
            color: #111827;
            margin-bottom: 6px;
        }
        .login-right .subtitle {
            color: #9CA3AF;
            font-size: 0.875rem;
            margin-bottom: 32px;
        }
        .form-group { margin-bottom: 18px; }
        .form-group label {
            font-size: 0.85rem;
            font-weight: 600;
            color: #374151;
            margin-bottom: 7px;
            display: block;
        }
        .form-group .input-wrap {
            position: relative;
        }
        .form-group .input-wrap i {
            position: absolute;
            left: 13px; top: 50%;
            transform: translateY(-50%);
            color: #9CA3AF;
            font-size: 0.95rem;
        }
        .form-group input {
            width: 100%;
            padding: 11px 14px 11px 38px;
            border: 1.5px solid #E5E7EB;
            border-radius: 10px;
            font-size: 0.9rem;
            color: #374151;
            transition: border-color 0.15s, box-shadow 0.15s;
            outline: none;
        }
        .form-group input:focus {
            border-color: #1E6FFF;
            box-shadow: 0 0 0 3px rgba(30,111,255,0.12);
        }
        .btn-login {
            width: 100%;
            padding: 12px;
            background: #1E6FFF;
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 0.95rem;
            font-weight: 700;
            cursor: pointer;
            transition: background 0.15s;
            margin-top: 8px;
        }
        .btn-login:hover { background: #1558D6; }
        .error-box {
            background: #FEF2F2;
            color: #DC2626;
            border-radius: 8px;
            padding: 10px 14px;
            font-size: 0.85rem;
            margin-bottom: 18px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .hint-box {
            margin-top: 24px;
            background: #F8FAFF;
            border-radius: 10px;
            padding: 14px 16px;
            font-size: 0.8rem;
            color: #6B7280;
            border: 1px solid #EEF4FF;
        }
        .hint-box strong { color: #1E6FFF; }
    </style>
</head>
<body>
<div class="login-wrap">
    <div class="login-left">
        <div>
            <div class="brand">PeopleFlow<span>.</span></div>
            <div class="tagline">스마트한 HR 관리를<br>시작하세요</div>
            <div class="desc">직원 관리부터 급여, 근태, 휴가까지<br>모든 HR 업무를 한 곳에서.</div>
        </div>
        <div class="features">
            <div class="feature-item"><i class="bi bi-people-fill"></i> 직원 · 부서 · 직급 관리</div>
            <div class="feature-item"><i class="bi bi-clock-history"></i> 실시간 근태 현황</div>
            <div class="feature-item"><i class="bi bi-cash-stack"></i> 자동 급여 계산</div>
            <div class="feature-item"><i class="bi bi-calendar-check"></i> 휴가 신청 · 승인</div>
        </div>
    </div>
    <div class="login-right">
        <h2>로그인</h2>
        <p class="subtitle">사번과 비밀번호를 입력해주세요.</p>

        <% if ("1".equals(request.getParameter("error"))) { %>
        <div class="error-box"><i class="bi bi-exclamation-circle-fill"></i> 사번 또는 비밀번호가 올바르지 않습니다.</div>
        <% } %>

        <form action="${pageContext.request.contextPath}/auth/login" method="post">
            <div class="form-group">
                <label>사번</label>
                <div class="input-wrap">
                    <i class="bi bi-person"></i>
                    <input type="text" name="empNo" placeholder="사번을 입력하세요" required autocomplete="username">
                </div>
            </div>
            <div class="form-group">
                <label>비밀번호</label>
                <div class="input-wrap">
                    <i class="bi bi-lock"></i>
                    <input type="password" name="password" placeholder="비밀번호를 입력하세요" required autocomplete="current-password">
                </div>
            </div>
            <button type="submit" class="btn-login">로그인</button>
        </form>

        <div class="hint-box">
            <strong>테스트 계정</strong><br>
            관리자: <strong>EMP001</strong> / admin1234 &nbsp;&nbsp;|&nbsp;&nbsp;
            직원: <strong>EMP002</strong> / pass1234
        </div>
    </div>
</div>
</body>
</html>
