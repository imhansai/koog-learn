package dev.fromnowon.record3

import ai.koog.agents.core.agent.AIAgentService
import ai.koog.agents.core.agent.context.RollbackStrategy
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.onAssistantMessage
import ai.koog.agents.snapshot.feature.Persistence
import ai.koog.agents.snapshot.providers.file.JVMFilePersistenceStorageProvider
import ai.koog.prompt.message.Message
import dev.fromnowon.llmModel
import dev.fromnowon.singleLLMPromptExecutor
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.util.*

fun main() = runBlocking {

    val agentId = "persistence-test"

    val checkpointDir = Files.createTempDirectory("agent-checkpoints")
    println("Checkpoint directory: $checkpointDir")

    val provider = JVMFilePersistenceStorageProvider(checkpointDir)

    val diyStrategy = strategy("diy") {

        val node1 by node<String, Message.Response> { input ->

            // withPersistence { agentContext ->
            //     rollbackToLatestCheckpoint(agentContext)
            // }

            val messageResponse = llm.writeSession {
                appendPrompt {
                    user(input)
                }

                requestLLMWithoutTools()
            }


            // withPersistence { agentContext ->
            //     val checkpoint = agentContext.persistence().createCheckpoint(
            //         agentContext = agentContext,
            //         nodePath = "current-node-id",
            //         lastInput = input,
            //         lastInputType = agentInputType,
            //         version = 0L
            //     )
            //     val checkpointId = checkpoint?.checkpointId
            //     println(checkpointId)
            // }

            messageResponse
        }

        edge(nodeStart forwardTo node1)
        edge(node1 forwardTo nodeFinish onAssistantMessage { true })
    }

    val aiAgentService = AIAgentService(
        promptExecutor = singleLLMPromptExecutor,
        llmModel = llmModel,
        // strategy = diyStrategy,
    ) {
        install(Persistence) {
            // Use in-memory storage for snapshots ❌, need use JVMFilePersistenceStorageProvider
            storage = provider
            // Enable automatic persistence after each node
            enableAutomaticPersistence = true
            /*
             Select which state will be restored on a new agent run.

             Available options are:
             1. Default: Restores the agent to the exact execution point (node in the strategy graph) where it stopped.
                This is especially useful for building complex, fault-tolerant agents.
             2. MessageHistoryOnly: Restores only the message history to the last saved state.
                The agent will always restart from the first node in the strategy graph, but with history from previous runs.
                This is useful for building conversational agents or chatbots.
            */
            rollbackStrategy = RollbackStrategy.MessageHistoryOnly
        }
    }

    Scanner(System.`in`).use { scanner ->
        while (true) {
            print("\n用户: ")
            println("助理: ${aiAgentService.createAgent(id = agentId).run(scanner.nextLine())}")
        }
    }

}