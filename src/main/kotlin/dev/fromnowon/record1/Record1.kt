package dev.fromnowon.record1

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel

suspend fun main() {

    // 执行器
    val promptExecutor = SingleLLMPromptExecutor(
        // 可以根据情况切换为其他的 LLMClient
        llmClient = OpenAILLMClient(
            apiKey = "",
            settings = OpenAIClientSettings(
                baseUrl = "http://127.0.0.1:1234" // 这里使用 lm studio 运行本地 LLM server
            ),
            // baseClient = HttpClient {
            //     install(Logging) {
            //         logger = Logger.DEFAULT
            //         level = LogLevel.ALL
            //     }
            // },
        )
    )

    // AI 代理
    val agent = AIAgent(
        promptExecutor = promptExecutor,
        llmModel = LLModel(
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
        ),
        systemPrompt = "你是一位资深的Kotlin/Java工程师，请用简体中文回答问题。" // 系统提示
    )

    val response = agent.run("你好！请介紹一下自己")
    println("AI 回答：$response")
}