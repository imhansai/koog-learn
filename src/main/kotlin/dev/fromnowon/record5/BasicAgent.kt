package dev.fromnowon.record5

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.SayToUser
import dev.fromnowon.llmModel
import dev.fromnowon.singleLLMPromptExecutor

suspend fun main() {

    val agent = AIAgent(
        promptExecutor = singleLLMPromptExecutor,
        systemPrompt = "You are a helpful assistant. Answer user questions concisely.",
        llmModel = llmModel,
        temperature = 0.7,
        toolRegistry = ToolRegistry {
            tool(SayToUser) // SayToUser 内置工具
        },
        maxIterations = 100
    )

    val result = agent.run("Hello! How can you help me?")
    println(result)
}
