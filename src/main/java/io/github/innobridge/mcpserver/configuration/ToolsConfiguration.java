package io.github.innobridge.mcpserver.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.innobridge.mcpserver.tools.CalculatorTool;
import io.github.innobridge.mcpserver.tools.WeatherTool;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration for MCP tools.
 */
@Configuration
@Slf4j
public class ToolsConfiguration {
    
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
    
    @Bean
    public CalculatorTool calculatorTool() {
        log.info("Creating CalculatorTool bean");
        return new CalculatorTool();
    }
    
    @Bean
    public WeatherTool weatherTool(WebClient.Builder webClientBuilder, 
                                  @Value("${WEATHER_API_KEY:demo_key}") String apiKey) {
        log.info("Creating WeatherTool bean with API key from environment variable");
        return new WeatherTool(webClientBuilder, apiKey);
    }
}
