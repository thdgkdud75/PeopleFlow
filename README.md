# PeopleFlow - 사내 인사 관리 시스템 (HR System)

Java Servlet / JSP 기반의 사내 인사 관리 시스템으로, Claude AI를 활용한 스마트 HR 기능을 포함합니다.

---

## 주요 기능

| 기능 | 설명 |
|------|------|
| 로그인 / 로그아웃 | 사번 + 비밀번호 인증, 세션 관리 |
| 직원 관리 | 직원 등록 / 조회 / 수정 / 퇴직 처리 |
| 부서 · 직급 관리 | 부서 및 직급 정보 관리 |
| 근태 관리 | 출퇴근 체크, 월별 근태 현황 조회 |
| 급여 관리 | 급여 등록, 지급 확정, 명세서 조회 |
| 휴가 관리 | 휴가 신청 / 승인 / 반려 |
| 인사 평가 | 직원 평가 등록 및 조회 |
| **AI HR 챗봇** | 직원이 HR 관련 질문 시 Claude AI가 실시간 답변 |
| **AI 근태 분석** | 월별 근태 데이터를 AI가 분석하여 관리자 리포트 생성 |
| **AI 평가서 생성** | 평가 데이터 기반으로 AI가 공식 인사 평가서 자동 작성 |

---

## 프로젝트 구조

```
HRSystem/
├── src/
│   ├── controller/       # Servlet 컨트롤러
│   │   ├── AuthController.java
│   │   ├── EmployeeController.java
│   │   ├── DeptController.java
│   │   ├── AttendanceController.java
│   │   ├── SalaryController.java
│   │   ├── LeaveController.java
│   │   ├── EvalController.java
│   │   └── AIController.java
│   ├── service/          # 비즈니스 로직
│   ├── dao/              # DB 접근 (JDBC)
│   ├── model/            # 데이터 모델
│   └── util/
│       ├── DBUtil.java       # DB 커넥션
│       ├── SessionUtil.java  # 세션 / 권한 관리
│       └── AIUtil.java       # Claude API 호출
└── WebContent/
    ├── WEB-INF/
    │   ├── web.xml
    │   └── lib/              # 외부 라이브러리 (JAR)
    ├── common/               # 공통 header / footer / nav
    ├── auth/                 # 로그인 페이지
    ├── admin/                # 관리자 전용 화면
    └── employee/             # 직원 전용 화면
```

---

## 기술 스택

- **Backend**: Java 17, Servlet 6.0, JSP
- **Frontend**: Bootstrap 5.3, JavaScript (Fetch API)
- **Database**: MySQL 8.x
- **AI**: Anthropic Claude API (claude-sonnet-4-6)
- **WAS**: Apache Tomcat 10.x

---

## 환경 설정

### 필수 환경변수

| 변수명 | 필수 | 설명 |
|--------|------|------|
| `DB_PASSWORD` | ✅ | MySQL 비밀번호 |
| `ANTHROPIC_API_KEY` | ✅ (AI 기능 사용 시) | Claude API 키 |
| `DB_USER` | 선택 | DB 사용자명 (기본값: `root`) |
| `DB_URL` | 선택 | DB 접속 URL |

**Windows 환경변수 설정:**
```powershell
[System.Environment]::SetEnvironmentVariable("DB_PASSWORD", "your_password", "Machine")
[System.Environment]::SetEnvironmentVariable("DB_USER", "root", "Machine")
```

### DB 설정

MySQL에서 데이터베이스 생성:
```sql
CREATE DATABASE hrdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 라이브러리

`WebContent/WEB-INF/lib/` 에 아래 JAR 추가:
- `jakarta.servlet-api-6.0.0.jar`
- `mysql-connector-j-8.x.jar`

---

## 권한 구조

| 역할 | 접근 가능 화면 |
|------|---------------|
| `ADMIN` | 전체 (대시보드, 직원·근태·급여·휴가·평가 관리, AI 분석) |
| `EMPLOYEE` | 마이페이지, 내 근태, 내 급여, 휴가 신청, HR 챗봇 |

---

## AI 기능 상세

### 1. HR 챗봇 (`/employee/chatbot.jsp`)
직원이 연차, 급여, 복지 등 HR 관련 질문을 입력하면 Claude AI가 실시간으로 답변합니다.

### 2. 근태 분석 (`/admin/attendance/ai_report.jsp`)
월별 근태 데이터(지각·결근·조기퇴근 현황)를 AI가 분석하여 관리자용 리포트를 생성합니다.

### 3. 인사 평가서 생성 (`/admin/eval/ai_generate.jsp`)
평가 점수, 등급, 코멘트를 기반으로 AI가 공식 인사 평가서를 자동으로 작성합니다.
