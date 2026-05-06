<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/header.jsp" %>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <h4 class="mb-4">직원 등록</h4>
    <div class="card">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/employee/register" method="post">
                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label">사번</label>
                        <input type="text" name="empNo" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">이름</label>
                        <input type="text" name="name" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">이메일</label>
                        <input type="email" name="email" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">전화번호</label>
                        <input type="tel" name="phone" class="form-control">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">부서 ID</label>
                        <input type="number" name="deptId" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">직급 ID</label>
                        <input type="number" name="posId" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">입사일</label>
                        <input type="date" name="hireDate" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">권한</label>
                        <select name="role" class="form-select">
                            <option value="EMPLOYEE">직원</option>
                            <option value="ADMIN">관리자</option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">초기 비밀번호</label>
                        <input type="password" name="password" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">상태</label>
                        <select name="status" class="form-select">
                            <option value="ACTIVE">재직</option>
                            <option value="ON_LEAVE">휴직</option>
                        </select>
                    </div>
                </div>
                <div class="mt-4">
                    <button type="submit" class="btn btn-primary">등록</button>
                    <a href="${pageContext.request.contextPath}/employee/list" class="btn btn-secondary ms-2">취소</a>
                </div>
            </form>
        </div>
    </div>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
