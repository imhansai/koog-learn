package dev.fromnowon.record9

import ai.koog.agents.core.agent.AIAgentService
import ai.koog.agents.features.eventHandler.feature.handleEvents
import dev.fromnowon.llmModel
import dev.fromnowon.singleLLMPromptExecutor

suspend fun main() {

    val aiAgentService = AIAgentService(
        promptExecutor = singleLLMPromptExecutor,
        llmModel = llmModel
    ) {
        handleEvents {
            // Handle tool calls
            onToolCallStarting { eventContext ->
                println("Tool called: ${eventContext.toolName} with args ${eventContext.toolArgs}")
            }
            // Handle event triggered when the agent completes its execution
            onAgentCompleted { eventContext ->
                println("Agent finished with result: ${eventContext.result}")
            }

            // Other event handlers
        }

    }

    val result = aiAgentService.createAgentAndRun("你好呀")
    println(result)

}