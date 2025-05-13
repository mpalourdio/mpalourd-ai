/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mpalourdio.projects.mpalourd_ai.external

import com.fasterxml.jackson.databind.ObjectMapper
import com.mpalourdio.projects.mpalourd_ai.external.model.FlattenTree
import com.mpalourdio.projects.mpalourd_ai.external.model.Tree
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.reader.JsonReader
import org.springframework.ai.vectorstore.filter.Filter
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder
import org.springframework.ai.vectorstore.pgvector.PgVectorStore
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.io.ByteArrayResource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ExternalApiHandler(
    private val externalApiConfigurationProperties: ExternalApiConfigurationProperties,
    private val vectorStore: PgVectorStore,
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Scheduled(cron = "\${external.api.cron}")
    fun refresh() {
        log.info("========================================================")
        log.info("Beginning to refresh vector store with external API data")

        val extractedResults = fetchAndExtractResults()
        val flattenTreeAsByteArray = getAsByteArray(extractedResults)
        val documents: List<Document> = getDocumentsList(flattenTreeAsByteArray)
        cleanAndPopulateDB(documents)

        log.info("Finished refreshing vector store with external API data")
        log.info("========================================================")
    }

    private fun fetchAndExtractResults(): List<Tree> {
        log.info("Fetching external API to populate vector store")
        val apiResult = RestTemplateBuilder()
            .build()
            .getForObject(
                externalApiConfigurationProperties.url,
                Array<Tree>::class.java
            )
        // we only get the first element of the JSON array because the last array is useless.
        // the first object of this first array is useless too (/media/xxxxxx/XXXX/XXXX).
        return apiResult!![0].contents!!
    }

    private fun getAsByteArray(treeOuput: List<Tree>): ByteArray {
        val flattenTree = flattenTreeOutput(treeOuput)
        return ObjectMapper().writeValueAsString(flattenTree).encodeToByteArray()
    }

    private fun flattenTreeOutput(trees: List<Tree>): List<FlattenTree> {
        val flattenTrees = mutableListOf<FlattenTree>()
        trees.forEach { crawlTreeOutput(it, flattenTrees) }

        return flattenTrees
    }

    private fun crawlTreeOutput(tree: Tree, flattenTrees: MutableList<FlattenTree>) {
        tree.contents?.forEach {
            when {
                it.contents != null -> crawlTreeOutput(it, flattenTrees)
                else -> {
                    val toSplit = it.name!!
                    val artist = toSplit.substringBeforeLast(" -")
                    val album = toSplit.substringAfterLast("- ", toSplit).replace(" [FLAC]", "")
                    flattenTrees.add(FlattenTree(artist, album))
                }
            }
        }
    }

    private fun getDocumentsList(flattenTreeAsByteArray: ByteArray): List<Document> {
        val jsonReader = JsonReader(
            ByteArrayResource(flattenTreeAsByteArray),
            ExternalApiMetadataGenerator(),
            "artist", "album"
        )

        return jsonReader.get()
    }

    private fun populateDB(documents: List<Document>) {
        log.info("Population database with new results")
        this.vectorStore.add(documents)
    }

    private fun cleanupDB() {
        log.info("Cleaning database")
        // delete the DB before refreshing
        val expression: Filter.Expression = FilterExpressionBuilder()
            .not(FilterExpressionBuilder().eq("artist", "delete the DB before refreshing"))
            .build()

        this.vectorStore.delete(expression)
    }

    private fun cleanAndPopulateDB(documents: List<Document>) {
        cleanupDB()
        populateDB(documents)
    }
}
