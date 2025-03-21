/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import { AfterViewInit, Component, ElementRef, model, OnInit, signal, ViewChild } from '@angular/core';
import { HttpService } from '../http.service';
import { FormsModule } from '@angular/forms';
import { NgIf, ViewportScroller } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { modelTypes } from '../model/model-type';
import { SseService } from "./sse.service";
import { map, tap } from "rxjs";
import { ChatResponse } from "../model/chatresponse-type";
import { MarkdownComponent } from "ngx-markdown";

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
export class AnswerComponent implements AfterViewInit, OnInit {

    @ViewChild('promptField') promptField!: ElementRef;
    prompt = model<string | null>();
    answer = signal<string>('');
    errorMessage = '';
    isCustom = false;
    models = modelTypes;
    modelType = modelTypes[0];

    constructor(private httpService: HttpService,
                private router: Router,
                private route: ActivatedRoute,
                private scroller: ViewportScroller,
                private sseService: SseService) {
    }

    ngOnInit(): void {
        this.sseService
            .sseDataObservable$
            .pipe(
                map(event => JSON.parse(event) as ChatResponse),
                tap(chatResponse => {
                    this.answer.update(answer => `${answer}${chatResponse.text}`);
                    this.scroller.scrollToAnchor("scroll-anchor");
                }))
            .subscribe({
                error: this.handleError()
            });
    }

    ngAfterViewInit(): void {
        this.promtFieldFocus();
        this.updateQueryParamsOnPromptChange();
        this.updatePromptOnQueryParamsChange();
    }

    private handleError(): () => string {
        return () => this.errorMessage = 'An arror has occured, please retry later.';
    }

    private updatePromptOnQueryParamsChange(): void {
        this.route
            .queryParams
            .subscribe(queryParams => this.prompt.set(queryParams['search']));
    }

    private updateQueryParamsOnPromptChange(): void {
        this.prompt.subscribe(value => {
            this.router.navigate([], {
                queryParams: {
                    search: value || null
                }
            }).then();
        });
    }

    request(): void {
        this.errorMessage = '';
        this.answer.set('');

        this.httpService
            .request$(this.prompt(), this.isCustom, this.modelType)
            .subscribe({
                error: this.handleError()
            });
    }

    clear(): void {
        this.errorMessage = '';
        this.answer.set('');
        this.prompt.set(null);
        this.promtFieldFocus();
    }

    private promtFieldFocus(): void {
        this.promptField.nativeElement.focus();
    }
}
