package util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class AIUtil {

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

    /** 문서 요약 호출 (하위호환 — 작성자 컨텍스트 보강 없이) */
    public static String callSummarize(String docType, String content) throws Exception {
        return callSummarize(docType, content, 0);
    }

    /**
     * 문서 요약 호출 (업무일지/회의록/출장보고서).
     * empId 를 함께 넘기면 서버의 SummarizeAgent 가 작성자 컨텍스트(소속·직급·최근
     * 활동)를 DB 도구로 보강해 요약에 반영한다. empId=0 이면 보강 없이 동작.
     */
    public static String callSummarize(String docType, String content, int empId) throws Exception {
        HttpURLConnection conn = (HttpURLConnection)
            URI.create(LOCAL_SUMMARIZE_URL).toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(120_000);

        String body = "{\"doc_type\":\"" + escapeJson(docType) + "\""
                    + ",\"content\":\"" + escapeJson(content) + "\""
                    + ",\"emp_id\":" + empId + "}";

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

    static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
