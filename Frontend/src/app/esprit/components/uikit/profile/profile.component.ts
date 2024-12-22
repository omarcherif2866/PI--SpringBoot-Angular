import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../service/user.service';
import { StorageService } from '../../../service/storage.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  templateUrl: './profile.component.html',
  styles: [`
  :host ::ng-deep .pi-eye,
  :host ::ng-deep .pi-eye-slash {
      transform:scale(1.6);
      margin-right: 1rem;
      color: var(--primary-color) !important;
  }
`]
})
export class ProfileComponent implements OnInit {
   
  imageSrc: string | ArrayBuffer | null = 'assets/demo/images/avatar/amyelsner.png'; // Default image
  
  form: any = {
    prenom: null,
    nom: null,
    email: null,
    tel: null,
    password: null,
    image: null
  };
  loginType: string;
  isLoggedIn = false;
  errorMessage = '';
  userProfile: any;
  constructor(
    private userService: UserService,
    private storageService: StorageService,
    private router: Router
  ) { }

  ngOnInit(): void {
     if (this.storageService.isLoggedIn()) {
      this.isLoggedIn = true;
       this.loadUserProfile();
     }
     this.userProfile = this.storageService.getUserProfile();
    this.loginType = this.storageService.getLoginType();
  }
  
  loadUserProfile(): void {
    this.userService.getUserProfile().subscribe({
      next: (response: any) => {
        console.log('User profile data:', response);
  
        // Extract the data from the nested Data object
        const data = response.Data;
        
        // Ensure data structure matches the expected form
        this.form = {
          prenom: data.prenom ?? '',
          nom: data.nom ?? '',
          username: data.username ?? '',
          email: data.email ?? '',
          tel: data.tel ?? '',
          password: '', // Do not pre-fill password for security reasons
          image: data.image ?? ''
        };
        console.log('Updated form:', this.form);
      },
      error: (err: any) => {
        console.error('Error loading user profile:', err);
        this.errorMessage = 'Failed to load user profile.';
      }
    });
  }
  
  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const fileName = file.name;
      console.log(fileName); // Log the file name
  
      // Store the file name in your form (if needed)
      this.form.image = fileName;
  
      // Read the file and display it as a preview
      const reader = new FileReader();
      reader.onload = () => {
        // Assuming you have an imageSrc variable to store the base64 encoded image data
        this.imageSrc = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }
  
  

  onSubmit(): void {
    // Get the user object from storage
    const user_id = this.storageService.getUser().id;
    console.log('User id :', this.storageService.getUser().id);
   
    // Prepare the form data (make sure this.form contains the necessary properties)
    const formData = this.form;
  
    // Call the updateUserProfile method with the userId and formData
    this.userService.updateUserProfile(user_id, formData).subscribe({
      next: (data: any) => {
        // Save the updated user data to storage
        this.storageService.saveUser(data);
  
        // Navigate to the profile page on success
        this.router.navigate(['/uikit/profile']);
      },
      error: (err: any) => {
        console.error('Error updating user profile:', err);
  
        // Handle the error and set the error message
        this.errorMessage = 'Failed to update user profile.';
      }
    });
  }
}