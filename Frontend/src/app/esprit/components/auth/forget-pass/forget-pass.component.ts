import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from 'src/app/esprit/service/user.service';


@Component({
  selector: 'app-forget-pass',
  templateUrl: './forget-pass.component.html',
  styles: [`
      :host ::ng-deep .pi-eye,
      :host ::ng-deep .pi-eye-slash {
          transform:scale(1.6);
          margin-right: 1rem;
          color: var(--primary-color) !important;
      }
  `]
})
export class ForgetPassComponent {
  form: any = {
    email: null,
  };

  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
  }

  onSubmit(): void {
    const { email} = this.form;

    this.userService.forgotPassword(email).subscribe(
      data => {
        console.log(data);
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        window.alert('Email sent successfully');
        this.router.navigate(['/auth/login']);
      },
      err => {
        this.errorMessage = err.error.message;
        this.isSignUpFailed = true;
      }
    );
  }
}

