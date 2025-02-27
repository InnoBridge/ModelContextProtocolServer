package io.github.innobridge.mcpserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.modelcontextprotocol.server.transport.WebFluxSseServerTransport;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class McpSseController {
    
    private final WebFluxSseServerTransport transport;
    
    @Autowired
    public McpSseController(WebFluxSseServerTransport transport) {
        this.transport = transport;
    }
    
    @GetMapping("/mcp/message")
    public SseEmitter handleSse() {
        log.info("SSE connection established at /mcp/message");
        SseEmitter emitter = new SseEmitter();
        // Configure with your transport
        return emitter;
    }
}
