import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { PasswordModule } from 'primeng/password';
import { InputTextModule } from 'primeng/inputtext';
import {
    GoogleLoginProvider,
    FacebookLoginProvider
  } from '@abacritt/angularx-social-login';
  import { SocialLoginModule, SocialAuthServiceConfig } from '@abacritt/angularx-social-login';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CallbackRoutingModule } from './callback-routing.module';


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    CallbackRoutingModule,
    ButtonModule,
        CheckboxModule,
        InputTextModule,
        FormsModule,
        PasswordModule,
        ReactiveFormsModule,
        SocialLoginModule
  ]
})
export class CallbackModule { }
