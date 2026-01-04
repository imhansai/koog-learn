package dev.fromnowon.record5

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    // Create an agent
    val agent = AIAgent(
        promptExecutor = SingleLLMPromptExecutor(
            OpenAILLMClient(
                apiKey = "",
                settings = OpenAIClientSettings(
                    baseUrl = "http://127.0.0.1:1234"
                )
            )
        ),
        llmModel = openAILLModel,
        toolRegistry = ToolRegistry {
            tools(MathTools().asTools())
        },
        systemPrompt = "You are a precise math assistant. When multiplication is needed, use the multiplication tool.",
        strategy = functionalStrategy<String, String> { input -> // Define the agent logic extended with tool calls
            // Send the user input to the LLM
            var responses = requestLLMMultiple(input)

            // Only loop while the LLM requests tools
            while (responses.containsToolCalls()) {
                // Extract tool calls from the response
                val pendingCalls = extractToolCalls(responses)
                // Execute the tools and return the results
                val results = executeMultipleTools(pendingCalls)
                // Send the tool results back to the LLM. The LLM may call more tools or return a final output
                responses = sendMultipleToolResults(results)
            }

            // When no tool calls remain, extract and return the assistant message content from the response
            responses.single().asAssistantMessage().content
        }
    )

    // Run the agent
    val result = agent.run("Please multiply 12.5 and 4, then add 10 to the result.")
    println(result)
}
