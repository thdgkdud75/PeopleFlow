package util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class AIUtil {

    private static final String API_KEY = "REMOVED_API_KEY";
    private static final String API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

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
