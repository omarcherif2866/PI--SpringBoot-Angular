import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { AppLayoutComponent } from "./layout/app.layout.component";
import { ReservationComponent } from './esprit/components/reservation/reservation.component';
import { UsersComponent } from './esprit/components/users/users.component';
import { UserGuard } from './esprit/guards/user.guard'; 
import { UserChartsComponent } from './esprit/components/user-charts/user-charts.component';

@NgModule({
    imports: [
        RouterModule.forRoot([
            { path: '', redirectTo: 'auth/login', pathMatch: 'full' },  // Redirige vers login par défaut
            {
                path: '', component: AppLayoutComponent,
                children: [
                    { path: '', loadChildren: () => import('./esprit/components/dashboard/dashboard.module').then(m => m.DashboardModule), canActivate: [UserGuard] },
                    { path: 'uikit', loadChildren: () => import('./esprit/components/uikit/uikit.module').then(m => m.UIkitModule), canActivate: [UserGuard] },
                    { path: 'utilities', loadChildren: () => import('./esprit/components/utilities/utilities.module').then(m => m.UtilitiesModule), canActivate: [UserGuard] },
                    { path: 'documentation', loadChildren: () => import('./esprit/components/documentation/documentation.module').then(m => m.DocumentationModule), canActivate: [UserGuard] },
                    { path: 'blocks', loadChildren: () => import('./esprit/components/primeblocks/primeblocks.module').then(m => m.PrimeBlocksModule), canActivate: [UserGuard] },
                    { path: 'pages', loadChildren: () => import('./esprit/components/pages/pages.module').then(m => m.PagesModule), canActivate: [UserGuard]},
                    { path: 'reservation', component: ReservationComponent, canActivate: [UserGuard] },
                    { path: 'session', loadChildren: () => import('./esprit/components/session/session.module').then(m => m.SessionModule), canActivate: [UserGuard] },
                    { path: 'activite', loadChildren: () => import('./esprit/components/activite/activite.module').then(m => m.ActiviteModule), canActivate: [UserGuard] },
                    { path: 'activityDashboard', loadChildren: () => import('./esprit/components/activity-dashboard/activity-dashboard.module').then(m => m.ActivityDashboardModule), canActivate: [UserGuard] },
                    { path: 'participerActivite', loadChildren: () => import('./esprit/components/activite-subs/activite-subs.module').then(m => m.ActiviteSubsModule), canActivate: [UserGuard] },
                    { path: 'dashboard', loadChildren: () => import('./esprit/components/dashboard/dashboard.module').then(m => m.DashboardModule), canActivate: [UserGuard] },
                    { path: 'users', component: UsersComponent, canActivate: [UserGuard] },
                    { path: 'UserCharts', component: UserChartsComponent, canActivate: [UserGuard] },
                ]
            },
            { path: 'auth', loadChildren: () => import('./esprit/components/auth/auth.module').then(m => m.AuthModule) },
            { path: 'landing', loadChildren: () => import('./esprit/components/landing/landing.module').then(m => m.LandingModule) , canActivate: [UserGuard]},
        
        ], { scrollPositionRestoration: 'enabled', anchorScrolling: 'enabled', onSameUrlNavigation: 'reload' })
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {}