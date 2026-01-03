# Tool  

本质上是 LLM Function Calling  

Received request: POST to /v1/chat/completions with body:
```json
{
  "messages": [
    {
      "role": "system",
      "content": "You are a helpful assistant. Answer user questions concisely."
    },
    {
      "role": "user",
      "content": "Hello! How can you help me?"
    }
  ],
  "model": "gpt-oss-20b",
  "n": 1,
  "stream": false,
  "temperature": 0.7,
  "tools": [
    {
      "function": {
        "name": "say_to_user",
        "description": "Service tool, used by the agent to talk.",
        "parameters": {
          "type": "object",
          "properties": {
            "message": {
              "description": "Message from the agent",
              "type": "string"
            }
          },
          "required": [
            "message"
          ]
        }
      },
      "type": "function"
    }
  ]
}
```  

Generated prediction:
```json
{
  "id": "chatcmpl-24v6oiaeceiph3d9rr6i0j",
  "object": "chat.completion",
  "created": 1767411340,
  "model": "gpt-oss-20b",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "",
        "reasoning": "User greeting. Need to respond concisely, explain capabilities.",
        "tool_calls": [
          {
            "type": "function",
            "id": "910617247",
            "function": {
              "name": "say_to_user",
              "arguments": "{\"message\":\"Hello! I can help with a wide range of tasks—answering questions, providing explanations, generating ideas or content, assisting with programming, offering study help, and more. Just let me know what you need!\"}"
            }
          }
        ]
      },
      "logprobs": null,
      "finish_reason": "tool_calls"
    }
  ],
  "usage": {
    "prompt_tokens": 153,
    "completion_tokens": 80,
    "total_tokens": 233
  },
  "stats": {},
  "system_fingerprint": "gpt-oss-20b"
}
```  

Received request: POST to /v1/chat/completions with body:
```json
{
  "messages": [
    {
      "role": "system",
      "content": "You are a helpful assistant. Answer user questions concisely."
    },
    {
      "role": "user",
      "content": "Hello! How can you help me?"
    },
    {
      "role": "assistant",
      "tool_calls": [
        {
          "id": "910617247",
          "function": {
            "name": "say_to_user",
            "arguments": "{\"message\":\"Hello! I can help with a wide range of... <Truncated in logs> ... help, and more. Just let me know what you need!\"}"
          },
          "type": "function"
        }
      ]
    },
    {
      "role": "tool",
      "content": "DONE",
      "tool_call_id": "910617247"
    }
  ],
  "model": "gpt-oss-20b",
  "n": 1,
  "stream": false,
  "temperature": 0.7,
  "tools": [
    {
      "function": {
        "name": "say_to_user",
        "description": "Service tool, used by the agent to talk.",
        "parameters": {
          "type": "object",
          "properties": {
            "message": {
              "description": "Message from the agent",
              "type": "string"
            }
          },
          "required": [
            "message"
          ]
        }
      },
      "type": "function"
    }
  ]
}
```  

Generated prediction: 
```json
{
  "id": "chatcmpl-b7286sxhscs3x8mzsj928u",
  "object": "chat.completion",
  "created": 1767411343,
  "model": "gpt-oss-20b",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "Sure! I can help with questions, explanations, brainstorming ideas, coding support, study help, creative writing, and more. Just let me know what you need!",
        "reasoning": "The user says: \"Hello! How can you help me?\" The assistant responded with a generic helpful statement. That likely meets the instruction: \"Answer user questions concisely.\" The assistant did that.\n\nBut maybe we should ask what they need. The instructions: \"You are a helpful assistant. Answer user questions concisely.\" So maybe we should ask what they need. But the response was okay.\n\nWe might want to respond with a brief question: \"Sure, what do you need help with?\" But the user asked how can I help them. The assistant responded basically telling what it can do, which is fine.\n\nWe should keep answer concise. It's done. No further action needed unless user asks more.\n\nThus we can keep the answer.",
        "tool_calls": []
      },
      "logprobs": null,
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 228,
    "completion_tokens": 194,
    "total_tokens": 422
  },
  "stats": {},
  "system_fingerprint": "gpt-oss-20b"
}
```