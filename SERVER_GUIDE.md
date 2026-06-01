# PeopleFlow HR System - 서버 실행 가이드

## 구성 요소

| 항목 | 버전 / 경로 |
|------|------------|
| Java | JDK 25.0.2 (`C:\Program Files\Java\jdk-25.0.2`) |
| WAS | Apache Tomcat 10.1.54 (`C:\Users\woojin\tomcat10\apache-tomcat-10.1.54`) |
| DB | MySQL 8.0 (Windows 서비스: `MySQL80`) |
| 앱 소스 | `C:\Users\woojin\Documents\GitHub\PeopleFlow\HRSystem` |
| AI 모델 | Qwen2.5-3B + LoRA 어댑터 (`C:\Users\woojin\qwen-finetuned\`) |
| 모델 서버 | FastAPI + Uvicorn (`C:\Users\woojin\model_server_qwen.py`, 포트 8000) |

---

## 서버 시작 순서

### 1단계 - MySQL 확인
```powershell
Get-Service MySQL80
```
- `Status: Running` 이면 OK
- 멈춰 있으면: `Start-Service MySQL80`

### 2단계 - AI 모델 서버 시작 (약 15~20초 소요)
```powershell
$env:PYTHONUTF8 = "1"
Start-Process -FilePath "python" `
  -ArgumentList "C:\Users\woojin\model_server_qwen.py" `
  -WorkingDirectory "C:\Users\woojin" `
  -WindowStyle Minimized `
  -RedirectStandardOutput "C:\Users\woojin\model_server.log" `
  -RedirectStandardError  "C:\Users\woojin\model_server_err.log"
```
- 상태 확인: `Invoke-RestMethod http://localhost:8000/health`
- `{"status":"ok","model":"Qwen2.5-3B-Instruct + LoRA","tools":5}` 이면 정상

### 3단계 - Java 소스 컴파일 (소스 변경 시에만)
```powershell
$base = "C:\Users\woojin\Documents\GitHub\PeopleFlow\HRSystem"
$lib  = "$base\WebContent\WEB-INF\lib\*"
$tc   = "C:\Users\woojin\tomcat10\apache-tomcat-10.1.54\lib\*"
Get-ChildItem "$base\src" -Recurse -Filter "*.java" |
  ForEach-Object { $_.FullName } |
  ForEach-Object {
    & "C:\Program Files\Java\jdk-25.0.2\bin\javac.exe" `
      -cp "$lib;$tc" -sourcepath "$base\src" `
      -d "$base\WebContent\WEB-INF\classes" $_
  }
```

### 4단계 - Tomcat 시작
```powershell
# 기존 Tomcat 프로세스 확인 후 종료 (중요!)
$existing = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($existing) { Stop-Process -Id $existing.OwningProcess -Force }

$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"
Start-Process -FilePath "C:\Users\woojin\tomcat10\apache-tomcat-10.1.54\bin\startup.bat" `
  -WorkingDirectory "C:\Users\woojin\tomcat10\apache-tomcat-10.1.54" `
  -WindowStyle Minimized
```

**브라우저 접속:** http://localhost:8080/HRSystem/

---

## 서버 중지

### Tomcat 중지 (포트 기반 강제 종료 권장)
```powershell
# 안전한 방법 (포트 8080을 점유한 프로세스 종료)
Stop-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess -Force
```

> **주의:** `shutdown.bat` 사용 시 이전 Tomcat이 이미 포트 8005를 점유하고 있으면
> 새 Tomcat이 즉시 종료됩니다. 반드시 위 방법으로 기존 프로세스를 먼저 종료하세요.

### AI 모델 서버 중지
```powershell
Stop-Process -Name "python" -Force
```

---

## 테스트 계정

| 사번 | 비밀번호 | 역할 | 이름 |
|------|----------|------|------|
| EMP001 | admin1234 | ADMIN (관리자) | 관리자 |
| EMP002 | pass1234 | EMPLOYEE | 김철수 |
| EMP003 | pass1234 | EMPLOYEE | 이영희 |
| EMP004 | pass1234 | EMPLOYEE | 박민준 |

---

## 환경 변수 (Windows 시스템 환경 변수)

| 변수명 | 값 | 설명 |
|--------|-----|------|
| `DB_PASSWORD` | `1234` | MySQL root 비밀번호 (필수) |
| `DB_USER` | `root` | MySQL 사용자 (기본값) |
| `DB_URL` | `jdbc:mysql://localhost:3306/hrdb?...` | DB 주소 (기본값 사용) |

---

## DB 초기화 (최초 1회)

```bash
mysql -u root -p < schema.sql
```

---

## 파일 구조

```
PeopleFlow/
├── HRSystem/
│   ├── src/                    ← Java 소스 (.java)
│   └── WebContent/
│       ├── WEB-INF/
│       │   ├── classes/        ← 컴파일된 .class 파일
│       │   ├── lib/            ← JAR 파일
│       │   └── web.xml
│       └── WEB-INF/views/      ← JSP 화면
├── schema.sql                  ← DB 초기화 SQL
├── SERVER_GUIDE.md             ← 이 파일
├── AI_AGENT_GUIDE.md           ← AI Agent 구조 설명
└── hr_train_data_v5.json       ← AI 파인튜닝 데이터
```

---

## AI 모델 서버 상세

### 엔드포인트

| 엔드포인트 | 메서드 | 용도 |
|-----------|--------|------|
| `/chat` | POST | HR 챗봇 (Tool-use Agent) |
| `/summarize` | POST | 문서 요약 (업무일지/회의록/출장) |
| `/health` | GET | 서버 상태 확인 |

### `/chat` 요청 형식
```json
{
  "message"   : "잔여 연차 알려줘",
  "emp_id"    : 2,
  "emp_name"  : "김철수",
  "role"      : "EMPLOYEE",
  "max_tokens": 400
}
```

### `/summarize` 요청 형식
```json
{
  "doc_type" : "worklog",
  "content"  : "[업무일지]\n날짜: 2026-06-01\n내용:\n..."
}
```
- `doc_type`: `worklog` | `meeting` | `trip`

### 모델 정보

| 항목 | 내용 |
|------|------|
| 베이스 모델 | Qwen2.5-3B-Instruct |
| LoRA 어댑터 | `C:\Users\woojin\qwen-finetuned\` |
| 파인튜닝 데이터 | `hr_train_data_v5.json` (50개: 업무일지·회의록·출장보고서) |
| 토큰 정확도 | 85.3% (5에포크) |
| 양자화 | 4-bit NF4 (bfloat16) |
| VRAM 사용량 | 약 2.5GB |
| 응답 시간 | 약 15~25초/요약 |
| Tool 수 | 5개 (연차·근태·급여·미승인휴가·오늘출근) |

### 파인튜닝 이력

| 모델 | 데이터 | 스크립트 | 항목 수 | 토큰 정확도 | 비고 |
|------|--------|---------|--------|------------|------|
| EXAONE 2.4B | v1~v5 누적 | finetune_v7.py | 50개 | 77.4% | 구버전 |
| **Qwen2.5-3B** | **v5** | **finetune_qwen.py** | **50개** | **85.3%** | **현재 사용** |

---

## 자주 있는 문제

### Tomcat 재시작 후 변경이 반영 안 될 때
원인: 이전 Tomcat 프로세스가 포트 8005를 점유해 새 Tomcat이 즉시 종료됨
```powershell
# 기존 프로세스 먼저 확인
netstat -ano | Select-String ":8080|:8005"
# 포트 8080을 점유한 프로세스 종료
Stop-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess -Force
# 그 다음 startup.bat 실행
```

### DB 연결 오류 (ExceptionInInitializerError)
- `DB_PASSWORD` 환경 변수 확인
- Windows 시스템 환경 변수에 등록되어 있어야 함

### AI 요약이 "HR 관련 질문만 답변" 메시지로 나올 때
- 이전 버전 Tomcat이 구버전 코드로 실행 중인 경우
- 포트 8080 프로세스 강제 종료 후 Tomcat 재시작

### 모델 서버 시작 오류 (UnicodeEncodeError)
```powershell
$env:PYTHONUTF8 = "1"  # 반드시 설정 후 실행
```

### AI 요약이 느릴 때
- VRAM 8GB 기준 응답 15~25초 정상
- GPU 사용 확인: `nvidia-smi`
