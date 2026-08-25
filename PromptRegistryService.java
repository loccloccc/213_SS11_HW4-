package com.bank.defense.prompt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service Quản lý Prompt Tập trung 4 lớp bảo vệ (4-Layer Defense Strategy):
 * Lớp 1: Remote Fetch từ Langfuse Registry
 * Lớp 2: In-Memory Cache (TTL 60s)
 * Lớp 3: Fallback Prompt mặc định đóng gói cứng trong Code
 * Lớp 4: Variable Compilation (Thay thế biến động vào Template)
 */
public class PromptRegistryService {

    private static final Logger log = LoggerFactory.getLogger(PromptRegistryService.class);
    private static final long CACHE_TTL_MS = TimeUnit.SECONDS.toMillis(60);

    // Fallback Prompt đóng gói cứng phòng thủ sập mạng
    private static final String DEFAULT_TRANSACTION_PROMPT = 
            "Xác nhận giao dịch chuyển khoản từ tài khoản {senderAccount} đến tài khoản {receiverAccount} với số tiền {amount} VND. Vui lòng xác thực mã OTP.";

    // In-Memory Cache lưu giữ Prompt cùng mốc thời gian lưu
    private final ConcurrentHashMap<String, CacheEntry> promptCache = new ConcurrentHashMap<>();

    private record CacheEntry(String promptTemplate, long timestamp) {
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL_MS;
        }
    }

    /**
     * Lấy prompt template theo name, ưu tiên Cache -> Remote -> Fallback
     */
    public String getPromptTemplate(String promptName) {
        // Lớp 2: In-Memory Cache
        CacheEntry cached = promptCache.get(promptName);
        if (cached != null && !cached.isExpired()) {
            log.info("[Lớp 2 - Cache Hit] Lấy prompt '{}' từ bộ nhớ RAM.", promptName);
            return cached.promptTemplate();
        }

        // Lớp 1: Remote Fetch từ Langfuse Server
        try {
            log.info("[Lớp 1 - Remote Fetch] Đang tải prompt '{}' từ Langfuse Registry...", promptName);
            String remotePrompt = fetchFromLangfuseRegistry(promptName);
            if (remotePrompt != null && !remotePrompt.isBlank()) {
                // Lưu vào cache
                promptCache.put(promptName, new CacheEntry(remotePrompt, System.currentTimeMillis()));
                return remotePrompt;
            }
        } catch (Exception e) {
            log.error("[Lớp 1 - Failed] Không thể kết nối Langfuse Registry: {}", e.getMessage());
        }

        // Lớp 3: Defensive Fallback
        log.warn("[Lớp 3 - Fallback] Kích hoạt Fallback Prompt mặc định cho '{}'.", promptName);
        return DEFAULT_TRANSACTION_PROMPT;
    }

    /**
     * Lớp 4: Biên dịch prompt bằng cách thay thế các biến động vào template
     */
    public String compilePrompt(String promptName, Map<String, String> variables) {
        String template = getPromptTemplate(promptName);
        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                String value = entry.getValue() != null ? entry.getValue() : "";
                template = template.replace(placeholder, value);
            }
        }
        return template;
    }

    /**
     * Giả lập việc gọi API Langfuse Prompt Registry
     */
    private String fetchFromLangfuseRegistry(String promptName) throws Exception {
        // Giả lập kịch bản kết nối thực tế
        if ("network_error".equals(promptName)) {
            throw new RuntimeException("Connection Timeout: Unable to reach http://localhost:3000");
        }
        return "Yêu cầu chuyển khoản ngân hàng: Tài khoản nguồn {senderAccount}, Tài khoản đích {receiverAccount}, Hạn mức chuyển {amount} VND. Trạng thái: Chờ xác thực.";
    }
}
