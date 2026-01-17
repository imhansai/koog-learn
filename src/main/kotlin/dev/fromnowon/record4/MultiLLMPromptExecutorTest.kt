package dev.fromnowon.record4

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.dashscope.DashscopeModels
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.llm.LLMProvider
import dev.fromnowon.dashscopeLLMClient
import dev.fromnowon.llmClient
import dev.fromnowon.llmModel


suspend fun main() {

    val multiLLMPromptExecutor = MultiLLMPromptExecutor(
        LLMProvider.OpenAI to llmClient,
        LLMProvider.Alibaba to dashscopeLLMClient,
        fallback = MultiLLMPromptExecutor.FallbackPromptExecutorSettings(
            fallbackProvider = LLMProvider.Alibaba,
            fallbackModel = DashscopeModels.QWEN3_MAX
        )
    )

    val prompt = prompt("chat") {
        user("你现在正在使用什么模型回答问题？")
    }

    val messageResponses = multiLLMPromptExecutor.execute(
        prompt,
        llmModel
        // DashscopeModels.QWEN3_MAX
        // AnthropicModels.Opus_3 // AnthropicModels.Opus_3 将使用 fallback provider
    )
    messageResponses.forEach {
        println(it)
    }
}