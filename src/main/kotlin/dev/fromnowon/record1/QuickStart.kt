package dev.fromnowon.record1

import ai.koog.agents.core.agent.AIAgent
import dev.fromnowon.llmModel
import dev.fromnowon.singleLLMPromptExecutor

suspend fun main() {

    // AI 代理
    val agent = AIAgent(
        promptExecutor = singleLLMPromptExecutor,
        llmModel = llmModel,
        systemPrompt = "你是一位资深的Kotlin/Java工程师，请用简体中文回答问题。" // 系统提示
    )

    val response = agent.run("你好！请介绍一下自己")
    println("AI 回答：$response")
}