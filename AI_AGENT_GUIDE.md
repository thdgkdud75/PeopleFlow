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

### 측정값 (RTX 5060 Laptop 8GB)

| 작업 | 최적화 전 | 최적화 후 | 개선 |
|---|---|---|---|
| 챗봇 응답 | 21초 | **0.26초** | **약 80배** |
| 문서 요약 | 70초 | **16.7초** | **약 4배** |

**① 챗봇 LLM 완전 제거 (결정론적 도구 선택 — 가장 큰 효과)**
- 기존: 키워드로 에이전트를 정한 뒤에도, 서브에이전트가 *LLM을 호출해* 도구를 선택 → 1건당 20초+
- 변경: 키워드 라우팅이 도메인을 정하면, 각 에이전트가 **질문 키워드 + 컨텍스트(권한·이번달 등)로 도구를 즉시 결정**하고 DB만 조회
- 챗봇 경로에서 **LLM 추론이 0회** → 응답이 DB 쿼리 시간(0.26초)으로 단축

```python
# 예: AttendanceAgent.resolve — LLM 없이 즉시 도구 결정
def resolve(self, query, ctx):
    if "오늘" in query:
        return get_today_attendance()          # 오늘 현황
    return get_attendance_summary(ctx.emp_id, parse_month(query, ctx.month))  # 월별
```

> ⚠️ **8-bit은 함정이었음**: 한때 "균형"을 이유로 4-bit→8-bit(LLM.int8)으로
> 바꿨으나, 8-bit은 이상치 분해 오버헤드로 **토큰 생성이 오히려 느림**.
> 4-bit NF4로 되돌려 요약 속도를 70초→16.7초로 개선.

**② 4-bit NF4 + SDPA (RTX 5060 8GB)**
```
모드 비교 (RTX 5060 Laptop 8GB 기준):
  BF16   : 모델 ~6.2GB → 추론 여유 ~0.2GB (OOM 위험)
  8-bit  : 모델 ~3.1GB → 토큰 생성 느림 (LLM.int8 오버헤드)
  4-bit  : 모델 ~2.5GB → VRAM 사용 ~3.9GB, 여유 ~4GB ← 현재 선택 (빠름)
```
- `attn_implementation="sdpa"`: PyTorch 내장 최적화 어텐션 (flash-attn 불필요)
- TF32 활성화: Blackwell 아키텍처 행렬 연산 가속

**③ 키워드 라우팅 (오케스트레이터 LLM 제거)**
- 오케스트레이터도 LLM 없이 키워드로 즉시 분기

**④ max_new_tokens 축소**
- 챗봇 250 (현재는 챗봇이 LLM을 안 쓰므로 요약에만 적용)
- 문서 요약 500 → 384

### 남은 병목과 추가 개선 여지

| 항목 | 현황 | 추가 개선안 |
|---|---|---|
| 챗봇 | 0.26초 (충분히 빠름) | 추가 최적화 불필요 |
| 문서 요약 | 16.7초 (LLM 생성 필수) | vLLM 도입 시 5초 이하 가능(설치 복잡), 1.5B 모델 시 절반(품질↓) |

---

## 11. 트러블슈팅 / 수정 이력

실제 운영 중 발견한 문제와 해결 방법을 기록합니다.

### ① 출근 인원 질문에 모델이 추측으로 답변

- **증상**: "오늘 출근 인원" 질문에 *"출장 또는 퇴근 등으로 인해 16명 중 누군가가..."* 처럼
  데이터에 없는 이유를 지어내서 답변 (환각, hallucination)
- **원인**:
  1. 해당 날짜에 출근 기록이 아예 없는데, Tool이 "결근(미기록)"으로만 뭉뚱그려 반환
  2. 모델이 빈 정보를 추측으로 채움
- **해결**:
  - `get_today_attendance` Tool을 개선 — `attendance`뿐 아니라 `business_trip`(출장),
    `leave_request`(승인 휴가) 테이블을 교차 조회해 **출근/출장/휴가/미기록을 명확히 구분**
  - AttendanceAgent 시스템 프롬프트에 *"도구 결과에 없는 이유를 추측해서 지어내지 마세요"* 추가

### ② Tool 결과가 JSON/코드 형식으로 출력됨

- **증상**: 답변이 `{"data": {"재직자": 16, "출근": 0}}` 처럼 코드 형식으로 나옴
- **원인**: 모델이 구조화된 Tool 결과를 보고 그대로 JSON 스타일로 흉내냄
- **해결**: 시스템 프롬프트에 *"JSON이나 코드가 아닌 자연스러운 한국어 문장으로 작성하세요"* 명시

### ③ `<tool_call>` 태그가 사용자에게 그대로 노출

- **증상**: 답변에 `<tool_call>\n{"emp_id": 1, ...}` 같은 내부 태그가 잘린 채 노출
- **원인**: 소형 모델이 닫는 태그 `</tool_call>` 없이 출력을 끝내면, 기존 정규식이
  파싱에 실패하고 원시 텍스트를 그대로 반환
- **해결**:
  - `parse_tool_call`: 닫는 태그 누락 시에도 **중괄호 균형을 계산해 JSON 객체만 추출**
  - `clean_output`: 닫히지 않은 `<tool_call>` 잔재까지 제거

### ④ "오늘" 질문에 월별 통계 Tool을 잘못 선택

