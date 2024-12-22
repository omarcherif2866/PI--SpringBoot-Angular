import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from 'src/app/esprit/service/user.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

declare const gapi: any;

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']  // Adjust path as per your file structure
})
export class SignupComponent implements OnInit {
  signupForm: FormGroup;
  form: any = {
    username: null,
    email: null,
    password: null,
    tel: null,
    image: null
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';

  constructor(private userService: UserService, private router: Router, private fb: FormBuilder) {
    this.signupForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      tel: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      image: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadGoogleSignInClient();
  }

  onSubmit(): void {
    if (this.signupForm.valid) {
      const { username, email, tel, password, image } = this.signupForm.value;
      console.log('Form Submitted', this.signupForm.value);
      this.userService.register(username, email, tel, password, image).subscribe(
        data => {
          console.log(data);
          this.isSuccessful = true;
          this.isSignUpFailed = false;
          window.alert('Signup successfully');
          this.router.navigate(['/auth/login']);
        },
        err => {
          this.errorMessage = err.error.message;
          this.isSignUpFailed = true;
        }
      );
    } else {
      console.log('Form not valid');
    }
  }


  loadGoogleSignInClient(): void {
    const script = document.createElement('script');
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    document.body.appendChild(script);

    script.onload = () => {
      if (typeof gapi !== 'undefined') {
        this.initializeGoogleSignIn();
      } else {
        console.error('Google API script not loaded');
      }
    };
  }

  initializeGoogleSignIn(): void {
    if (typeof gapi !== 'undefined') {
      gapi.load('auth2', () => {
        gapi.auth2.init({
          client_id: '453891221202-gg8co001vis4l6tva7nb2610alchkq95.apps.googleusercontent.com'
        }).then(() => {
          console.log('Google Sign-In client initialized.');
        }).catch((error: any) => {
          console.error('Error initializing Google Sign-In client:', error);
        });
      });
    }
  }
  onButtonClick() {
    console.log('Bouton cliqué !');
    // Ajoutez ici les actions que vous souhaitez effectuer lors du clic sur le bouton
  }
 
}