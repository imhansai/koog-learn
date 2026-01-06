package dev.fromnowon.record9

import ai.koog.agents.core.agent.AIAgentService
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel

suspend fun main() {

    val aiAgentService = AIAgentService(
        promptExecutor = SingleLLMPromptExecutor(
            llmClient = OpenAILLMClient(
                apiKey = "",
                settings = OpenAIClientSettings(
                    baseUrl = "http://127.0.0.1:1234"
                ),
                // baseClient = HttpClient {
                //     install(Logging) {
                //         logger = Logger.DEFAULT
                //         level = LogLevel.ALL
                //     }
                // },
            )
        ),
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
        )
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