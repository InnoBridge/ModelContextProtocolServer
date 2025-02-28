package io.github.innobridge.mcpserver.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.innobridge.mcpserver.tools.CalculatorTool;
import io.github.innobridge.mcpserver.tools.WeatherTool;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransport;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransport;
import io.modelcontextprotocol.spec.ServerMcpTransport;
import io.modelcontextprotocol.spec.McpSchema.Implementation;
import io.modelcontextprotocol.spec.McpSchema.LoggingLevel;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class McpConfiguration {
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "transport", name = "mode", havingValue = "webflux", matchIfMissing = true)
    public WebFluxSseServerTransport webFluxSseServerTransport(ObjectMapper mapper) {
        String endpoint = "/mcp/message";
        log.info("Creating WebFluxSseServerTransport with endpoint: {}", endpoint);
        return new WebFluxSseServerTransport(mapper, endpoint);
    }

    @Bean
    @ConditionalOnProperty(prefix = "transport", name = "mode", havingValue = "stdio")
    public StdioServerTransport stdioServerTransport() {
        log.info("Creating StdioServerTransport");
        return new StdioServerTransport();
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "transport", name = "mode", havingValue = "webflux", matchIfMissing = true)
    public RouterFunction<?> mcpRouterFunction(WebFluxSseServerTransport transport) {
        log.info("Registering RouterFunction for MCP endpoint");
        return transport.getRouterFunction();
    }
    
    @Bean(destroyMethod = "close")
    public McpSyncServer mcpSyncServer(ServerMcpTransport transport, 
                                       CalculatorTool calculatorTool,
                                       WeatherTool weatherTool) {
        log.info("Initializing McpSyncServer with transport: {}", transport);
        
        // Create a server with custom configuration
        McpSyncServer syncServer = McpServer.sync(transport)
            .serverInfo(new Implementation("my-server", "1.0.0"))
            .capabilities(ServerCapabilities.builder()
                .tools(true)         // Enable tool support
                .prompts(false)      // Change to false if not implementing prompts
                .resources(false,false)
                .logging()           // Enable logging support
                .build())
            .build();
        
        // Send initial logging notification
        syncServer.loggingNotification(LoggingMessageNotification.builder()
            .level(LoggingLevel.INFO)
            .logger("custom-logger")
            .data("Server initialized")
            .build());

        // Register the calculator tool
        var calculatorToolRegistration = new McpServerFeatures.SyncToolRegistration(
            calculatorTool.getToolDefinition(),
            calculatorTool
        );
        
        // Register the weather tool
        var weatherToolRegistration = new McpServerFeatures.SyncToolRegistration(
            weatherTool.getToolDefinition(),
            weatherTool
        );

        syncServer.addTool(calculatorToolRegistration);
        syncServer.addTool(weatherToolRegistration);

        log.info("MCP Server initialized with capabilities: tools={}, prompts={}, resources={}",
                syncServer.getServerCapabilities().tools(),
                syncServer.getServerCapabilities().prompts(),
                syncServer.getServerCapabilities().resources());
        return syncServer;
    }
}