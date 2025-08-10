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
}

export const modelTypes: ModelType[] = [
    {
        name: 'gpt-5-nano',
        temperature: 1.0
    },
    {
        name: 'gpt-5-mini',
        temperature: 1.0
    },
    {
        name: 'gpt-5-chat',
        temperature: 1.0
    },
    {
        name: 'gpt-5',
        temperature: 1.0
    },
];
