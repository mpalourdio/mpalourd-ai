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
        name: 'gpt-4o-mini',
        temperature: 0.7
    },
    {
        name: 'gpt-4o',
        temperature: 0.7
    },
    {
        name: 'o3-mini',
        temperature: 1.0,
        formatting: 'Formatting re-enabled '
    },
];
