import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: '',
        loadComponent : () => import('./answer/answer.component').then(m => m.AnswerComponent)
    },
    {
        path: '**',
        redirectTo: ''
    },
];
