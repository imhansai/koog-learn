package dev.fromnowon.record7

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.dsl.extension.requestLLM
import ai.koog.prompt.cache.memory.InMemoryPromptCache
import ai.koog.prompt.cache.model.PromptCache
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

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

    // Create an in-memory cache with a maximum of 1000 entries
    val cache = InMemoryPromptCache(maxEntries = 1000)

    val functionalStrategy = functionalStrategy<String, Unit> {
        var userResponse = it
        while (userResponse != "/bye") {

            val prompt = prompt("chat") { user(it) }
            val request = PromptCache.Request.create(prompt, emptyList())

            // Try to get a cached response
            val cachedResponse = cache.get(request)

            if (cachedResponse != null) {
                println("Found cached response: ${cachedResponse.first().content}")
            } else {

                val response = requestLLM(userResponse)

                // Cache the response
                cache.put(request, listOf(response))
                println("Cached new response: ${response.content}")
            }

            userResponse = readln()
        }
    }

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
        strategy = functionalStrategy
    )

    agent.run("What is the capital of France?")

}