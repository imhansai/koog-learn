package dev.fromnowon

import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel

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
    contextLength = 4_096,
    maxOutputTokens = 131_072,
)

val llmClient = OpenAILLMClient(
    apiKey = "",
    settings = OpenAIClientSettings(
        baseUrl = "http://127.0.0.1:1234"
    )
)