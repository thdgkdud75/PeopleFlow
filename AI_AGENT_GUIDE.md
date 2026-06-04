# PeopleFlow AI Agent 구조 설명

## 1. 전체 아키텍처

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
      | HTTP POST
      |  /chat      → HR 챗봇 (Tool-use Agent)
      |  /summarize → 문서 요약
      v
[Python AI Agent :8000]
  model_server_qwen.py
      |
      | pymysql (직접 DB 조회)
      v
[MySQL :3306 / hrdb]
```

---

## 2. 기존 방식 vs Agent 방식

### 기존 방식 (단순 프롬프트 주입)
```
사용자: "잔여 연차 알려줘"
  → Java가 DB에서 미리 연차·근태·급여 전부 조회
  → 조회 결과를 시스템 프롬프트에 텍스트로 삽입
  → 모델이 프롬프트를 읽고 답변

문제점: 질문과 무관한 데이터도 항상 DB에서 미리 가져옴
        모델이 직접 판단하지 않고 주어진 텍스트만 읽어서 답변
```

### Agent 방식 (Tool-use / Function Calling)
```
사용자: "잔여 연차 알려줘"
  → 모델이 스스로 판단: "연차 조회가 필요하다"
  → <tool_call> get_remain_leave(emp_id=2) 출력
  → Python 서버가 실제 DB 조회 실행
  → 조회 결과를 모델에게 다시 전달
  → 모델이 결과를 바탕으로 자연어 답변 생성

장점: 모델이 필요한 데이터만 선택적으로 가져옴
      복잡한 질문은 여러 Tool을 순서대로 호출 가능
      HR 범위 외 질문은 Tool 없이 직접 답변 또는 안내
```

---

## 3. Tool-use (Function Calling) 원리

### 핵심 개념
모델에게 **"이런 함수들이 있고, 필요하면 써도 돼"** 라고 알려주면
모델이 스스로 어떤 함수를 써야 할지 판단해서 호출 요청을 출력합니다.

### Qwen2.5의 Tool Call 출력 형식
```
<tool_call>
{"name": "get_remain_leave", "arguments": {"emp_id": 2}}
</tool_call>
```

### Agent 루프 (ReAct: Reasoning + Acting)

```
[1단계] 사용자 질문 수신
        "이번 달 급여 얼마야?"

[2단계] 모델이 Tool 목록 보고 판단
        → get_salary_info() 함수가 필요하다 판단
        → <tool_call> 출력

[3단계] Python 서버가 Tool 실행
        → pymysql로 MySQL 직접 조회
        → "실수령액 2,850,000원" 반환

[4단계] 결과를 모델에게 다시 전달
        → 대화 컨텍스트에 tool result 추가

[5단계] 모델이 최종 답변 생성
        → "이번 달 실수령액은 2,850,000원입니다."

[최대 3번 반복]
        복잡한 질문은 여러 Tool을 순차 호출 가능
```

### HR 범위 외 질문 처리
```
사용자: "오늘 날씨 어때?"
  → 모델이 Tool 목록 확인
  → 날씨 관련 Tool 없음
  → "죄송합니다, HR 관련 질문만 답변드릴 수 있습니다." 반환
```

---

## 4. 등록된 Tool 목록

| Tool 이름 | 설명 | 권한 |
|-----------|------|------|
| `get_remain_leave(emp_id)` | 직원 잔여 연차 조회 | 전체 |
| `get_attendance_summary(emp_id, month)` | 월별 근태 현황 (정상/지각/결근/조기퇴근) | 전체 |
| `get_salary_info(emp_id)` | 최근 급여 내역 (기본급·수당·공제·실수령액) | 전체 |
| `get_pending_leaves()` | 승인 대기 중인 휴가 목록 | 관리자 |
| `get_today_attendance()` | 오늘 전체 직원 출근 현황 | 관리자 |

---

## 5. 문서 요약 엔드포인트 (/summarize)

Tool-use Agent(`/chat`)와 별개로, 문서 요약은 `/summarize` 전용 엔드포인트를 사용합니다.

```
POST /summarize
{
  "doc_type": "worklog" | "meeting" | "trip",
  "content": "문서 전체 내용"
}
```

### 각 문서 유형별 출력 형식

**업무일지 (worklog)**
```
📋 업무일지 요약

▶ 주요 업무
- ...

✅ 완료 사항
- ...

⚠️ 이슈 / 특이사항
- ...
```

**회의록 (meeting)**
```
📝 회의록 요약

▶ 회의 개요
- 일시 / 참석자

📌 주요 논의사항
- ...

✅ 결정사항
- ...

🔔 액션아이템
- 담당자: 할 일
```

**출장보고서 (trip)**
```
✈️ 출장보고서 요약

▶ 출장 개요
- 기간 / 출장지 / 목적

📌 주요 활동
- ...

✅ 성과 / 결과
- ...

🔔 후속 조치
- ...
```

---

## 6. 모델 구조

```
Qwen2.5-3B-Instruct (베이스 모델, 3.1B 파라미터)
    +
LoRA Adapter (29.9M 파라미터, 전체의 0.96%)
    ↓
4-bit NF4 양자화 (VRAM 약 2.5GB)
    ↓
FastAPI 서버 (uvicorn, port 8000)
```

### 베이스 모델이 하는 것
- 한국어/영어 자연어 이해 및 생성
- Function Calling 형식 이해 (Qwen2.5 네이티브 지원)
- 일반적인 추론 및 대화

### LoRA 어댑터가 추가하는 것
- 업무일지/회의록/출장보고서 요약 포맷 특화
- `hr_train_data_v5.json` (50개 샘플, 5에포크, 토큰 정확도 85.3%)

---

## 7. 데이터 흐름

### HR 챗봇 (Tool-use)
```
브라우저
  → POST /employee/ai/chatbot  (Java)
  → AIController
  → AIService.chatbot(message, empName, empId)
  → AIUtil.callAgentChat(message, empId, empName, role, maxTokens)
  → POST http://localhost:8000/chat
  → Python Agent 루프 (최대 3회 반복)
      → Qwen 모델 추론
      → Tool 호출 결정
      → pymysql DB 직접 조회
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
  → Python /summarize 엔드포인트
      → Qwen 모델 추론 (Tool 없이 직접 요약)
      → 요약 텍스트 반환
  → {"answer": "📋 업무일지 요약..."}
  → MySQL work_log.ai_summary 저장
  → 페이지 리다이렉트 → 요약 표시
```

---

## 8. Java 연동 (AIUtil.java)

```java
// HR 챗봇 (Agent)
AIUtil.callAgentChat(message, empId, empName, role, maxTokens);

// 문서 요약
AIUtil.callSummarize(docType, content);

// 기존 단순 호출 (근태분석, 인사평가)
AIUtil.callLocalModel(systemPrompt, userMessage, maxTokens);
```

---

## 9. UI 로딩 표시

AI 요약 버튼 클릭 시 JavaScript가 자동으로:
- 버튼 비활성화 (중복 클릭 방지)
- 버튼 텍스트 → `⏳ AI 요약 중...` + 스피너로 교체
- 서버 응답 후 페이지 갱신 → 요약 결과 표시

요약이 이미 있는 항목에는 **"다시 요약"** 버튼이 표시되어 재생성 가능합니다.

---

## 10. 엔드포인트 요약

| 엔드포인트 | 메서드 | 용도 |
|-----------|--------|------|
| `/chat` | POST | HR 챗봇 (Agent + Tool-use) |
| `/summarize` | POST | 문서 요약 (업무일지/회의록/출장) |
| `/health` | GET | 서버 상태 확인 |
