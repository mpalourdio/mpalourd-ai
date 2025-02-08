/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import { AfterViewInit, Component, ElementRef, model, ViewChild } from '@angular/core';
import { HttpService } from '../http.service';
import { FormsModule } from '@angular/forms';
import { Answer } from '../model/answer';
import { MarkdownComponent } from 'ngx-markdown';
import { NgIf } from '@angular/common';

@Component({
    selector: 'app-answer',
    imports: [
        FormsModule,
        MarkdownComponent,
        NgIf,
    ],
    templateUrl: './answer.component.html',
    styleUrl: './answer.component.scss'
})
export class AnswerComponent implements AfterViewInit {
    private _httpService: HttpService;

    @ViewChild("promptField") promptField!: ElementRef;
    prompt = model<string | null>();
    answer!: Answer | null;
    errorMessage = '';
    isCustom = false;

    constructor(httpService: HttpService) {
        this._httpService = httpService;
    }

    ngAfterViewInit(): void {
        this.promtFieldFocus();
    }

    request(): void {
        this.errorMessage = '';
        this.answer = null;

        this._httpService
            .request$(this.prompt(), this.isCustom)
            .subscribe({
                next: results => this.answer = results,
                error: () => this.errorMessage = 'An arror has occured, please retry later.'
            });
    }

    clear(): void {
        this.errorMessage = '';
        this.answer = null;
        this.prompt.set(null);
        this.promtFieldFocus();
    }

    private promtFieldFocus(): void {
        this.promptField.nativeElement.focus();
    }
}
