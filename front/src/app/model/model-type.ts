/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

export interface ModelType {
    name: string;
    temperature: number;
    formatting?: string;
    provider: string;
}

export const modelTypes: ModelType[] = [
    {
        name: 'claude-haiku-4-5',
        temperature: 0.1,
        provider: 'anthropic'
    },
    {
        name: 'claude-sonnet-4-6',
        temperature: 0.1,
        provider: 'anthropic'
    },
    {
        name: 'claude-opus-4-6',
        temperature: 0.1,
        provider: 'anthropic'
    },
    {
        name: 'gpt-4.1-nano',
        temperature: 0.7,
        provider: 'openai'
    },
    {
        name: 'gpt-4.1-mini',
        temperature: 0.7,
        provider: 'openai'
    },
    {
        name: 'o4-mini',
        temperature: 1.0,
        formatting: 'Formatting re-enabled ',
        provider: 'openai'
    },
    {
        name: 'gpt-4.1',
        temperature: 0.7,
        provider: 'openai'
    },
];
