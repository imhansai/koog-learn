package dev.fromnowon.record5

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.AskUser
import ai.koog.prompt.executor.clients.dashscope.DashscopeModels
import dev.fromnowon.dashscopeLLMPromptExecutor

suspend fun main() {

    val aiAgent = AIAgent(
        promptExecutor = dashscopeLLMPromptExecutor,
        systemPrompt = "请先询问对方的姓名，然后再打招呼",
        llmModel = DashscopeModels.QWEN3_MAX,
        temperature = 0.7,
        toolRegistry = ToolRegistry {
            tool(AskUser)
        },
        maxIterations = 30
    )

    val result = aiAgent.run("你好")
    println(result)
}