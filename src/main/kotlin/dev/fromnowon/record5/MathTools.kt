package dev.fromnowon.record5

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Simple multiplier")
class MathTools : ToolSet {

    @Tool
    @LLMDescription("Multiplies two numbers and returns the result")
    fun multiply(a: Double, b: Double): Double {
        val result = a * b
        return result
    }

}