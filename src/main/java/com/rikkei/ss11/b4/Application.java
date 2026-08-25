package com.rikkei.ss11.b4;

import com.rikkei.ss11.b4.service.PromptRegistryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final PromptRegistryService promptRegistryService;

    public Application(PromptRegistryService promptRegistryService) {
        this.promptRegistryService = promptRegistryService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=================================================");
        System.out.println("   4-LAYER PROMPT REGISTRY (CACHE & FALLBACK) - BÀI 4 (SS11)");
        System.out.println("=================================================");

        System.out.println("\n--- CALL 1 (Cache Miss - Remote Fetch) ---");
        String p1 = promptRegistryService.compilePrompt("rikkeipay-transfer-prompt", "190111", "190222", "5,000,000");
        System.out.println(p1);

        System.out.println("\n--- CALL 2 (Cache Hit - Zero Latency) ---");
        String p2 = promptRegistryService.compilePrompt("rikkeipay-transfer-prompt", "190333", "190444", "10,000,000");
        System.out.println(p2);

        System.out.println("=================================================");
    }
}
