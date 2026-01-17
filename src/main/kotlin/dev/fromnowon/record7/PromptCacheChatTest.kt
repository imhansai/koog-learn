package dev.fromnowon.record7

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.dsl.extension.requestLLM
import ai.koog.prompt.cache.memory.InMemoryPromptCache
import ai.koog.prompt.cache.model.PromptCache
import ai.koog.prompt.dsl.prompt
import dev.fromnowon.llmModel
import dev.fromnowon.singleLLMPromptExecutor
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

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
        promptExecutor = singleLLMPromptExecutor,
        llmModel = llmModel,
        strategy = functionalStrategy
    )

    agent.run("What is the capital of France?")

}