# PeopleFlow AI Agent 구조 설명

## 1. 전체 아키텍처 (A2A)

```
[사용자 브라우저]
      |
      | HTTP
      v
[Java Tomcat :8080]
  AIController      ← HR 챗봇
  DocumentService   ← 문서 요약 (업무일지/회의록/출장)
  AIService         ← 근태분석, 인사평가 리포트
      |
      | HTTP POST /chat · /summarize
      v
[Python AI 서버 :8000]
  OrchestratorAgent  ← 질문 분석, 서브에이전트 위임
    ├── LeaveAgent      ← 연차·휴가 전문
    ├── AttendanceAgent ← 근태·출근 전문
    └── SalaryAgent     ← 급여 전문
      |
      | pymysql (직접 DB 조회)
      v
[MySQL :3306 / hrdb]
```

---

## 2. 구조 변천사

### 1세대 — 단순 프롬프트 주입
```
사용자: "잔여 연차 알려줘"
  → Java가 DB에서 연차·근태·급여 전부 미리 조회
  → 조회 결과를 시스템 프롬프트에 텍스트로 삽입
  → 모델이 프롬프트를 읽고 답변

문제: 질문과 무관한 데이터도 항상 조회, 모델이 판단 없이 텍스트만 읽음
```

### 2세대 — Single Agent + Tool-use
```
사용자: "잔여 연차 알려줘"
  → 모델이 스스로 판단: "연차 조회가 필요하다"
  → <tool_call> get_remain_leave(emp_id=2) 출력
  → Python 서버가 DB 조회 실행 후 결과 전달
  → 모델이 자연어 답변 생성

문제: 에이전트 1개가 모든 도메인(연차·근태·급여) 담당 → 역할 비대화
```

### 3세대 — A2A (Agent-to-Agent) ← 현재
```
사용자: "잔여 연차 알려줘"
  → OrchestratorAgent가 질문 분석
  → "연차 질문이다" → call_leave_agent 위임
  → LeaveAgent가 자체 ReAct 루프 실행
      → get_remain_leave() 호출 → DB 조회
      → "잔여 연차: 12일" 반환
  → Orchestrator가 결과를 사용자에게 전달

장점: 각 에이전트가 자기 도메인에만 집중, 복합 질문 시 여러 에이전트 순차 위임
```

---

## 3. A2A 개념

A2A(Agent-to-Agent)는 **에이전트가 직접 DB나 함수를 호출하는 대신,
다른 전문 에이전트에게 작업을 위임하는 구조**입니다.

```
기존 Tool-use:
  에이전트 ──→ Tool(함수/DB)

A2A:
  오케스트레이터 ──→ 서브에이전트 A ──→ Tool(함수/DB)
                 └─→ 서브에이전트 B ──→ Tool(함수/DB)
```

### 오케스트레이터의 Tool = 서브에이전트 호출

| 오케스트레이터 Tool | 위임 대상 |
|---|---|
| `call_leave_agent` | LeaveAgent |
| `call_attendance_agent` | AttendanceAgent |
| `call_salary_agent` | SalaryAgent |

---

## 4. 서브에이전트별 Tool 목록

### LeaveAgent — 연차·휴가 전문

| Tool | 설명 | 권한 |
|---|---|---|
| `get_remain_leave(emp_id)` | 직원 잔여 연차 조회 | 전체 |
| `get_pending_leaves()` | 승인 대기 중인 휴가 목록 | 관리자 |

### AttendanceAgent — 근태·출근 전문

| Tool | 설명 | 권한 |
|---|---|---|
| `get_attendance_summary(emp_id, month)` | 월별 근태 현황 (정상/지각/결근/조기퇴근) | 전체 |
| `get_today_attendance()` | 오늘 전체 직원 출근 현황 | 관리자 |

### SalaryAgent — 급여 전문

| Tool | 설명 | 권한 |
|---|---|---|
| `get_salary_info(emp_id)` | 최근 급여 내역 (기본급·수당·공제·실수령액) | 전체 |

---

## 5. Agent 루프 (ReAct: Reasoning + Acting)

### 단일 도메인 질문 흐름
```
[1단계] 사용자: "이번 달 급여 얼마야?"

[2단계] OrchestratorAgent
        → 급여 관련 질문 판단
        → <tool_call> call_salary_agent(query="이번 달 급여 얼마야?")

[3단계] SalaryAgent 실행
        → get_salary_info() 호출
        → DB 조회 → "최근 급여 (2026-05) - 실수령액 2,850,000원"

[4단계] Orchestrator → 결과 바로 사용자에게 전달
        → "이번 달 실수령액은 2,850,000원입니다."
```

