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
    ├── SalaryAgent     ← 급여 전문
    ├── EvaluationAgent ← 인사평가 전문
    ├── EmployeeAgent   ← 직원정보·조직 전문
    └── DocumentAgent   ← 업무일지·회의록·출장 전문
      |
      | pymysql (직접 DB 조회)
      v
[MySQL :3306 / hrdb]
```

---

## 2. 챗봇 질문 가능 범위

### 연차·휴가 (LeaveAgent)
| 질문 예시 | Tool |
|---|---|
| "잔여 연차 몇 일이야?" | `get_remain_leave` |
| "내 남은 연차 알려줘" | `get_remain_leave` |
| "미승인 휴가 신청 목록 보여줘" *(관리자)* | `get_pending_leaves` |
| "승인 대기 중인 휴가 있어?" *(관리자)* | `get_pending_leaves` |

### 근태·출근 (AttendanceAgent)
| 질문 예시 | Tool |
|---|---|
| "이번 달 근태 어때?" | `get_attendance_summary` |
| "지난달 지각 몇 번 했어?" | `get_attendance_summary` |
| "오늘 몇 명 출근했어?" *(관리자)* | `get_today_attendance` |
| "오늘 결근자 있어?" *(관리자)* | `get_today_attendance` |

### 급여 (SalaryAgent)
| 질문 예시 | Tool |
|---|---|
| "이번 달 급여 얼마야?" | `get_salary_info` |
| "실수령액 알려줘" | `get_salary_info` |
| "지난달이랑 급여 차이 얼마야?" | `get_salary_info` |
| "공제 내역 알려줘" | `get_salary_info` |

### 인사평가 (EvaluationAgent)
| 질문 예시 | Tool |
|---|---|
| "내 인사평가 점수 얼마야?" | `get_my_evaluation` |
| "최근 평가 등급 알려줘" | `get_my_evaluation` |
| "우리 부서 평가 현황 보여줘" *(관리자)* | `get_team_evaluations` |

### 직원정보·조직 (EmployeeAgent)
| 질문 예시 | Tool |
|---|---|
| "내 부서가 어디야?" | `get_employee_profile` |
| "내 직급이 뭐야?" | `get_employee_profile` |
| "입사일이 언제야?" | `get_employee_profile` |
| "내 연락처 정보 알려줘" | `get_employee_profile` |
| "우리 부서 직원 누가 있어?" *(관리자)* | `get_department_members` |

### 업무문서 (DocumentAgent)
| 질문 예시 | Tool |
|---|---|
| "최근 업무일지 보여줘" | `get_my_worklogs` |
| "이번 주에 업무일지 뭐 썼어?" | `get_my_worklogs` |
| "최근 회의록 목록 알려줘" | `get_my_meetings` |
| "출장 내역 있어?" | `get_my_trips` |
| "최근 출장 어디 갔어?" | `get_my_trips` |

### 복합 질문 (오케스트레이터가 여러 에이전트 순차 위임)
| 질문 예시 | 위임 에이전트 |
|---|---|
| "연차랑 급여 둘 다 알려줘" | LeaveAgent + SalaryAgent |
| "내 부서랑 평가 점수 알려줘" | EmployeeAgent + EvaluationAgent |

### 답변 불가 (HR 범위 외)
- 날씨, 뉴스, 일반 상식
- 채용·퇴직 처리 (DB 테이블 없음)
- 시스템 외 정보

---

## 3. A2A 구조 변천사

### 1세대 — 단순 프롬프트 주입
```
Java가 DB에서 모든 데이터 미리 조회 → 프롬프트에 텍스트 삽입 → 모델이 읽고 답변
문제: 질문과 무관한 데이터도 항상 조회
```

### 2세대 — Single Agent + Tool-use
```
모델이 필요한 Tool만 선택적 호출 → DB 조회 → 답변
문제: 에이전트 1개가 모든 도메인 담당 → 역할 비대화
```

### 3세대 — A2A (Agent-to-Agent) ← 현재
```
OrchestratorAgent가 질문 분류 → 전문 서브에이전트에 위임 → 각 에이전트가 자기 도메인만 처리
```

---

## 4. 서브에이전트별 Tool 상세

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

### EvaluationAgent — 인사평가 전문
| Tool | 설명 | 권한 |
|---|---|---|
| `get_my_evaluation(emp_id)` | 내 인사평가 내역 (점수·등급·코멘트) | 전체 |
| `get_team_evaluations(dept_id)` | 부서 전체 인사평가 현황 | 관리자 |

### EmployeeAgent — 직원정보·조직 전문
| Tool | 설명 | 권한 |
|---|---|---|
| `get_employee_profile(emp_id)` | 직원 프로필 (부서·직급·입사일·연락처) | 전체 |
| `get_department_members(dept_id)` | 부서 재직 직원 목록 | 관리자 |

### DocumentAgent — 업무문서 전문
| Tool | 설명 | 권한 |
|---|---|---|
| `get_my_worklogs(emp_id, limit)` | 최근 업무일지 목록 | 전체 |
| `get_my_meetings(emp_id, limit)` | 최근 회의록 목록 | 전체 |
| `get_my_trips(emp_id, limit)` | 최근 출장 내역 | 전체 |

---

## 5. Agent 루프 (ReAct: Reasoning + Acting)

### 단일 도메인 질문
```
사용자: "이번 달 급여 얼마야?"
  → OrchestratorAgent → call_salary_agent
  → SalaryAgent: get_salary_info() → DB 조회
  → "실수령액 2,850,000원" 반환
