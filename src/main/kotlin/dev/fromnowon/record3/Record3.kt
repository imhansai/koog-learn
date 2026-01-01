package dev.fromnowon.record3

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.dsl.extension.requestLLM
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

    val llModel = LLModel(
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

    val functionalStrategy = functionalStrategy<String, Unit> {
        var userResponse = it
        while (userResponse != "/bye") {
            val responses = requestLLM(userResponse)
            println(responses.content)
            userResponse = readln()
        }
    }

    val aiAgentService = AIAgent(
        promptExecutor = promptExecutor,
        llmModel = llModel,
        strategy = functionalStrategy
    )

    println("Simple chat agent started\nUse /bye to quit\nEnter your message:")
    val input = readln()
    aiAgentService.run(input)

}