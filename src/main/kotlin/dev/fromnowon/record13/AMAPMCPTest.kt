package dev.fromnowon.record13

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.mcp.McpToolRegistryProvider
import ai.koog.prompt.executor.clients.dashscope.DashscopeModels
import dev.fromnowon.dashscopeLLMPromptExecutor
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {

    val toolRegistry = McpToolRegistryProvider.fromTransport(
        transport = McpToolRegistryProvider.defaultSseTransport("https://mcp.amap.com/sse?key=${System.getenv("AMAP_KEY")}")
    )

    val agent = AIAgent(
        promptExecutor = dashscopeLLMPromptExecutor,
        llmModel = DashscopeModels.QWEN3_MAX,
        toolRegistry = toolRegistry,
    )
    val result = agent.run("深圳的天气怎么样?")
    println(result)

}
