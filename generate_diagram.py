# -*- coding: utf-8 -*-
"""PeopleFlow 포트폴리오용 아키텍처 다이어그램 (컴팩트 가로형: 시스템개요 + A2A + 2서버 합본)"""
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch
import matplotlib.font_manager as fm

FONT = r"C:\Windows\Fonts\malgun.ttf"
fm.fontManager.addfont(FONT)
plt.rcParams["font.family"] = "Malgun Gothic"
plt.rcParams["axes.unicode_minus"] = False

NAVY   = "#1e293b"
BLUE   = "#2563eb"
GREEN  = "#16a34a"
ORANGE = "#ea580c"
GRAY   = "#64748b"
SLATE  = "#475569"
LIGHT  = "#f1f5f9"
USER   = "#0ea5e9"

fig, ax = plt.subplots(figsize=(13, 9.2))
ax.set_xlim(0, 100)
ax.set_ylim(0, 70)
ax.axis("off")
fig.patch.set_facecolor("white")


def box(x, y, w, h, text, fc, tc="white", fs=10, bold=True, ec=None):
    p = FancyBboxPatch((x, y), w, h,
                       boxstyle="round,pad=0.12,rounding_size=0.7",
                       fc=fc, ec=ec or fc, lw=1.4, zorder=3)
    ax.add_patch(p)
    ax.text(x + w / 2, y + h / 2, text, ha="center", va="center",
            color=tc, fontsize=fs, fontweight="bold" if bold else "normal",
            zorder=5, linespacing=1.35)


def arrow(x1, y1, x2, y2, color=SLATE, lw=1.8, style="-|>"):
    ax.annotate("", xy=(x2, y2), xytext=(x1, y1),
                arrowprops=dict(arrowstyle=style, color=color, lw=lw,
                                shrinkA=3, shrinkB=3), zorder=2)


def header(y, text, color):
    ax.text(3, y, text, ha="left", va="center", fontsize=12.5,
            fontweight="bold", color=color)


# ─── TITLE ───
ax.text(50, 67.3, "PeopleFlow — AI HR 시스템 아키텍처", ha="center",
        va="center", fontsize=20, fontweight="bold", color=NAVY)
ax.text(50, 63.6, "사내 HR 데이터 자연어 질의 + 업무 문서 자동 요약  ·  온프레미스 자체 LLM 서빙",
        ha="center", va="center", fontsize=10.5, color=GRAY)

# ─── 시스템 개요 (가로 플로우) ───
oy = 56
flow = [("사용자\n(웹 브라우저)", USER), ("Java / Tomcat\n웹 :8080", NAVY),
        ("AI 서버\n:8000", BLUE), ("MySQL\nhrdb", GRAY)]
xs = [6, 30, 54, 78]
for (txt, c), x in zip(flow, xs):
    box(x, oy, 16, 6, txt, c, fs=10)
for x in xs[:-1]:
    arrow(x + 16, oy + 3, x + 24, oy + 3)

ax.plot([3, 97], [53, 53], color="#cbd5e1", lw=1.1, zorder=1)

# ════ ① A2A 멀티 에이전트 ════
header(51, "①  A2A 멀티 에이전트 챗봇 — 결정론적 라우팅 (응답 0.26초)", BLUE)

box(3, 44.5, 17, 4.8, "사용자 질문", USER, fs=10.5)
arrow(11.5, 44.5, 11.5, 43.3)
box(3, 37.3, 17, 6, "Orchestrator\n키워드 라우팅", NAVY, fs=10)
ax.text(11.5, 35.4, "LLM 0회", ha="center", va="top", fontsize=9,
        color=GREEN, fontweight="bold")

agents = ["LeaveAgent\n연차·휴가", "AttendanceAgent\n근태·출근", "SalaryAgent\n급여",
          "EvaluationAgent\n인사평가", "EmployeeAgent\n직원정보", "DocumentAgent\n업무문서"]
gx = [24, 43, 62]
gy = [44.0, 37.3]
gw, gh = 18, 5.2
for i, txt in enumerate(agents):
    cx = gx[i % 3]
    cy = gy[i // 3]
    box(cx, cy, gw, gh, txt, "#dcfce7", tc="#166534", fs=9.3, ec=GREEN)
arrow(20, 41, 24, 43, color="#94a3b8", lw=1.4)          # orchestrator → grid
box(83, 39, 14, 6, "MySQL\nhrdb", GRAY, fs=10)
arrow(80, 41, 83, 42, color="#94a3b8", lw=1.4)          # grid → DB

ax.text(50, 33.7, "키워드가 도메인을 정하고 각 에이전트가 DB를 직접 조회 — LLM 추론 없이 즉시 응답",
        ha="center", va="center", fontsize=9, color=GRAY, style="italic")

ax.plot([3, 97], [31.5, 31.5], color="#cbd5e1", lw=1.1, zorder=1)

# ════ ② 2-서버 배포 구조 ════
header(29.5, "②  2-서버 배포 구조 — 요약 전용 LLM(vLLM) 분리", ORANGE)

box(3, 21, 17, 6.5, "Java / Tomcat\n:8080", NAVY, fs=10)
arrow(20, 24.2, 27, 24.2)
box(27, 20, 27, 8.5, "경량 챗봇 서버 :8000\n(모델 X · 재시작 즉시)", BLUE, fs=10)
ax.text(40.5, 18.6, "/chat → DB 직접   ·   /summarize → 프록시",
        ha="center", va="top", fontsize=8.8, color=SLATE)

box(66, 19, 31, 10, "vLLM 서버 (Docker) :8001\nQwen2.5-3B + LoRA · 4-bit · GPU 전용",
    ORANGE, fs=10)
arrow(54, 24.2, 66, 24.2, color=ORANGE, lw=2.2)
ax.text(60, 25.5, "요약 위임", ha="center", va="bottom", fontsize=8.8,
        color=ORANGE, fontweight="bold")
ax.text(81.5, 17.6, "RTX 5060 (Blackwell sm_120)", ha="center", va="top",
        fontsize=8.3, color=ORANGE, style="italic")

box(27, 11.5, 27, 5, "MySQL  hrdb", GRAY, fs=10)
arrow(40.5, 20, 40.5, 16.5)

ax.text(50, 9, "Java 수정 0  ·  GPU/모델 완전 격리  ·  요약 동시처리 26배  ·  서버 재시작 30초+ → 즉시",
        ha="center", va="center", fontsize=9.3, color=GREEN, fontweight="bold")

ax.plot([3, 97], [6.5, 6.5], color="#cbd5e1", lw=1.1, zorder=1)

# ─── 성과 + 기술스택 ───
ax.text(50, 4.8, "성능:  챗봇 21초 → 0.26초 (80배↓)   |   문서요약 70초 → 16.7초   |   외부 API 비용 $0",
        ha="center", va="center", fontsize=10, fontweight="bold", color=NAVY)
box(8, 0.6, 84, 3,
    "Qwen2.5-3B · LoRA · vLLM · bitsandbytes 4bit · FastAPI · Docker · MySQL · Java/Tomcat",
    LIGHT, tc=SLATE, fs=9.5)

plt.tight_layout()
OUT = r"C:\Users\woojin\Documents\GitHub\PeopleFlow\portfolio_architecture.png"
plt.savefig(OUT, dpi=160, bbox_inches="tight", facecolor="white")
print(f"saved: {OUT}")
