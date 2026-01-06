package dev.fromnowon.record10

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.singleRunStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.agent.chatAgentStrategy
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import dev.fromnowon.llmClient
import dev.fromnowon.llmModel

/**
 * QueryRouter - 策略路由的核心類別
 * 根據問題類型自動選擇最適合的 Agent
 */
class QueryRouter {

    // 快速回應 Agent - 單次執行策略
    private val quickAgent = AIAgent(
        promptExecutor = SingleLLMPromptExecutor(llmClient),
        strategy = singleRunStrategy(),        // 簡單問題用單次執行
        systemPrompt = """
            你是一個高效客服助手，專門處理簡單查詢
            回答要直接、準確、簡潔
            使用正體中文回答
        """.trimIndent(),
        llmModel = llmModel
    )

    // 深度支援 Agent - 聊天對話策略
    private val deepAgent = ai.koog.agents.core.agent.AIAgent(
        promptExecutor = SingleLLMPromptExecutor(llmClient),
        strategy = chatAgentStrategy(),              // 複雜問題用對話策略
        systemPrompt = """
            你是一個專業的技術支援專家，能處理複雜問題
            可以與客戶多輪互動，深入了解問題並提供詳細解決方案
            使用正體中文回答
        """.trimIndent(),
        llmModel = llmModel,
        toolRegistry = ToolRegistry {
            // 可以主動向用戶說話
            tool(SayToUser)
            // 可以詢問更多資訊
            tool(AskUser)
        }
    )

    /**
     * 核心功能：判斷問題是否複雜
     * 這是基於規則的簡單分類邏輯
     *
     * 您可以根據需求調整：
     * - 增加或修改關鍵字清單
     * - 調整長度閾值（目前是 50 字）
     * - 加入其他判斷條件（如：包含特定符號、多個句子等）
     */
    private fun isComplexQuery(query: String): Boolean {
        // 複雜問題的關鍵字
        val complexKeywords = listOf(
            "分析", "比較", "問題", "故障", "無法",
            "不滿意", "投訴", "退款", "協助", "解決"
        )

        // 檢查是否包含複雜關鍵字
        val hasComplexKeywords = complexKeywords.any { query.contains(it) }

        // 長問題通常更複雜
        val isLongQuery = query.length > 50

        return hasComplexKeywords || isLongQuery
    }

    /**
     * 處理客戶查詢的主方法
     */
    suspend fun handleQuery(query: String): QueryResult {
        return try {
            val startTime = System.currentTimeMillis()

            // 這裡就是策略路由的關鍵！
            val result = if (isComplexQuery(query)) {
                println("識別為複雜問題，使用深度支援 Agent")
                deepAgent.run(query)
            } else {
                println("識別為簡單問題，使用快速回應 Agent")
                quickAgent.run(query)
            }

            val processingTime = System.currentTimeMillis() - startTime

            QueryResult(
                answer = result,
                isComplex = isComplexQuery(query),
                processingTimeMs = processingTime
            )

        } catch (e: Exception) {
            println("處理問題時發生錯誤: ${e.message}")
            QueryResult(
                answer = "抱歉，系統暫時無法處理您的問題。請稍後再試",
                isComplex = false,
                processingTimeMs = 0,
                error = e.message
            )
        }
    }
}

// 結果資料類別
data class QueryResult(
    val answer: String,
    val isComplex: Boolean,
    val processingTimeMs: Long,
    val error: String? = null
)
