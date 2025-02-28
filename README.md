# ModelContextProtocolServer

# Connect to SSE stream
```
curl -N -H "Accept: text/event-stream" http://localhost:8081/sse
```

# Send a tool/list request
```
curl -X POST -H "Content-Type: application/json" -d '{"jsonrpc":"2.0","id":2,"method":"tools/list","params":{}}' http://localhost:8081/mcp/message
```

# Call a tool
```
curl -X POST -H "Content-Type: application/json" -d '{"jsonrpc":"2.0","id":1,"method":"tools/call","params":{"name":"calculator","arguments":{"operation":"+","a":2,"b":3}}}' http://localhost:8081/mcp/message
```