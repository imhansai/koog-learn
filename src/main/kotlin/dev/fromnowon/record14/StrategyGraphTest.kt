package dev.fromnowon.record14

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.singleRunStrategy
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.prompt.executor.clients.dashscope.DashscopeModels
import dev.fromnowon.dashscopeLLMPromptExecutor
import kotlinx.coroutines.runBlocking

/**
 * 如果 agent 是一个工厂，策略图就是生产线的设计图，每个节点是一个工作站，边是传送带，条件是品质检查点
 */
fun illustrate() = runBlocking {

    // 简单的单次执行策略
    val agent = AIAgent(
        promptExecutor = dashscopeLLMPromptExecutor,
        llmModel = DashscopeModels.QWEN3_MAX,
        strategy = singleRunStrategy(), // 简单策略
        systemPrompt = "你是一个客服助手"
    )

    // 顶级容器
    val strategy = strategy<String, String>("strategy_name") {
        // 在这里定义节点和边

        val processNode by node<String, String>("process_data") { input ->
            // 处理输入材料
            val result = "处理后的材料：$input"
            result // 返回处理结果
        }

        // 为了演示，nodeStart 和 nodeFinish 是两个特殊的节点，分别代表起点、终点

        // 基本连接
        edge(nodeStart forwardTo processNode)

        // 条件连接
        edge(nodeStart forwardTo processNode onCondition { output ->
            output.length > 10
        })

        // 转换输出
        edge(nodeStart forwardTo processNode transformed { output ->
            output.uppercase()
        })
    }

}


suspend fun main() {
    val processor = OrderProcessingAgent()

    println("=== 訂單處理策略圖演示 ===\n")

    // 測試正常訂單
    val validOrder = """
        客戶：張小明
        商品：筆記型電腦
        金額：50000
    """.trimIndent()

    println("📝 處理正常訂單：")
    try {
        val result = processor.processOrder(validOrder)
        println("\n🎯 處理結果：")
        println(result)
    } catch (e: Exception) {
        println("❌ 處理失敗：${e.message}")
    }

    println("\n" + "=".repeat(50) + "\n")

    // 測試異常訂單
    val invalidOrder = """
        客戶：李小華
        商品：智慧型手機
        // 缺少金額資訊
    """.trimIndent()

    println("📝 處理異常訂單：")
    try {
        val result = processor.processOrder(invalidOrder)
        println("\n🎯 處理結果：")
        println(result)
    } catch (e: Exception) {
        println("❌ 處理失敗：${e.message}")
    }
}
