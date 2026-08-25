# Hướng Dẫn Sử Dụng & Kiểm Thử Hệ Thống Prompt Registry 4 Lớp Phòng Thủ (4-Layer Defense)

## 1. Kiến Trúc 4 Lớp Phòng Thủ (4-Layer Defense Architecture)

```
[Client Request] 
      │
      ▼
┌─────────────────────────────────────────────────────────────┐
│ Lớp 2: In-Memory Cache (TTL = 60s)                          │
│ ──> Kiểm tra RAM. Nếu còn hiệu lực ──> Trả về ngay (Fast)  │
└──────────────────────────────┬──────────────────────────────┘
                               │ (Cache Miss / Expired)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ Lớp 1: Remote Fetch (Langfuse Registry)                     │
│ ──> Tải phiên bản Prompt mới nhất qua HTTP API             │
└──────────────────────────────┬──────────────────────────────┘
                               │ (Network Failure / Timeout)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ Lớp 3: Defensive Fallback                                   │
│ ──> Sử dụng Prompt mặc định đóng gói cứng trong mã nguồn   │
└──────────────────────────────┬──────────────────────────────┘
                               │ (Template sẵn sàng)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ Lớp 4: Variable Compilation                                 │
│ ──> Thế biến {senderAccount}, {receiverAccount}, {amount}   │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Mã Nguồn Kiểm Thử Nhanh (Main App Demo)

```java
package com.bank.defense.prompt;

import java.util.Map;

public class PromptDemoApp {
    public static void main(String[] args) {
        PromptRegistryService service = new PromptRegistryService();

        Map<String, String> variables = Map.of(
            "senderAccount", "1903123456789",
            "receiverAccount", "990011223344",
            "amount", "50,000,000"
        );

        // Lần 1: Gọi remote fetch thành công và lưu cache
        System.out.println("=== TEST LẦN 1 (Remote Fetch -> Cache) ===");
        String prompt1 = service.compilePrompt("transfer_prompt", variables);
        System.out.println("Kết quả: " + prompt1 + "\n");

        // Lần 2: Lấy từ In-Memory Cache (TTL 60s)
        System.out.println("=== TEST LẦN 2 (Hit Cache) ===");
        String prompt2 = service.compilePrompt("transfer_prompt", variables);
        System.out.println("Kết quả: " + prompt2 + "\n");

        // Lần 3: Giả lập sự cố mạng -> Chuyển sang Fallback
        System.out.println("=== TEST LẦN 3 (Network Error -> Fallback) ===");
        String prompt3 = service.compilePrompt("network_error", variables);
        System.out.println("Kết quả: " + prompt3 + "\n");
    }
}
```
