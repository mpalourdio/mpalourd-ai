import { Observable, Subject } from "rxjs";
import { Injectable } from "@angular/core";

@Injectable({
    providedIn: 'root'
})
export class SseService {

    private eventSource: EventSource;
    private sseDataSubject$ = new Subject<string>();
    private _sseDataObservable$ = this.sseDataSubject$.asObservable();

    constructor() {
        this.eventSource = new EventSource('api/openai/chat');
        this.connectToSSE();
    }

    get sseDataObservable$(): Observable<string> {
        return this._sseDataObservable$;
    }

    private connectToSSE(): void {
        this.eventSource.onmessage = (event: MessageEvent<string>): void => {
            this.sseDataSubject$.next(event.data);
        };

        this.eventSource.onerror = (error): void => {
            this.sseDataSubject$.error(error);
            this.eventSource.close();
            this.connectToSSE();
        };
    }
}
