package dev.fromnowon.record11.streamingagentwithtools

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.GraphAIAgent.FeatureContext
import ai.koog.agents.core.agent.asMermaidDiagram
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeExecuteMultipleTools
import ai.koog.agents.core.dsl.extension.nodeLLMRequestStreamingAndSendResults
import ai.koog.agents.core.dsl.extension.onMultipleToolCalls
import ai.koog.agents.core.environment.ReceivedToolResult
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.message.Message
import ai.koog.prompt.message.RequestMetaInfo
import ai.koog.prompt.streaming.StreamFrame
import dev.fromnowon.llmClient
import dev.fromnowon.llmModel

suspend fun main() {
    val mermaidDiagram = streamingWithToolsStrategy().asMermaidDiagram()
    println(mermaidDiagram)

    val switch = Switch()

    val toolRegistry = ToolRegistry {
        tools(SwitchTools(switch).asTools())
    }

    val agent = openAiAgent(toolRegistry) {
        handleEvents {
            onToolCallStarting { context ->
                println("\n🔧 Using ${context.toolName} with ${context.toolArgs}... ")
            }
            onLLMStreamingFrameReceived { context ->
                (context.streamFrame as? StreamFrame.Append)?.let { frame ->
                    print(frame.text)
                }
            }
            onLLMStreamingFailed {
                println("❌ Error: ${it.error}")
            }
            onLLMStreamingCompleted {
                println("")
            }
        }
    }

    println("Streaming chat agent started\nUse /quit to quit\nEnter your message:")
    var input = ""
    while (input != "/quit") {
        input = readln()

        // Example message:
        // Tell me if the switch if on or off. Elaborate on how you will determine that. After that, if it was off, turn it on. Be very verbose in all the steps

        agent.run(input)

        println()
        println("Enter your message:")
    }
}

private fun openAiAgent(
    toolRegistry: ToolRegistry,
    installFeatures: FeatureContext.() -> Unit = {}
) = AIAgent(
    promptExecutor = SingleLLMPromptExecutor(llmClient),
    strategy = streamingWithToolsStrategy(),
    llmModel = llmModel,
    systemPrompt = "You're responsible for running a Switch and perform operations on it by request",
    temperature = 0.0,
    toolRegistry = toolRegistry,
    installFeatures = installFeatures
)

fun streamingWithToolsStrategy() = strategy("streaming_loop") {
    val executeMultipleTools by nodeExecuteMultipleTools(parallelTools = true)
    val nodeStreaming by nodeLLMRequestStreamingAndSendResults()

    val mapStringToRequests by node<String, List<Message.Request>> { input ->
        listOf(Message.User(content = input, metaInfo = RequestMetaInfo.Empty))
    }

    val applyRequestToSession by node<List<Message.Request>, List<Message.Request>> { input ->
        llm.writeSession {
            appendPrompt {
                input.filterIsInstance<Message.User>()
                    .forEach {
                        user(it.content)
                    }

                tool {
                    input.filterIsInstance<Message.Tool.Result>()
                        .forEach {
                            result(it)
                        }
                }
            }
            input
        }
    }

    val mapToolCallsToRequests by node<List<ReceivedToolResult>, List<Message.Request>> { input ->
        input.map { it.toMessage() }
    }

    edge(nodeStart forwardTo mapStringToRequests)
    edge(mapStringToRequests forwardTo applyRequestToSession)
    edge(applyRequestToSession forwardTo nodeStreaming)
    edge(nodeStreaming forwardTo executeMultipleTools onMultipleToolCalls { true })
    edge(executeMultipleTools forwardTo mapToolCallsToRequests)
    edge(mapToolCallsToRequests forwardTo applyRequestToSession)
    edge(
        nodeStreaming forwardTo nodeFinish onCondition {
            it.filterIsInstance<Message.Tool.Call>().isEmpty()
        }
    )
}
