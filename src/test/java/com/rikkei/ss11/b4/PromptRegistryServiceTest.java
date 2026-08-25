package com.rikkei.ss11.b4;

import com.rikkei.ss11.b4.service.PromptRegistryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PromptRegistryServiceTest {

    @Autowired
    private PromptRegistryService promptRegistryService;

    @Test
    @DisplayName("PromptRegistryService correctly fetches remote prompt and binds dynamic variables")
    void testPromptCompilationAndFallback() {
        promptRegistryService.setOfflineMode(false);
        String compiled = promptRegistryService.compilePrompt("p1", "ACC_A", "ACC_B", "1,000,000");

        assertNotNull(compiled);
        assertTrue(compiled.contains("ACC_A"));
        assertTrue(compiled.contains("ACC_B"));
        assertTrue(compiled.contains("1,000,000"));
    }
}
