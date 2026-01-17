package dev.fromnowon.record5

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Simple multiplier")
class MathTools : ToolSet {

    @Tool
    @LLMDescription("Multiplies two numbers and returns the result")
    fun multiply(@LLMDescription("第一个数") a: Double, @LLMDescription("第二个数") b: Double): Double {
        val result = a * b
        return result
    }

}