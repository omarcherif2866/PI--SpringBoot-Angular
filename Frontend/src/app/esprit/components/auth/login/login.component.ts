import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from 'src/app/esprit/service/user.service';
import { StorageService } from 'src/app/esprit/service/storage.service';
declare const gapi: any;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styles: [`
    :host ::ng-deep .pi-eye,
    :host ::ng-deep .pi-eye-slash {
      transform: scale(1.6);
      margin-right: 1rem;
      color: var(--primary-color) !important;
    }
  `]
})
export class LoginComponent implements OnInit {

  form: any = {
    username: null,
    email: null,
    password: null,
    tel: null,
    image: null,
    twoFactorCode: null
  };

  signUpForm: any = {
    username: null,
    password: null,
    twoFactorCode: null // Add twoFactorCode property
  };

  isLoggedIn = false;
  roles: string[] = [];
  isLoginFailed = false;
  errorMessage = '';

  constructor(
    private userService: UserService,
    private storageService: StorageService,
    private router: Router
  ) { }

  ngOnInit(): void {
    if (this.storageService.isLoggedIn()) {
      this.isLoggedIn = true;
      this.roles = this.storageService.getUser().roles;
    }
    this.loadGoogleSignInClient();
  }
  onButtonClick() {
    console.log('Bouton cliqué !');
    // Ajoutez ici les actions que vous souhaitez effectuer lors du clic sur le bouton
  }
  // loginWithLinkedIn(): void {
  //   const clientId = '77hourd8u6e0z3';
  //   const redirectUri = 'http://localhost:4200/auth/login';
  //   const state = this.generateRandomString(16);  // Generate a random string for state
  //   const scope = 'r_liteprofile r_emailaddress';

  //   const linkedInAuthUrl = `https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id=${clientId}&redirect_uri=${encodeURIComponent(redirectUri)}&state=${state}&scope=${scope}`;

  //   window.location.href = linkedInAuthUrl;
  // }

  // private generateRandomString(length: number): string {
  //   const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  //   let result = '';
  //   const charactersLength = characters.length;
  //   for (let i = 0; i < length; i++) {
  //     result += characters.charAt(Math.floor(Math.random() * charactersLength));
  //   }
  //   return result;
  // }

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

  onSignIn(googleUser: any): void {
    const profile = googleUser.getBasicProfile();
    const id_token = googleUser.getAuthResponse().id_token;
    console.log('ID: ' + profile.getId());
    console.log('Name: ' + profile.getName());
    console.log('Image URL: ' + profile.getImageUrl());
    console.log('Email: ' + profile.getEmail());
    console.log('ID Token: ' + id_token);

    this.storageService.setUserProfile(profile, id_token);
    this.storageService.setLoginType('google');
    this.router.navigate(['/auth/callback']);
  }

  onSubmit(): void {
    const { username, password, twoFactorCode } = this.signUpForm;
    if (this.signUpForm.username && this.signUpForm.password && this.signUpForm.twoFactorCode) {
      console.log('Form Submitted', this.signUpForm);
      this.userService.login(username, password, twoFactorCode).subscribe({
        next: data => {
          console.log(data)
          this.storageService.saveUser(data);
          this.isLoginFailed = false;
          this.isLoggedIn = true;
          this.roles = this.storageService.getUser().roles;
          this.storageService.setTokens(data.acessToken, data.refreshToken);
          console.log('Login successful');
          this.router.navigate(['/uikit/profile']);
        },
        error: err => {
          this.errorMessage = err.error.message;
          this.isLoginFailed = true;
        }
      });
    } else {
      console.log('Form not valid');
    }
  }

  attachSignin(element: any): void {
    gapi.auth2.getAuthInstance().attachClickHandler(element, {},
      (googleUser: any) => {
        this.onSignIn(googleUser);
      }, (error: any) => {
        console.error('Sign-in error', error);
      });
  }
}