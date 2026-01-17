package dev.fromnowon.record5

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.dashscope.DashscopeModels
import dev.fromnowon.dashscopeLLMPromptExecutor
import kotlinx.serialization.Serializable

suspend fun main() {

    val toolRegistry = ToolRegistry {
        tool(SayToUser)
        tool(AddTool)
    }

    val additionToolAgent = AIAgent(
        promptExecutor = dashscopeLLMPromptExecutor,
        llmModel = DashscopeModels.QWEN3_MAX,
        temperature = 0.7,
        toolRegistry = toolRegistry,
        maxIterations = 30
    )

    val result = additionToolAgent.run("计算 15+6 等于多少?")
    println(result)
}

object AddTool : SimpleTool<AddTool.Args>(
    argsSerializer = Args.serializer(),
    name = "__add_numbers__",
    description = "将两个数字相加"
) {

    @Serializable
    data class Args(

        @property:LLMDescription("第一个数字")
        val number1: Int,

        @property:LLMDescription("第二个数字")
        val number2: Int

    )

    override suspend fun execute(args: Args): String {
        return runCatching {
            val result = args.number1 + args.number2
            "计算结果: ${args.number1} + ${args.number2} = $result"
        }.getOrElse {
            "计算异常: ${it.message}"
        }
    }

}