- **증상**: "오늘 출근 인원" 질문에 월 단위 `get_attendance_summary`를 호출
- **원인**: 두 Tool의 설명이 비슷해 모델이 혼동
- **해결**: Tool 설명을 명확히 구분
  - `get_attendance_summary`: *"한 달 전체 통계. '오늘' 질문에는 사용 금지"*
  - `get_today_attendance`: *"'오늘 몇 명 출근' 질문에 사용"*

### ⑤ Tool 실행 후 모델이 빈 답변 생성

- **증상**: Tool은 정상 실행됐는데 2차 생성에서 빈 출력 → "답변을 생성할 수 없습니다" 폴백
- **원인**: 소형 모델이 tool 결과를 받은 뒤 자연어 답변 대신 즉시 종료 토큰을 생성
- **해결**: `react_loop`에서 **2차 생성이 비면 직전 Tool 결과를 그대로 반환**.
  Tool 함수가 이미 완성된 한국어 문장을 반환하므로 정확성·속도 모두 확보

### ⑥ 질문 표현에 따라 다시 JSON 출력 (`{"employee_count": 16}`)

- **증상**: ②를 프롬프트로 막았지만, 특정 질문 표현에서 여전히
  `{"employee_count": 16}` 같은 JSON이 답변으로 노출
- **원인**: 그리디 디코딩 특성상 질문 표현이 조금만 달라도 모델이 JSON 형태로 빠짐.
  프롬프트 지시만으로는 소형 모델을 100% 통제 불가
- **해결**: 프롬프트에 의존하지 않는 **출력 검증 가드(`looks_unnatural`)** 추가.
  모델 답변이 `{`/`[`/```` ``` ````로 시작하거나 한글 없는 `"key": value` 형태면
  비자연어로 판정하고 **Tool 결과를 그대로 반환**.

### ⑦ "오늘 출장 간 사람"이 문서 에이전트로 잘못 라우팅

- **증상**: "오늘 출장 간 사람 있어?" → 출장 *문서* 조회로 빠져 빈 답변
- **원인**: 키워드 라우팅에서 "출장"이 DocumentAgent 키워드와 겹침.
  하지만 "오늘 출장 간 사람"은 *오늘 근태 현황* 질문
- **해결**: `keyword_route`에 우선 규칙 추가 —
  *"오늘" + (출장/휴가/출근/지각/인원/누구 등)* 이면 무조건 AttendanceAgent로 라우팅

### ⑧ 도구 결과를 모델이 재작성하며 환각 (한자·빈답변)

- **증상**:
  - 인사평가 질문에 **빈 답변** (모델이 도구를 아예 호출 안 함)
  - 휴가 목록을 "야간병**假**", "연차**假**" 처럼 **한자로 변형**
- **원인**: 소형 모델(3B)이 도구 결과를 자연어로 *다시 쓰는* 과정에서
  탈락·환각이 잦음. 프롬프트로는 근본 차단 불가
- **해결**: **재작성 단계를 아예 제거**.
  도구 함수가 이미 완성된 한국어 문장을 반환하므로,
  `react_loop`가 도구 실행 결과를 **그대로 반환**하도록 변경.
  추가로 모델이 도구 선택을 실패하면 **각 에이전트의 기본 도구(`default_fn`)로 폴백**
  (예: EvaluationAgent → `get_my_evaluation`, AttendanceAgent → "오늘"이면 `get_today_attendance`).
  부작용: 답변이 약간 덜 대화체이지만 **정확성·속도는 향상**

### ⑨ 업무일지에 거부 메시지가 요약으로 저장됨 (데이터 오염)

- **증상**: 업무일지 목록에 *"죄송합니다, HR 관련 질문만..."* 이 요약으로 표시
- **원인**: 과거 AI 요약 실패 시 **거부 메시지가 `ai_summary` 컬럼에 그대로 저장**됨
  (코드 버그가 아닌 누적된 오염 데이터)
- **해결**: `work_log`/`meeting_minutes`/`business_trip`의 오염된 `ai_summary`를
  `NULL`로 초기화 → 원본 내용이 표시되도록 복구

### ⑩ 챗봇이 너무 느림 (1건당 21초)

- **증상**: 단순 조회("내 잔여 연차")도 응답에 21초 소요
- **원인**: ⑧에서 도구 결과 *재작성*은 없앴지만, **도구 선택은 여전히 LLM이 수행**.
  소형 모델(3B)이 도구 하나 고르는 데 20초+ (8-bit 양자화가 이를 더 악화)
- **해결**: **챗봇 경로에서 LLM을 완전히 제거**.
  키워드 라우팅이 도메인을 정하면, 각 에이전트의 `resolve()`가
  질문 키워드 + 컨텍스트로 도구를 즉시 결정 → DB만 조회.
  추가로 8-bit→4-bit 복귀로 요약 속도도 개선.
  **결과: 챗봇 21초→0.26초, 요약 70초→16.7초** (성능 섹션 10 참조)

### 회귀 테스트 결과

근태·연차·급여·인사평가·직원정보·문서·범위외 질문 **전부 정상**.
환각·JSON·빈답변·오선택 모두 해소, 챗봇 0.26초로 응답.

---

## 12. 엔드포인트 요약

| 엔드포인트 | 메서드 | 용도 |
|---|---|---|
| `/chat` | POST | HR 챗봇 (A2A: Orchestrator + 6 Sub-Agents) |
| `/summarize` | POST | 문서 요약 (업무일지/회의록/출장) |
| `/health` | GET | 서버 상태 확인 |
