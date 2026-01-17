package dev.fromnowon.record5

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.ExitTool
import dev.fromnowon.llmModel
import dev.fromnowon.singleLLMPromptExecutor

suspend fun main() {
    val agent = AIAgent(
        promptExecutor = singleLLMPromptExecutor,
        systemPrompt = "请先询问对方的姓名，然后再打招呼",
        llmModel = llmModel,
        temperature = 0.7,
        toolRegistry = ToolRegistry {
            // tool(SayToUser)
            tool(AskUser)
            tool(ExitTool)
        },
        maxIterations = 30
    )

    val result = agent.run("你好")
    println(result)
}