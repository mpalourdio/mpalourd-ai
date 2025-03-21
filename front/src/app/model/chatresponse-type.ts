/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

export interface ChatResponse {
    metadata: ChatResponseMetadata;
    results:  Result[];
    result:   Result;
}

export interface ChatResponseMetadata {
    id:             string;
    model:          string;
    rateLimit:      RateLimit;
    usage:          Usage;
    promptMetadata: unknown[];
    empty:          boolean;
}

export interface RateLimit {
    requestsLimit:     number;
    requestsRemaining: number;
    requestsReset:     string;
    tokensLimit:       number;
    tokensRemaining:   number;
    tokensReset:       string;
}

export interface Usage {
    completionTokens: number;
    promptTokens:     number;
    generationTokens: number;
    totalTokens:      number;
}


export interface Result {
    metadata: ResultMetadata;
    output:   Output;
}

export interface ResultMetadata {
    finishReason:   string;
    contentFilters: unknown[];
    empty:          boolean;
}

export interface Output {
    messageType: string;
    metadata:    OutputMetadata;
    toolCalls:   unknown[];
    media:       unknown[];
    text:        string;
}

export interface OutputMetadata {
    refusal:      string;
    finishReason: string;
    index:        number;
    id:           string;
    role:         string;
    messageType:  string;
}
