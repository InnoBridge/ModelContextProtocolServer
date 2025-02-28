# ModelContextProtocolServer


## API Examples

### Connect to SSE stream
```
curl -N -H "Accept: text/event-stream" http://localhost:8081/sse
```

### Connect to stdio
Generate Java artifact
```
./mvnw clean install
```

Starting stdio server
```bash
java -Dtransport.mode=stdio \
     -Dspring.main.web-application-type=none \
     -Dspring.main.banner-mode=off \
     -Dlogging.file.name=mcpserver.log \
     -jar target/mcpserver-0.0.1-SNAPSHOT.jar
```

### Send a tool/list request
Example 

Request
```
curl -X POST -H "Content-Type: application/json" -d '{"jsonrpc":"2.0","id":2,"method":"tools/list","params":{}}' http://localhost:8081/mcp/message
```

Respone
```
event:message
data:{"jsonrpc":"2.0","id":2,"result":{"tools":[{"name":"calculator","description":"Basic calculator","inputSchema":{"type":"object","properties":{"operation":{"type":"string"},"a":{"type":"number"},"b":{"type":"number"}},"required":["operation","a","b"]}},{"name":"get_current_weather","description":"Get the current weather in a given location","inputSchema":{"type":"object","properties":{"location":{"type":"string","description":"The name of the city e.g. San Francisco, CA"},"format":{"type":"string","enum":["celsius","fahrenheit"],"description":"The format to return the weather in"}},"required":["location"]}}]}}
```

### Call the calculator tool
Example

Request
```
curl -X POST -H "Content-Type: application/json" -d '{"jsonrpc":"2.0","id":1,"method":"tools/call","params":{"name":"calculator","arguments":{"operation":"+","a":2,"b":3}}}' http://localhost:8081/mcp/message
```

Response
```
event:message
data:{"jsonrpc":"2.0","id":1,"result":{"content":[{"type":"text","type":"text","text":"2.00 + 3.00 = 5.00"}],"isError":false}}
```

### Call the weather tool
Example

Request
```
curl -X POST -H "Content-Type: application/json" -d '{"jsonrpc":"2.0","id":1,"method":"tools/call","params":{"name":"get_current_weather","arguments":{"location":"San Francisco","format":"celsius"}}}' http://localhost:8081/mcp/
```

Response
```
event:message
data:{"jsonrpc":"2.0","id":1,"result":{"content":[{"type":"text","type":"text","text":"Current weather in San Francisco, United States of America: Partly cloudy, 11.1°C, Precipitation: 0.0 mm"}],"isError":false}}