```

### 복합 도메인 질문
```
사용자: "연차랑 급여 둘 다 알려줘"
  → OrchestratorAgent → call_leave_agent → "잔여 연차 12일"
  → OrchestratorAgent → call_salary_agent → "실수령액 2,850,000원"
  → 두 결과 합산 전달
```

### HR 범위 외 질문
```
사용자: "오늘 날씨 어때?"
  → OrchestratorAgent: 해당 에이전트 없음
  → "죄송합니다, HR 관련 질문만 답변드릴 수 있습니다."
```

---

## 6. 문서 요약 엔드포인트 (/summarize)

A2A 챗봇과 별개로, 문서 요약은 서브에이전트 없이 모델이 직접 처리합니다.

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
A2A: Orchestrator + 6 Sub-Agents
```

---

## 8. 데이터 흐름

### HR 챗봇 (A2A)
```
브라우저
  → POST /employee/ai/chatbot  (Java)
  → AIUtil.callAgentChat(message, empId, empName, role, maxTokens)
  → POST http://localhost:8000/chat
  → OrchestratorAgent → 서브에이전트 위임
  → 서브에이전트 ReAct 루프 (Tool → DB 조회)
  → {"answer": "..."}
```

### 문서 AI 요약
```
브라우저 (AI 요약 버튼)
  → POST /doc/worklog/summarize  (Java)
  → AIUtil.callSummarize("worklog", content)
  → POST http://localhost:8000/summarize
  → Qwen 직접 추론 (서브에이전트 없음)
  → MySQL ai_summary 저장
```

---

## 9. Java 연동 (AIUtil.java)

```java
AIUtil.callAgentChat(message, empId, empName, role, maxTokens); // HR 챗봇 (A2A)
AIUtil.callSummarize(docType, content);                          // 문서 요약
AIUtil.callLocalModel(systemPrompt, userMessage, maxTokens);     // 단순 호출 (근태분석 등)
```

---

## 10. 성능 최적화

### 병목 원인
A2A 구조는 에이전트가 여러 개라 LLM 추론이 여러 번 발생합니다.

| 구조 | 질문 1개당 LLM 추론 횟수 |
|---|---|
| 1세대 (프롬프트 주입) | 1회 |
| 2세대 (Single Agent) | 1~2회 |
| 3세대 A2A 초기 | 2~3회 (오케스트레이터 1회 + 서브에이전트 1~2회) |
| **3세대 A2A 최적화 후** | **1~2회** (오케스트레이터 LLM 제거) |

### 적용된 최적화

**① 오케스트레이터 키워드 라우팅 (가장 큰 효과)**
- LLM 기반 라우팅 → 키워드 패턴 매칭으로 교체
- 오케스트레이터 LLM 추론 1회 완전 제거
- "잔여 연차"라는 단어가 있으면 즉시 LeaveAgent로 분기

```python
# 키워드만으로 즉시 라우팅 (LLM 호출 없음)
ROUTING_RULES = [
    (["연차", "휴가", "반차", ...], "call_leave_agent"),
    (["급여", "월급", "수당", ...], "call_salary_agent"),
    ...
]
```

**② max_new_tokens 축소 (400 → 250)**
- HR 답변은 짧기 때문에 토큰 생성 한도를 줄여 생성 시간 단축
- 문서 요약은 500 유지 (긴 텍스트 필요)

**③ 서브에이전트 max_iter = 2 유지**
- Tool 호출(1회) + 최종 답변 생성(1회) = 최소 2회 필요
- 불필요한 반복 루프 없음

### 추가로 속도를 더 높이려면

| 방법 | 효과 | 비고 |
|---|---|---|
| Flash Attention 2 | 중간 | GPU가 Ampere(30xx) 이상이어야 함 |
| Qwen2.5-1.5B로 교체 | 큰 | 답변 품질 다소 저하 |
| vLLM 서버로 교체 | 매우 큰 | 설치 복잡, VRAM 여유 필요 |
| 양자화 수준 낮추기 (8-bit) | 작음 | VRAM 더 필요 |

---

## 11. 엔드포인트 요약

| 엔드포인트 | 메서드 | 용도 |
|---|---|---|
| `/chat` | POST | HR 챗봇 (A2A: Orchestrator + 6 Sub-Agents) |
| `/summarize` | POST | 문서 요약 (업무일지/회의록/출장) |
| `/health` | GET | 서버 상태 확인 |
