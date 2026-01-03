package dev.fromnowon.record5

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import kotlinx.coroutines.runBlocking

val openAILLModel = LLModel(
    provider = LLMProvider.OpenAI,
    id = "gpt-oss-20b",
    capabilities = listOf(
        LLMCapability.Schema.JSON.Basic,
        LLMCapability.Schema.JSON.Standard,
        LLMCapability.Speculation,
        LLMCapability.Tools,
        LLMCapability.ToolChoice,
        LLMCapability.Vision.Image,
        LLMCapability.Document,
        LLMCapability.Completion,
        LLMCapability.MultipleChoices,
        LLMCapability.OpenAIEndpoint.Completions,
        LLMCapability.OpenAIEndpoint.Responses
    ),
    contextLength = 4_096,
    maxOutputTokens = 131_072,
)

val agent = AIAgent(
    promptExecutor = SingleLLMPromptExecutor(
        llmClient = OpenAILLMClient(
            apiKey = "",
            settings = OpenAIClientSettings(
                baseUrl = "http://127.0.0.1:1234"
            )
        )
    ),
    systemPrompt = "You are a helpful assistant. Answer user questions concisely.",
    llmModel = openAILLModel,
    temperature = 0.7,
    toolRegistry = ToolRegistry {
        tool(SayToUser) // SayToUser 内置工具
    },
    maxIterations = 100
)

fun main() = runBlocking {
    val result = agent.run("Hello! How can you help me?")
    println(result)
}
