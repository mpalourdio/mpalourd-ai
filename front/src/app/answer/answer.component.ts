/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import { AfterViewInit, Component, ElementRef, model, signal, ViewChild } from '@angular/core';
import { HttpService } from '../http.service';
import { FormsModule } from '@angular/forms';
import { MarkdownComponent } from 'ngx-markdown';
import { NgIf, ViewportScroller } from '@angular/common';
import { ActivatedRoute, Router } from "@angular/router";
import { HttpDownloadProgressEvent, HttpEventType } from "@angular/common/http";
import { tap } from "rxjs";

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

    @ViewChild("promptField") promptField!: ElementRef;
    prompt = model<string | null>();
    answer = signal<string>('');
    errorMessage = '';
    isCustom = false;

    constructor(private httpService: HttpService,
                private router: Router,
                private route: ActivatedRoute,
                private scroller: ViewportScroller) {
    }

    ngAfterViewInit(): void {
        this.promtFieldFocus();
        this.updateQueryParamsOnPromptChange();
        this.updatePromptOnQueryParamsChange();
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
            .request$(this.prompt(), this.isCustom)
            .pipe(
                tap(event => {
                    if (event.type === HttpEventType.DownloadProgress) {
                        this.answer.set((event as HttpDownloadProgressEvent).partialText!);
                        this.scroller.scrollToAnchor("scroll-anchor");
                    }
                })
            )
            .subscribe({
                error: () => this.errorMessage = 'An arror has occured, please retry later.'
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
