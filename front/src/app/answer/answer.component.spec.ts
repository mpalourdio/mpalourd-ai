import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnswerComponent } from './answer.component';
import { provideHttpClient } from "@angular/common/http";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe('AnswerComponent', () => {
    let component: AnswerComponent;
    let fixture: ComponentFixture<AnswerComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [AnswerComponent],
            providers: [
                provideHttpClient(),
                provideHttpClientTesting(),
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(AnswerComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
