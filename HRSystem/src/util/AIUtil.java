package util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class AIUtil {
    // TODO: AI 기능 연결 시 ANTHROPIC_API_KEY 환경변수 설정 후 주석 해제
    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-sonnet-4-6";

    public static String callClaude(String systemPrompt, String userMessage) throws Exception {
        String apiKey = System.getenv("ANTHROPIC_API_KEY");
        if (apiKey == null || apiKey.isBlank())
            throw new UnsupportedOperationException("ANTHROPIC_API_KEY 환경변수를 설정한 후 사용하세요.");

        HttpURLConnection conn = (HttpURLConnection) URI.create(API_URL).toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("x-api-key", apiKey);
        conn.setRequestProperty("anthropic-version", "2023-06-01");
        conn.setDoOutput(true);

        String body = buildRequestBody(systemPrompt, userMessage);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        if (conn.getResponseCode() != 200) {
            String err = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            throw new RuntimeException("Claude API error " + conn.getResponseCode() + ": " + err);
        }

        try (InputStream is = conn.getInputStream()) {
            String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return extractText(response);
        }
    }

    private static String buildRequestBody(String systemPrompt, String userMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"model\":\"").append(MODEL).append("\",");
        sb.append("\"max_tokens\":2048,");
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            sb.append("\"system\":\"").append(escapeJson(systemPrompt)).append("\",");
        }
        sb.append("\"messages\":[{\"role\":\"user\",\"content\":\"")
          .append(escapeJson(userMessage))
          .append("\"}]");
        sb.append("}");
        return sb.toString();
    }

    // JSON 응답에서 content[0].text 값만 추출
    private static String extractText(String json) {
        String marker = "\"text\":\"";
        int start = json.indexOf(marker);
        if (start == -1) throw new RuntimeException("API 응답 파싱 실패: " + json);
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
