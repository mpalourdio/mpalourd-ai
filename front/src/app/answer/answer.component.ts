import { Component } from '@angular/core';
import { HttpService } from "../http.service";

@Component({
    selector: 'app-answer',
    imports: [],
    templateUrl: './answer.component.html',
    styleUrl: './answer.component.scss'
})
export class AnswerComponent {
    private _httpService: HttpService;

    constructor(httpService: HttpService) {
        this._httpService = httpService;
    }
}
