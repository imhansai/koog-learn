package dev.fromnowon.record7

import ai.koog.prompt.cache.memory.InMemoryPromptCache
import ai.koog.prompt.cache.model.PromptCache
import ai.koog.prompt.dsl.prompt
import dev.fromnowon.llmModel
import dev.fromnowon.singleLLMPromptExecutor

suspend fun main() {

    val prompt = prompt("promptCache") {
        system("使用简体中文回答问题")
        user("介绍一下Kotlin")
    }

    val cache = InMemoryPromptCache(maxEntries = 1000)

    val request = PromptCache.Request.create(prompt, emptyList())

    println("第一次提问")

    // Try to get a cached response
    var cachedResponse = cache.get(request)

    if (cachedResponse != null) {
        println("Found cached response: ${cachedResponse.first().content}")
    } else {

        val response = singleLLMPromptExecutor.execute(prompt, llmModel)

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

        val response = singleLLMPromptExecutor.execute(prompt, llmModel)

        // Cache the response
        cache.put(request, response)
        println("Cached new response: ${response.first().content}")
    }

}