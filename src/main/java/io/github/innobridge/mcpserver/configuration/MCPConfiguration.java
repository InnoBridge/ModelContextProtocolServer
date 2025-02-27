package io.github.innobridge.mcpserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransport;
import io.modelcontextprotocol.spec.McpSchema.Implementation;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import io.modelcontextprotocol.spec.ServerMcpTransport;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebMvc
@Slf4j
public class MCPConfiguration {
    // Configuration beans will be added here
    @Bean(destroyMethod = "close")
    public McpSyncServer mcpSyncServer(ServerMcpTransport transport) {
        // Create a server with custom configuration
        McpSyncServer syncServer = McpServer.sync(transport)
            .serverInfo(new Implementation("my-server", "1.0.0"))
            .capabilities(ServerCapabilities.builder()
                .resources(true, true)  // Enable resource support with subscription
                .tools(true)         // Enable tool support
                .prompts(true)       // Enable prompt support
                .logging()           // Enable logging support
                .build())
            .build();

        // Send initial logging notification
        syncServer.loggingNotification(LoggingMessageNotification.builder()
            .level(LoggingLevel.INFO)
            .logger("custom-logger")
            .data("Server initialized")
            .build());

        log.info("MCP Server initialized");
        return syncServer;
    }

    @Bean
    WebFluxSseServerTransport webFluxSseServerTransport(ObjectMapper mapper) {
        log.info("Creating WebFluxSseServerTransport with endpoint: /mcp/message");
        return new WebFluxSseServerTransport(mapper, "/mcp/message");
    }

    @Bean
    RouterFunction<?> mcpRouterFunction(WebFluxSseServerTransport transport) {
        log.info("Registering RouterFunction for endpoint: {}", transport.getRouterFunction());
        return transport.getRouterFunction();
    }
}