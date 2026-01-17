package dev.fromnowon.record3

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.dsl.extension.requestLLM
import dev.fromnowon.llmModel
import dev.fromnowon.singleLLMPromptExecutor

suspend fun main() {

    val functionalStrategy = functionalStrategy<String, Unit> {
        var userResponse = it
        while (userResponse != "/bye") {
            val responses = requestLLM(userResponse)
            println(responses.content)
            userResponse = readln()
        }
    }

    val agent = AIAgent(
        promptExecutor = singleLLMPromptExecutor,
        llmModel = llmModel,
        strategy = functionalStrategy
    )

    println("Simple chat agent started\nUse /bye to quit\nEnter your message:")
    val input = readln()
    agent.run(input)

}