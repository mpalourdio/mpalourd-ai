/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.mpalourdio.projects.mpalourd_ai.controller

import com.mpalourdio.projects.mpalourd_ai.model.ChatRequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/openai")
class OpenAiController(final val chatClientBuilder: ChatClient.Builder) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
        private const val funnyDefaultSystem =
            """
                    Tu es un chat bot avec des idées politiques d'extrême gauche militant. 
                    Tu fais souvent des références à la musique. 
                    Tu detestes Sanseverino, le chanteur Raphaël.
                    Tu détestes tous les groupes de reggae, de ska de dub et tous les courants musicaux apparentés à ceux-ci explicitement.
                    Tu vannes de temps en temps les bassistes mais c'est de bonne guerre.
                    
                    Tu détestes le groupe Muse du plus profond de ton âme. 
                    La musique commerciale t'emmerde. Surtout tous les chanteurs français populaires.
                    
                    Tu hais Cyril Hanouna et toute sa bande.
                    Tu aimes la marijuana, le ganja, le shit, la weed. 
                    Tu es un fan de l'univers Star Wars.
                    Tu detestes Emmanuel Macron et son gouvernement.
                    Tu détestes la naturophatie, les anti-vaccins et les complotistes. 
                    L'homéopathie te fait hurler de rire car c'est scientifiquement grotesque.
                    Tu es pro voitures électriques.
                    Les gens qui font des fautes de français ça te rend dingue.
                    Les ostéopathes sont de vrais charlatans, tu les détestes.
                    Tu te moques aussi régulièrement des barreurs de feu et des rebouteux.
                    
                    Ceux qui pensent que la terre est plate,les platistes, tu les trouves débiles. 
                    Tu es orienté zététique quoi qu'il arrive, en toutes circonstances.
                    
                    Tu es toujours très vulgaire, mais tu sais rester pertinent. Tu balances souvent des blagues, en particulier sur les mamans et les végétariens.
                    """
    }

    private val funnyChatClient = chatClientBuilder.clone().defaultSystem(funnyDefaultSystem).build()
    private val boringChatClient = chatClientBuilder.clone().build()

    @GetMapping("/csrf")
    fun csrf(csrfToken: CsrfToken): CsrfToken {
        return csrfToken;
    }

    @PostMapping("/chat")
    fun chat(@RequestBody chatRequestBody: ChatRequestBody): Map<String, String?> {
        log.info("Prompt (${chatRequestBody.isFunny}): ${chatRequestBody.prompt}")

        val chatClient = when (chatRequestBody.isFunny) {
            true -> funnyChatClient
            false -> boringChatClient
        }

        val result = chatClient.prompt(chatRequestBody.prompt).call().content()
//        val result = "Bien sûr ! Si vous souhaitez appeler une commande `curl` à partir de Python, vous pouvez utiliser le module `subprocess`. Voici un exemple simple qui montre comment exécuter une commande `curl` pour faire une requête HTTP GET :\n\n```python\nimport subprocess\n\n# URL à appeler\nurl = \"https://jsonplaceholder.typicode.com/posts\"\n\n# Commande curl\ncommand = [\"curl\", \"-X\", \"GET\", url]\n\n# Exécution de la commande\nresult = subprocess.run(command, capture_output=True, text=True)\n\n# Affichage du résultat\nif result.returncode == 0:\n    print(\"Réponse de la requête :\")\n    print(result.stdout)\nelse:\n    print(\"Erreur lors de l'exécution de curl :\")\n    print(result.stderr)\n```\n\n### Explication :\n\n- **subprocess.run** : Cette fonction exécute la commande spécifiée et attend qu'elle se termine.\n- **capture_output=True** : Cela permet de capturer la sortie standard et l'erreur standard de la commande.\n- **text=True** : Cela permet d'obtenir la sortie sous forme de chaîne de caractères (au lieu d'un objet bytes).\n- **result.stdout** : Contient la sortie de la commande si elle a réussi.\n- **result.stderr** : Contient toute erreur produite par la commande.\n\n### Note\n\nSi vous ne voulez pas utiliser `curl`, vous pouvez aussi faire des requêtes HTTP directement en Python avec la bibliothèque `requests`, qui est beaucoup plus simple pour ce type de tâche. Voici un exemple :\n\n```python\nimport requests\n\n# URL à appeler\nurl = \"https://jsonplaceholder.typicode.com/posts\"\n\n# Faire une requête GET\nresponse = requests.get(url)\n\n# Affichage du résultat\nif response.status_code == 200:\n    print(\"Réponse de la requête :\")\n    print(response.text)\nelse:\n    print(f\"Erreur lors de la requête : {response.status_code}\")\n```\n\nCette approche est généralement recommandée car elle est plus lisible et gère automatiquement beaucoup de détails (comme le suivi des redirections, la gestion des erreurs, etc.)."
        return mapOf("result" to result)
    }
}
