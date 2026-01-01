package dev.fromnowon.record4

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.dashscope.DashscopeClientSettings
import ai.koog.prompt.executor.clients.dashscope.DashscopeLLMClient
import ai.koog.prompt.executor.clients.dashscope.DashscopeModels
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel


suspend fun main() {

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

    val openAILLMClient = OpenAILLMClient(
        apiKey = "",
        settings = OpenAIClientSettings(
            baseUrl = "http://127.0.0.1:1234"
        )
    )

    val dashscopeLLMClient = DashscopeLLMClient(
        apiKey = checkNotNull(System.getenv("OPENAI_API_KEY")),
        settings = DashscopeClientSettings(
            baseUrl = "https://dashscope.aliyuncs.com/"
        )
    )

    val multiLLMPromptExecutor = MultiLLMPromptExecutor(
        LLMProvider.OpenAI to openAILLMClient,
        LLMProvider.Alibaba to dashscopeLLMClient,
        fallback = MultiLLMPromptExecutor.FallbackPromptExecutorSettings(
            fallbackProvider = LLMProvider.OpenAI,
            fallbackModel = openAILLModel
        )
    )

    val prompt = prompt("chat") {
        user("你现在正在使用什么模型回答问题？")
    }
    val messageResponses = multiLLMPromptExecutor.execute(
        prompt,
        DashscopeModels.QWEN_PLUS // AnthropicModels.Opus_3 将使用 fallback provider
    )
    messageResponses.forEach {
        println(it)
    }
}