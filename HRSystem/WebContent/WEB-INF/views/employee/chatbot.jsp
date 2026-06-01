<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/header.jsp" %>
<%@ include file="/common/nav.jsp" %>
<div class="main-content">
    <h4 class="mb-4">HR 챗봇</h4>
    <div class="card" style="height: calc(100vh - 160px); display: flex; flex-direction: column; overflow: hidden;">
        <div class="card-body d-flex flex-column p-0" style="min-height: 0; flex: 1;">
            <div id="chatMessages" class="p-3" style="flex: 1; overflow-y: auto; min-height: 0;">
                <div class="d-flex mb-3">
                    <div class="bg-primary text-white rounded-3 p-3" style="max-width:70%;">
                        안녕하세요! HR 챗봇입니다. 연차, 급여, 복지 등 궁금한 점을 물어보세요.
                    </div>
                </div>
            </div>
            <div class="border-top p-3">
                <div class="input-group">
                    <input type="text" id="chatInput" class="form-control" placeholder="질문을 입력하세요...">
                    <button class="btn btn-primary" onclick="sendMessage()">전송</button>
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jsp" %>

<script>
const chatMessages = document.getElementById('chatMessages');
const chatInput = document.getElementById('chatInput');

chatInput.addEventListener('keypress', e => { if (e.key === 'Enter') sendMessage(); });

function appendMessage(text, isUser) {
    const wrapper = document.createElement('div');
    wrapper.className = 'd-flex mb-3 ' + (isUser ? 'justify-content-end' : '');
    const bubble = document.createElement('div');
    bubble.className = 'rounded-3 p-3 ' + (isUser ? 'bg-light text-dark' : 'bg-primary text-white');
    bubble.style.maxWidth = '70%';
    bubble.style.whiteSpace = 'pre-wrap';
    bubble.textContent = text;
    wrapper.appendChild(bubble);
    chatMessages.appendChild(wrapper);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

async function sendMessage() {
    const message = chatInput.value.trim();
    if (!message) return;
    chatInput.value = '';
    appendMessage(message, true);

    const loadingDiv = document.createElement('div');
    loadingDiv.className = 'd-flex mb-3';
    loadingDiv.innerHTML = '<div class="bg-primary text-white rounded-3 p-3"><div class="spinner-border spinner-border-sm" role="status"></div> 답변 생성 중...</div>';
    chatMessages.appendChild(loadingDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight;

    try {
        const params = new URLSearchParams();
        params.append('message', message);
        const res = await fetch('${pageContext.request.contextPath}/ai/chatbot', { method: 'POST', body: params });
        const data = await res.json();
        chatMessages.removeChild(loadingDiv);
        appendMessage(data.answer, false);
    } catch (e) {
        chatMessages.removeChild(loadingDiv);
        appendMessage('오류가 발생했습니다. 잠시 후 다시 시도해주세요.', false);
    }
}
</script>
