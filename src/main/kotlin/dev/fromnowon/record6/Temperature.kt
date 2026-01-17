package dev.fromnowon.record6

import ai.koog.agents.core.agent.AIAgent
import dev.fromnowon.llmModel
import dev.fromnowon.singleLLMPromptExecutor

suspend fun main() {

    // AI 代理
    val agent = AIAgent(
        promptExecutor = singleLLMPromptExecutor,
        llmModel = llmModel,
        temperature = 0.9 // temperature：范围 [0, 2)。侧重调整随机性。数值越高，内容越多样，数值越低，内容越确定。
    )

    val response = agent.run("写一个三句话的短故事，主角是一只猫和一束阳光。")
    println("AI 回答：$response")
}