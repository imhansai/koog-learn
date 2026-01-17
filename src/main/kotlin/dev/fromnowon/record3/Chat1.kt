package dev.fromnowon.record3

import ai.koog.agents.core.agent.AIAgentService
import ai.koog.agents.snapshot.feature.Persistence
import ai.koog.agents.snapshot.providers.InMemoryPersistenceStorageProvider
import dev.fromnowon.llmModel
import dev.fromnowon.singleLLMPromptExecutor
import java.util.*

suspend fun main() {

    val aiAgentService = AIAgentService(
        promptExecutor = singleLLMPromptExecutor,
        llmModel = llmModel
    ) {
        install(Persistence) {
            // Use in-memory storage for snapshots
            storage = InMemoryPersistenceStorageProvider()
            // Enable automatic persistence
            enableAutomaticPersistence = true
        }
    }

    Scanner(System.`in`).use { scanner ->
        while (true) {
            print("\n用户: ")
            println("助理: ${aiAgentService.createAgentAndRun(scanner.nextLine())}")
        }
    }

}