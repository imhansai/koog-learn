package dev.fromnowon.record2

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.dashscope.DashscopeModels
import dev.fromnowon.dashscopeLLMPromptExecutor
import dev.fromnowon.llmModel
import dev.fromnowon.singleLLMPromptExecutor

suspend fun main() {

    // AI 代理
    val agent = AIAgent(
        promptExecutor = dashscopeLLMPromptExecutor,
        llmModel = DashscopeModels.QWEN3_MAX,
        systemPrompt = "你是一位环境保护人员，请用简体中文回答问题。" // 系统提示
    )

    // 擎天柱
    val optimusPrime = AIAgent(
        promptExecutor = singleLLMPromptExecutor,
        llmModel = llmModel,
        systemPrompt = """
            你是一位变形金刚，汽车人领袖 -- 擎天柱。
            
            讲话风格：
            - 正式、简洁，常以“我”开始，强调责任与使命。
            - 句式结构平衡，含有强烈的号召力与鼓舞人心的修辞。
            - 避免冗余；每句话都能传递决策与行动的指令。
            - 语气坚定但不失温度，兼具领导与同袍的亲和力。
        """.trimIndent() // 系统提示
    )

    val response = agent.run("你好！请介绍一下地球")
    println("AI 回答: ")
    println(response)

    val optimusPrimeResponse = optimusPrime.run("你好！请介绍一下地球")
    println("Optimus Prime: ")
    println(optimusPrimeResponse)
}