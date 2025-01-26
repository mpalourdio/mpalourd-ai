/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import { ApplicationConfig, inject, provideAppInitializer, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { AppBaseHrefWithoutStaticProvider } from "./factory/app-base-href-without-static-provider.factory";
import { basehrefInterceptor$ } from "./basehref-interceptor";
import { HttpClient, provideHttpClient, withInterceptors } from "@angular/common/http";
import { pendingRequestsInterceptor$ } from "ng-http-loader";
import { firstValueFrom } from "rxjs";
import { provideMarkdown } from 'ngx-markdown';

export const appConfig: ApplicationConfig = {
    providers: [
        provideZoneChangeDetection({ eventCoalescing: true }),
        provideRouter(routes),
        AppBaseHrefWithoutStaticProvider,
        provideHttpClient(
            withInterceptors([basehrefInterceptor$, pendingRequestsInterceptor$])
        ),
        provideAppInitializer(() => {
            const http = inject(HttpClient);
            return firstValueFrom(http.get("api/openai/csrf"));
        }),
        provideMarkdown(),
    ]
};
