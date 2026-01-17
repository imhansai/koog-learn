package dev.fromnowon.record11

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.streaming.StreamFrame
import dev.fromnowon.llmModel
import dev.fromnowon.singleLLMPromptExecutor

suspend fun main() {

    val prompt = prompt("流式输出") {
        system("使用简体中文回答问题")
        user("简单介绍一下 koltin KMP")
    }

    singleLLMPromptExecutor.executeStreaming(prompt, llmModel)
        .collect { frame ->
            when (frame) {
                is StreamFrame.Append -> print(frame.text)
                is StreamFrame.ToolCall -> {
                    println("🔧 Tool call: ${frame.name} args=${frame.content}")
                    // Optionally parse lazily:
                    // val json = frame.contentJson
                }

                is StreamFrame.End -> println("[END] reason=${frame.finishReason}")
            }
        }

}
