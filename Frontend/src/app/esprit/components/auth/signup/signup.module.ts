import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SignupRoutingModule } from './signup-routing.module';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { FormsModule } from '@angular/forms';
import { PasswordModule } from 'primeng/password';
import { InputTextModule } from 'primeng/inputtext';
import { SignupComponent } from './signup.component';
import { ReactiveFormsModule } from '@angular/forms'; 
import { FileUploadModule } from 'primeng/fileupload'; // Adjust the path as necessary 
@NgModule({
    imports: [
        CommonModule,
        SignupRoutingModule,
        ButtonModule,
        CheckboxModule,
        InputTextModule,
        FormsModule,
        PasswordModule,
        ReactiveFormsModule,
        FileUploadModule,
    ],
    declarations: [SignupComponent]
})
export class SignupModule { }
