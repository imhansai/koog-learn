package dev.fromnowon

import ai.koog.prompt.executor.clients.dashscope.DashscopeClientSettings
import ai.koog.prompt.executor.clients.dashscope.DashscopeLLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor

// import io.ktor.client.*
// import io.ktor.client.plugins.logging.*

val dashscopeLLMClient = DashscopeLLMClient(
    apiKey = checkNotNull(System.getenv("DASHSCOPE_API_KEY")),
    settings = DashscopeClientSettings(
        baseUrl = "https://dashscope.aliyuncs.com/"
    ),
    // baseClient = HttpClient {
    //     install(Logging) {
    //         logger = Logger.DEFAULT
    //         level = LogLevel.ALL
    //     }
    // }
)

val dashscopeLLMPromptExecutor = SingleLLMPromptExecutor(dashscopeLLMClient)