### 복합 도메인 질문 흐름
```
[1단계] 사용자: "연차랑 급여 둘 다 알려줘"

[2단계] OrchestratorAgent → call_leave_agent 위임
        LeaveAgent → get_remain_leave() → "잔여 연차: 12일"

[3단계] OrchestratorAgent → call_salary_agent 위임
        SalaryAgent → get_salary_info() → "실수령액 2,850,000원"

[4단계] Orchestrator가 두 결과 합산 → 사용자에게 전달
```

### HR 범위 외 질문 처리
```
사용자: "오늘 날씨 어때?"
  → OrchestratorAgent가 Tool 목록 확인
  → 날씨 관련 에이전트 없음
  → 직접 답변: "죄송합니다, HR 관련 질문만 답변드릴 수 있습니다."
```

---

## 6. 문서 요약 엔드포인트 (/summarize)

A2A 챗봇(`/chat`)과 별개로, 문서 요약은 `/summarize` 전용 엔드포인트를 사용합니다.
서브에이전트 없이 모델이 직접 요약합니다.

```
POST /summarize
{
  "doc_type": "worklog" | "meeting" | "trip",
  "content": "문서 전체 내용"
}
```

| 문서 유형 | 출력 포맷 |
|---|---|
| worklog | 📋 업무일지 요약 (주요 업무 / 완료 사항 / 이슈) |
| meeting | 📝 회의록 요약 (회의 개요 / 논의사항 / 결정사항 / 액션아이템) |
| trip | ✈️ 출장보고서 요약 (출장 개요 / 주요 활동 / 성과 / 후속 조치) |

---

## 7. 모델 구조

```
Qwen2.5-3B-Instruct (베이스 모델, 3.1B 파라미터)
    +
LoRA Adapter (29.9M 파라미터, 전체의 0.96%)
    ↓
4-bit NF4 양자화 (VRAM 약 2.5GB)
    ↓
FastAPI 서버 (uvicorn, port 8000)
    ↓
A2A 에이전트 구조 (Orchestrator + 3 Sub-Agents)
```

| 구성 요소 | 역할 |
|---|---|
| 베이스 모델 | 한국어/영어 이해, Function Calling 형식 지원 |
| LoRA 어댑터 | 업무일지/회의록/출장 요약 포맷 특화 (hr_train_data v5, 85.3% 정확도) |

---

## 8. 데이터 흐름

### HR 챗봇 (A2A)
```
브라우저
  → POST /employee/ai/chatbot  (Java)
  → AIController
  → AIService.chatbot(message, empName, empId)
  → AIUtil.callAgentChat(message, empId, empName, role, maxTokens)
  → POST http://localhost:8000/chat
  → OrchestratorAgent
      → 질문 분류
      → 서브에이전트 위임 (LeaveAgent / AttendanceAgent / SalaryAgent)
      → 서브에이전트 ReAct 루프 (Tool 호출 → DB 조회)
      → 최종 답변 생성
  → {"answer": "..."}
  → 브라우저 챗봇 UI 표시
```

### 문서 AI 요약
```
브라우저 (AI 요약 / 다시 요약 버튼 클릭)
  → POST /doc/worklog/summarize  (Java)
  → DocumentController
  → DocumentService.summarizeWorkLog(logId)
  → AIUtil.callSummarize("worklog", content)
  → POST http://localhost:8000/summarize
  → Qwen 모델 직접 추론 (서브에이전트 없음)
  → {"answer": "📋 업무일지 요약..."}
  → MySQL work_log.ai_summary 저장
  → 페이지 리다이렉트 → 요약 표시
```

---

## 9. Java 연동 (AIUtil.java)

```java
// HR 챗봇 (A2A Orchestrator)
AIUtil.callAgentChat(message, empId, empName, role, maxTokens);

// 문서 요약
AIUtil.callSummarize(docType, content);

// 단순 호출 (근태분석, 인사평가)
AIUtil.callLocalModel(systemPrompt, userMessage, maxTokens);
```

---

## 10. 엔드포인트 요약

| 엔드포인트 | 메서드 | 용도 |
|---|---|---|
| `/chat` | POST | HR 챗봇 (A2A: Orchestrator + Sub-Agents) |
| `/summarize` | POST | 문서 요약 (업무일지/회의록/출장) |
| `/health` | GET | 서버 상태 확인 |
