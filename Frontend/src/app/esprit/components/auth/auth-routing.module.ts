import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
    imports: [RouterModule.forChild([
        { path: 'error', loadChildren: () => import('./error/error.module').then(m => m.ErrorModule) },
        { path: 'access', loadChildren: () => import('./access/access.module').then(m => m.AccessModule) },
        { path: 'login', loadChildren: () => import('./login/login.module').then(m => m.LoginModule) },
        { path: 'signup', loadChildren: () => import('./signup/signup.module').then(m => m.SignupModule) },
        { path: 'forgetpassword', loadChildren: () => import('./forget-pass/forget-pass.module').then(m => m.ForgetPassModule) },
        { path: 'callback', loadChildren: () => import('./callback/callback.module').then(m => m.CallbackModule) }
    ])],
    exports: [RouterModule]
})
export class AuthRoutingModule {}
