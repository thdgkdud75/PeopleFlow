<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/header.jsp" %>
<div class="container-fluid">
<div class="row">
<%@ include file="/common/nav.jsp" %>
<div class="col-md-10 main-content">
    <h4 class="mb-4">휴가 신청</h4>
    <div class="card">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/leave/apply" method="post">
                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label">휴가 유형</label>
                        <select name="leaveType" class="form-select" required>
                            <option value="ANNUAL">연차</option>
                            <option value="SICK">병가</option>
                            <option value="SPECIAL">특별휴가</option>
                            <option value="UNPAID">무급휴가</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">시작일</label>
                        <input type="date" name="startDate" class="form-control" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">종료일</label>
                        <input type="date" name="endDate" class="form-control" required>
                    </div>
                    <div class="col-12">
                        <label class="form-label">사유</label>
                        <textarea name="reason" class="form-control" rows="4" required></textarea>
                    </div>
                </div>
                <div class="mt-4">
                    <button type="submit" class="btn btn-primary">신청</button>
                    <a href="${pageContext.request.contextPath}/leave/history" class="btn btn-secondary ms-2">취소</a>
                </div>
            </form>
        </div>
    </div>
</div>
</div>
</div>
<%@ include file="/common/footer.jsp" %>
