import { provideHttpClient } from '@angular/common/http';
import { ApplicationConfig, APP_INITIALIZER, provideZonelessChangeDetection, importProvidersFrom  } from '@angular/core';
import { provideRouter, withEnabledBlockingInitialNavigation, withInMemoryScrolling } from '@angular/router';
import Aura from '@primeuix/themes/aura';
import { providePrimeNG } from 'primeng/config';
import { appRoutes } from './app.routes';

import { ApiModule } from './app/api/api.module';
import { Configuration } from './app/api/configuration';
import { environment } from './environments/environment';
import { TranslationService } from './app/core/i18n/translation.service';

function initializeTranslations(translationService: TranslationService) {
    return () =>
        translationService.init().catch(err => {
            console.error('i18n init failed (ignored):', err);
            return Promise.resolve(); // WICHTIG: App trotzdem starten
        });
}

export function apiConfigFactory(): Configuration {
    return new Configuration({
        basePath: environment.apiBasePath
    });
}

export const appConfig: ApplicationConfig = {
    providers: [
        provideRouter(appRoutes, withInMemoryScrolling({ anchorScrolling: 'enabled', scrollPositionRestoration: 'enabled' }), withEnabledBlockingInitialNavigation()),
        provideHttpClient(),
        {
            provide: APP_INITIALIZER,
            multi: true,
            useFactory: initializeTranslations,
            deps: [TranslationService]
        },
        provideZonelessChangeDetection(),
        providePrimeNG({ theme: { preset: Aura, options: { darkModeSelector: '.app-dark' } } }),
        importProvidersFrom(ApiModule.forRoot(apiConfigFactory))
    ]
};
