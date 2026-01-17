package dev.fromnowon

import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel

// import io.ktor.client.*
// import io.ktor.client.plugins.logging.*

val llmModel = LLModel(
    provider = LLMProvider.OpenAI,
    id = "gpt-oss-20b",
    capabilities = listOf(
        LLMCapability.Temperature,
        LLMCapability.ToolChoice,
        LLMCapability.Schema.JSON.Basic,
        LLMCapability.Schema.JSON.Standard,
        LLMCapability.Speculation,
        LLMCapability.Tools,
        LLMCapability.Document,
        LLMCapability.Completion,
        LLMCapability.MultipleChoices,
        LLMCapability.OpenAIEndpoint.Completions,
        LLMCapability.OpenAIEndpoint.Responses,
    ),
    contextLength = 131_072,
    maxOutputTokens = 32_768, // 65_536
)

val llmClient = OpenAILLMClient(
    apiKey = "",
    settings = OpenAIClientSettings(
        baseUrl = "http://127.0.0.1:1234"
    ),
    // baseClient = HttpClient {
    //     install(Logging) {
    //         logger = Logger.DEFAULT
    //         level = LogLevel.ALL
    //     }
    // },
)

val singleLLMPromptExecutor = SingleLLMPromptExecutor(llmClient)