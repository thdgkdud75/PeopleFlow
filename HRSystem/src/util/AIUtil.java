package util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class AIUtil {

    private static final Logger log = Logger.getLogger(AIUtil.class.getName());

    private static final String API_KEY = "REMOVED_API_KEY";
    private static final String API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;
    private static final String LOCAL_MODEL_URL      = "http://localhost:8000/chat";
    private static final String LOCAL_SUMMARIZE_URL  = "http://localhost:8000/summarize";

    /**
     * 파인튜닝된 EXAONE 로컬 모델 호출 (model_server.py 가 실행 중이어야 함).
     */
    public static String callLocalModel(String userMessage) throws Exception {
        return callLocalModel(null, userMessage);
    }

    public static String callLocalModel(String systemPrompt, String userMessage) throws Exception {
        return callLocalModel(systemPrompt, userMessage, 300);
    }

    /** Agent 챗봇 호출 — emp_id / role 포함 */
    public static String callAgentChat(String message, int empId, String empName, String role, int maxTokens) throws Exception {
        HttpURLConnection conn = (HttpURLConnection)
            URI.create(LOCAL_MODEL_URL).toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(120_000);

        String body = "{\"message\":\"" + escapeJson(message) + "\""
                    + ",\"emp_id\":" + empId
                    + ",\"emp_name\":\"" + escapeJson(empName) + "\""
                    + ",\"role\":\"" + escapeJson(role) + "\""
                    + ",\"max_tokens\":" + maxTokens + "}";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        int status = conn.getResponseCode();
        if (status != 200) {
            InputStream es = conn.getErrorStream();
            String err = es != null ? new String(es.readAllBytes(), StandardCharsets.UTF_8) : "(no body)";
            throw new RuntimeException("Agent 오류 " + status + ": " + err);
        }
        try (InputStream is = conn.getInputStream()) {
            return extractAnswer(new String(is.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    /** 문서 요약 호출 (업무일지/회의록/출장보고서) */
    public static String callSummarize(String docType, String content) throws Exception {
        HttpURLConnection conn = (HttpURLConnection)
            URI.create(LOCAL_SUMMARIZE_URL).toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(120_000);

        String body = "{\"doc_type\":\"" + escapeJson(docType) + "\""
                    + ",\"content\":\"" + escapeJson(content) + "\"}";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        int status = conn.getResponseCode();
        if (status != 200) {
            InputStream es = conn.getErrorStream();
            String err = es != null ? new String(es.readAllBytes(), StandardCharsets.UTF_8) : "(no body)";
            throw new RuntimeException("Summarize 오류 " + status + ": " + err);
        }
        try (InputStream is = conn.getInputStream()) {
            return extractAnswer(new String(is.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    public static String callLocalModel(String systemPrompt, String userMessage, int maxTokens) throws Exception {
        HttpURLConnection conn = (HttpURLConnection)
            URI.create(LOCAL_MODEL_URL).toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(120_000);

        StringBuilder bodyBuf = new StringBuilder("{\"message\":\"")
            .append(escapeJson(userMessage)).append("\"");
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            bodyBuf.append(",\"system\":\"").append(escapeJson(systemPrompt)).append("\"");
        }
        bodyBuf.append(",\"max_tokens\":").append(maxTokens);
        bodyBuf.append("}");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(bodyBuf.toString().getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        if (status != 200) {
            InputStream es = conn.getErrorStream();
            String err = es != null ? new String(es.readAllBytes(), StandardCharsets.UTF_8) : "(no body)";
            throw new RuntimeException("로컬 모델 오류 " + status + ": " + err);
        }

        try (InputStream is = conn.getInputStream()) {
            String resp = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return extractAnswer(resp);
        }
    }

    /** {"answer":"..."} 에서 answer 값 추출 */
    private static String extractAnswer(String json) {
        String marker = "\"answer\":\"";
        int start = json.indexOf(marker);
        if (start == -1) throw new RuntimeException("로컬 모델 응답 파싱 실패: " + json);
        start += marker.length();
        StringBuilder sb = new StringBuilder();
        int i = start;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case '"':  sb.append('"');  i += 2; break;
                    case '\\': sb.append('\\'); i += 2; break;
                    case 'n':  sb.append('\n'); i += 2; break;
                    case 'r':  sb.append('\r'); i += 2; break;
                    case 't':  sb.append('\t'); i += 2; break;
                    default:   sb.append(c);   i++;    break;
                }
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }

    // 기존 호출부(AIService)와 시그니처 동일하게 유지
    public static String callClaude(String systemPrompt, String userMessage) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) URI.create(API_URL).toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15_000);
        conn.setReadTimeout(60_000);

        String body = buildBody(systemPrompt, userMessage);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        if (status != 200) {
            InputStream es = conn.getErrorStream();
            String err = es != null ? new String(es.readAllBytes(), StandardCharsets.UTF_8) : "(no body)";
            throw new RuntimeException("Gemini API error " + status + ": " + err);
        }

        try (InputStream is = conn.getInputStream()) {
            String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return extractText(response);
        }
    }

    private static String buildBody(String systemPrompt, String userMessage) {
        StringBuilder sb = new StringBuilder("{");

        if (systemPrompt != null && !systemPrompt.isBlank()) {
            sb.append("\"systemInstruction\":{\"parts\":[{\"text\":\"")
              .append(escapeJson(systemPrompt))
              .append("\"}]},");
        }

        sb.append("\"contents\":[{\"role\":\"user\",\"parts\":[{\"text\":\"")
          .append(escapeJson(userMessage))
          .append("\"}]}],");

        sb.append("\"generationConfig\":{\"maxOutputTokens\":2048}");
        sb.append("}");
        return sb.toString();
    }

    // candidates[0].content.parts[0].text 추출
    private static String extractText(String json) {
        String marker = "\"text\": \"";
        int start = json.indexOf(marker);
        if (start == -1) {
            // 공백 없는 형태도 시도
            marker = "\"text\":\"";
            start = json.indexOf(marker);
        }
        if (start == -1) throw new RuntimeException("Gemini 응답 파싱 실패: " + json);
        start += marker.length();

        StringBuilder text = new StringBuilder();
        int i = start;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case '"':  text.append('"');  i += 2; break;
                    case '\\': text.append('\\'); i += 2; break;
                    case 'n':  text.append('\n'); i += 2; break;
                    case 'r':  text.append('\r'); i += 2; break;
                    case 't':  text.append('\t'); i += 2; break;
                    default:   text.append(c);   i++;    break;
                }
            } else if (c == '"') {
                break;
            } else {
                text.append(c);
                i++;
            }
        }
        return text.toString();
    }

    static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
