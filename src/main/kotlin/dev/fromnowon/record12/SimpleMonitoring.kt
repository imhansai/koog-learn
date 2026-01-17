package dev.fromnowon.record12

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.agents.features.opentelemetry.feature.OpenTelemetry
import dev.fromnowon.llmModel
import dev.fromnowon.singleLLMPromptExecutor
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.sdk.trace.samplers.Sampler
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

class SimpleMonitoring {

    // 建立一個帶監控功能的 Agent
    private val monitoredAgent = AIAgent(
        promptExecutor = singleLLMPromptExecutor,
        systemPrompt = """
            你是一位 AI 助手，请使用简体中文回答问题。
        """.trimIndent(),
        llmModel = llmModel,
        toolRegistry = ToolRegistry {
            tool(SayToUser)
            tool(FakeWeatherTool)
        }
    ) {
        // 安裝 OpenTelemetry 監控功能
        install(OpenTelemetry) {

            // 設定服務資訊：服務名稱和版本，用於識別和分組追蹤數據
            setServiceInfo("ai-agent-demo", "1.0.0")

            // 設定取樣速率
            setSampler(Sampler.traceIdRatioBased(0.5))

            // 開啟詳細模式，可以看到更多資訊
            setVerbose(true)

            // 新增自定的資源屬性
            addResourceAttributes(
                mapOf(
                    AttributeKey.stringKey("custom.attribute") to "custom-value",
                ),
            )

            // 使用 OTLP gRPC exporter 將數據發送到 Jaeger
            addSpanExporter(
                OtlpGrpcSpanExporter.builder()
                    .setEndpoint("http://localhost:4317") // Jaeger 的 OTLP gRPC 接收端點
                    .build()
            )

        }
    }

    suspend fun runWithMonitoring(query: String): String {
        println("🚀 開始執行查詢: $query")
        val result = monitoredAgent.run(query)
        println("✅ 查詢完成")
        return result
    }

    object FakeWeatherTool : SimpleTool<FakeWeatherTool.Args>(
        argsSerializer = Args.serializer(),
        name = "__get_weather__",
        description = "查询指定城市的天气状况"
    ) {
        @Serializable
        data class Args(

            @property:LLMDescription("要查询天气的城市名称")
            val city: String
        )

        override suspend fun execute(args: Args): String {
            // 模擬 API 呼叫延遲
            delay(2000)

            return when (args.city.lowercase()) {
                "台北", "taipei" -> "台北今天晴朗，溫度 25°C，濕度 60%"
                "高雄", "kaohsiung" -> "高雄今天多雲，溫度 28°C，濕度 70%"
                else -> "${args.city} 今天天氣良好，溫度適中"
            }
        }
    }

}

suspend fun main() {
    println("🌟 OpenTelemetry 監控演示")
    println("=".repeat(50))

    val simpleMonitoring = SimpleMonitoring()

    // 執行一個會觸發 LLM 呼叫和工具執行的查詢
    val query = "今天台北的天氣如何？"

    println("📞 用戶查詢: $query")
    println()

    val result = simpleMonitoring.runWithMonitoring(query)

    println()
    println("🤖 Agent 回應: $result")
}

