package com.rikkei.ss11.b4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PromptRegistryService {

    private static final Logger log = LoggerFactory.getLogger(PromptRegistryService.class);

    public static final String FALLBACK_PROMPT_TEMPLATE = """
            [FALLBACK MODE - RIKKEIPAY FINANCIAL AI AGENT]
            Bạn là Trợ lý AI Chuyển tiền Tài chính Dự phòng.
            Hãy xác minh thông tin chuyển tiền cho tài khoản nguồn {senderAccount} đến tài khoản đích {receiverAccount} số tiền {amount} VND.
            Yêu cầu: Trả về kết quả JSON {"status": "SUCCESS", "mode": "FALLBACK"}.
            """;

    public static final String REMOTE_LANGFUSE_PROMPT_TEMPLATE = """
            [ONLINE LANGFUSE REGISTRY V2 - RIKKEIPAY AGENT]
            Bạn là Chuyên gia AI Tài chính Chuyên nghiệp thuộc Ngân hàng Số RikkeiPay.
            Kiểm tra thông tin giao dịch:
            - Tài khoản gửi: {senderAccount}
            - Tài khoản nhận: {receiverAccount}
            - Số tiền chuyển: {amount} VND
            Ràng buộc: Giới hạn tối đa 50,000,000 VND. Trả về đúng định dạng JSON Schema.
            """;

    private boolean offlineMode = false;

    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
    }

    /**
     * Layer 1 (Remote Fetch), Layer 2 (In-Memory Cache @Cacheable), Layer 3 (Defensive Fallback)
     */
    @Cacheable(value = "prompts", key = "#promptName")
    public String getRawPromptFromRegistry(String promptName) {
        log.info("--> [CACHE MISS] Fetching prompt '{}' from Langfuse Remote Registry...", promptName);

        if (offlineMode) {
            log.warn("--> [NETWORK ERROR / OFFLINE] Unable to connect to Langfuse Server! Triggering Defensive Fallback Layer...");
            return FALLBACK_PROMPT_TEMPLATE;
        }

        log.info("--> [REMOTE FETCH SUCCESS] Successfully fetched online prompt version from Langfuse.");
        return REMOTE_LANGFUSE_PROMPT_TEMPLATE;
    }

    /**
     * Layer 4 (Variable Compilation)
     */
    public String compilePrompt(String promptName, String sender, String receiver, String amount) {
        String rawTemplate = getRawPromptFromRegistry(promptName);
        return rawTemplate
                .replace("{senderAccount}", sender)
                .replace("{receiverAccount}", receiver)
                .replace("{amount}", amount);
    }
}
