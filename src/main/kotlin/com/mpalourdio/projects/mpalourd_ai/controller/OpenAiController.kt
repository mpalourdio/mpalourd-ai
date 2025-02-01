/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.mpalourdio.projects.mpalourd_ai.controller

import com.mpalourdio.projects.mpalourd_ai.config.AiConfigurationProperties
import com.mpalourdio.projects.mpalourd_ai.model.ChatRequestBody
import jakarta.servlet.http.HttpSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.*
import java.io.File

@RestController
@RequestMapping("/api/openai")
class OpenAiController(
    chatClientBuilder: ChatClient.Builder,
    aiConfigurationProperties: AiConfigurationProperties,
    private val openAiChatProperties: OpenAiChatProperties,
    private val session: HttpSession
) {
    private final val customDefaultSystem: String =
        File(aiConfigurationProperties.defaultSystemFilePath).readText(Charsets.UTF_8)

    private final val messageChatMemoryAdvisor = MessageChatMemoryAdvisor(InMemoryChatMemory())

    private final val customChatClient = chatClientBuilder.clone()
        .defaultSystem(customDefaultSystem)
        .defaultAdvisors(messageChatMemoryAdvisor)
        .build()

    private final val boringChatClient = chatClientBuilder.clone()
        .defaultAdvisors(messageChatMemoryAdvisor)
        .build()

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    init {
        log.info("Custom default system: \n${this.customDefaultSystem}")
        log.info("Chat Model in use: ${this.openAiChatProperties.options.model}")
    }

    @GetMapping("/csrf")
    fun csrf(csrfToken: CsrfToken): CsrfToken {
        return csrfToken;
    }

    @PostMapping("/chat")
    fun chat(@RequestBody chatRequestBody: ChatRequestBody): Map<String, String?> {
        log.info("Prompt (${chatRequestBody.isCustom}): ${chatRequestBody.prompt}, for session ${session.id}")

        val chatClient = when (chatRequestBody.isCustom) {
            true -> customChatClient
            false -> boringChatClient
        }

        val result = chatClient.prompt(chatRequestBody.prompt)
            .advisors { a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, session.id) }
            .call()
            .content()

//        val result = "Bien sûr ! Si vous souhaitez appeler une commande `curl` à partir de Python, vous pouvez utiliser le module `subprocess`. Voici un exemple simple qui montre comment exécuter une commande `curl` pour faire une requête HTTP GET :\n\n```python\nimport subprocess\n\n# URL à appeler\nurl = \"https://jsonplaceholder.typicode.com/posts\"\n\n# Commande curl\ncommand = [\"curl\", \"-X\", \"GET\", url]\n\n# Exécution de la commande\nresult = subprocess.run(command, capture_output=True, text=True)\n\n# Affichage du résultat\nif result.returncode == 0:\n    print(\"Réponse de la requête :\")\n    print(result.stdout)\nelse:\n    print(\"Erreur lors de l'exécution de curl :\")\n    print(result.stderr)\n```\n\n### Explication :\n\n- **subprocess.run** : Cette fonction exécute la commande spécifiée et attend qu'elle se termine.\n- **capture_output=True** : Cela permet de capturer la sortie standard et l'erreur standard de la commande.\n- **text=True** : Cela permet d'obtenir la sortie sous forme de chaîne de caractères (au lieu d'un objet bytes).\n- **result.stdout** : Contient la sortie de la commande si elle a réussi.\n- **result.stderr** : Contient toute erreur produite par la commande.\n\n### Note\n\nSi vous ne voulez pas utiliser `curl`, vous pouvez aussi faire des requêtes HTTP directement en Python avec la bibliothèque `requests`, qui est beaucoup plus simple pour ce type de tâche. Voici un exemple :\n\n```python\nimport requests\n\n# URL à appeler\nurl = \"https://jsonplaceholder.typicode.com/posts\"\n\n# Faire une requête GET\nresponse = requests.get(url)\n\n# Affichage du résultat\nif response.status_code == 200:\n    print(\"Réponse de la requête :\")\n    print(response.text)\nelse:\n    print(f\"Erreur lors de la requête : {response.status_code}\")\n```\n\nCette approche est généralement recommandée car elle est plus lisible et gère automatiquement beaucoup de détails (comme le suivi des redirections, la gestion des erreurs, etc.)."
        return mapOf("result" to result)
    }
}
