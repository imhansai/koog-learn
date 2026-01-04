package dev.fromnowon.record7

import ai.koog.prompt.cache.memory.InMemoryPromptCache
import ai.koog.prompt.cache.model.PromptCache
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel

suspend fun main() {

    val singleLLMPromptExecutor = SingleLLMPromptExecutor(
        OpenAILLMClient(
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

    val prompt = prompt("promptCache") {
        system("使用简体中文回答问题")
        user("介绍一下Kotlin")
    }

    val model = LLModel(
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

    val cache = InMemoryPromptCache(maxEntries = 1000)

    val request = PromptCache.Request.create(prompt, emptyList())

    println("第一次提问")

    // Try to get a cached response
    var cachedResponse = cache.get(request)

    if (cachedResponse != null) {
        println("Found cached response: ${cachedResponse.first().content}")
    } else {

        val response = singleLLMPromptExecutor.execute(prompt, model)

        // Cache the response
        cache.put(request, response)
        println("Cached new response: ${response.first().content}")
    }


    println("第二次提问")

    // Try to get a cached response
    cachedResponse = cache.get(request)

    if (cachedResponse != null) {
        println("Found cached response: ${cachedResponse.first().content}")
    } else {

        val response = singleLLMPromptExecutor.execute(prompt, model)

        // Cache the response
        cache.put(request, response)
        println("Cached new response: ${response.first().content}")
    }

}