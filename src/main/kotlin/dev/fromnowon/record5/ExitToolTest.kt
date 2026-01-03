package dev.fromnowon.record5

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.ExitTool
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import kotlinx.coroutines.runBlocking


val exitToolAgent = AIAgent(
    promptExecutor = SingleLLMPromptExecutor(
        llmClient = OpenAILLMClient(
            apiKey = "",
            settings = OpenAIClientSettings(
                baseUrl = "http://127.0.0.1:1234"
            )
        )
    ),
    systemPrompt = "请先询问对方的姓名，然后再打招呼",
    llmModel = openAILLModel,
    temperature = 0.7,
    toolRegistry = ToolRegistry {
        // tool(SayToUser)
        tool(AskUser)
        tool(ExitTool)
    },
    maxIterations = 30
)

fun main() = runBlocking {
    val result = exitToolAgent.run("你好")
    println(result)
}