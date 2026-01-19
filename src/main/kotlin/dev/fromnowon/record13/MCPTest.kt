package dev.fromnowon.record13

import ai.koog.agents.core.agent.AIAgentService
import ai.koog.agents.core.agent.context.RollbackStrategy
import ai.koog.agents.mcp.McpToolRegistryProvider
import ai.koog.agents.mcp.defaultStdioTransport
import ai.koog.agents.snapshot.feature.Persistence
import ai.koog.agents.snapshot.providers.file.JVMFilePersistenceStorageProvider
import ai.koog.prompt.executor.clients.dashscope.DashscopeModels
import dev.fromnowon.dashscopeLLMPromptExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.util.*

fun main() = runBlocking {

    val agentId = "persistence-test"

    val checkpointDir = Files.createTempDirectory("agent-checkpoints")
    println("Checkpoint directory: $checkpointDir")

    val provider = JVMFilePersistenceStorageProvider(checkpointDir)

    // Start an MCP server (for example, as a process)
    val process = withContext(Dispatchers.IO) {
        ProcessBuilder(
            "java",
            "-jar",
            "/Users/hansai/IdeaProjects/weifangbus-mcp-server/build/libs/weifangbus-mcp-server-0.0.1-SNAPSHOT.jar"
        ).start()
    }

    // Create the stdio transport
    val transport = McpToolRegistryProvider.defaultStdioTransport(process)

    // Create a tool registry with tools from the MCP server
    val toolRegistry = McpToolRegistryProvider.fromTransport(
        transport = transport,
        name = "my-client",
        version = "1.0.0"
    )

    val aiAgentService = AIAgentService(
        promptExecutor = dashscopeLLMPromptExecutor,
        llmModel = DashscopeModels.QWEN3_MAX,
        toolRegistry = toolRegistry,
    ) {
        install(Persistence) {
            storage = provider
            enableAutomaticPersistence = true
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