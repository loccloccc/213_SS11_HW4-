# BÀI 4: QUẢN LÝ PROMPT TẬP TRUNG VỚI LANGFUSE PROMPT REGISTRY KÈM CACHING & FALLBACK

## 📌 1. Đồ Họa Kiến Trúc Phòng Thủ 4 Lớp (4-Layer Prompt Defense Architecture)

```
Client App Request (chuyển tiền AI)
       │
       ▼
[Layer 2: In-Memory Cache (TTL 60s)] ──── (HIT) ───► Trả về Prompt ngay lập tức (Latency ~0ms)
       │ (MISS)
       ▼
[Layer 1: Remote Fetch (Langfuse Registry)] ─── (SUCCESS) ───► Lưu Cache & Trả về Prompt V2
       │ (NETWORK ERROR / DOWN)
       ▼
[Layer 3: Defensive Fallback] ───► Dùng Hardcoded Local Prompt Template
       │
       ▼
[Layer 4: Variable Compilation] ───► Replace {senderAccount}, {receiverAccount}, {amount}
       │
       ▼
LLM Chat Model
```

---

## 📌 2. Lợi Ích Kỹ Thuật
1. **Zero-Latency cho 99% Request**: Bộ nhớ Cache trong RAM giúp loại bỏ hoàn toàn Network Round-trip Time khi lấy Prompt cho từng giao dịch.
2. **Khả năng Chịu lỗi Cao (High Availability)**: Khi Langfuse Server gặp sự cố đứt cáp hoặc sập hệ thống, ứng dụng tự động dùng Local Fallback Prompt để duy trì dịch vụ chuyển tiền ngân hàng liên tục 24/7.
"# 213_SS11_HW4-" 
