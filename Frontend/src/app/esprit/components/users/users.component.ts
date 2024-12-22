import { Component, OnInit } from '@angular/core';
import { User } from '../../models/user';
import { UserService } from '../../service/user.service';
import { MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { ToolbarModule } from 'primeng/toolbar';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { CommonModule } from '@angular/common';
import { DropdownModule } from 'primeng/dropdown';
import { StorageService } from '../../service/storage.service';
import { NavigationExtras, Router } from '@angular/router';



@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, ToolbarModule, TableModule, ButtonModule, DialogModule,DropdownModule],
  providers: [MessageService],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit {
  UserDialog: boolean = false;
  deleteUserDialog: boolean = false;
  deleteUsersDialog: boolean = false;
  Users: User[] = [];
  User: User = {};
  selectedUsers: User[] = [];
  submitted: boolean = false;
  cols: any[] = [];
  statuses: any[] = [];
  rowsPerPageOptions = [5, 10, 20];
  availableRoles: any[] = [];
  selectedStatuses: any;
  constructor(private userService: UserService, private router: Router, private messageService: MessageService,  private storageService: StorageService,) { }

  ngOnInit() {
    this.userService.getUsers().subscribe(
      data => this.Users = data,
      error => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load users', life: 3000 });
      }
    );

    

    this.cols = [
      { field: 'id', header: 'ID' },
      { field: 'username', header: 'Username' },
      { field: 'email', header: 'Email' },
      { field: 'tel', header: 'Phone' },
      { field: 'role', header: 'Role' },
      { field: 'role', header: 'Role_Change'},
      { field: 'activated', header: 'Status'}
    ];

    this.statuses = [
      { label: 'INSTOCK', value: 'instock' },
      { label: 'LOWSTOCK', value: 'lowstock' },
      { label: 'OUTOFSTOCK', value: 'outofstock' }
    ];

    this.availableRoles = [
      { label: 'ROLE_MEMBRE', value: 'ROLE_MEMBRE' },
      { label: 'ROLE_RESPONSABLE', value: 'ROLE_RESPONSABLE' }
    ];
  }

  openNew() {
      this.User = {};
      this.submitted = false;
      this.UserDialog = true;
  }

  deleteSelectedUsers() {
      this.deleteUsersDialog = true;
  }


  private reloadPage() {
    const currentUrl = this.router.url;
    const navigationExtras: NavigationExtras = {
      queryParams: { 'refresh': new Date().getTime() } // Add a unique query param to force reload
    };
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate([currentUrl], navigationExtras);
    });
  }


  unBanUser(user: User) {
    this.userService.unblockUser(user.id).subscribe(
      () => {
        console.log(`User with ID `+ user.id+` unblocked successfully.`);
        this.reloadPage();
        alert('User with ID '+ user.id +'a été activé avec succès');
        
      },
      error => {
        console.error('Error unblocking user:', error);
        // Optionally, handle error message or update UI
      }
    );
  }

  BanUser(user: User) {
    this.userService.blockUser(user.id).subscribe(
      () => {
        console.log(`User with ID `+ user.id+` blocked successfully.`);
        this.reloadPage();
        alert('User with ID '+ user.id+'a été blocké avec succès');
      },
      error => {
        console.error('Error blocking user:', error);
        // Optionally, handle error message or update UI
      }
    );
  }


updateUserRole(user: User): void {
  // Vérifier et ajuster le rôle si nécessaire
  const newRole = user.role === 'ROLE_RESPONSABLE' ? 'ROLE_MEMBRE' :
                 user.role === 'ROLE_MEMBRE' ? 'ROLE_RESPONSABLE' : user.role;

  // Appeler le service pour mettre à jour le rôle
  this.userService.updateRole(user.id, newRole).subscribe({
    next: (updatedUser: User) => {
      console.log(updatedUser);
      alert('Le rôle de l\'utilisateur a été mis à jour avec succès');
      this.reloadPage();
    },
    error: (error) => {
      console.error(error);
      alert('Échec de la mise à jour du rôle de l\'utilisateur');
    }
  });
}
  

  deleteUser(user: User): void {
  if (!user || !user.id) {
    return;
  }
  this.userService.deleteUser(user.id).subscribe(
    response => {
      console.log('User deleted successfully:', response);
      this.messageService.add({ severity: 'success', summary: 'Success', detail: 'User deleted successfully' });
    },
    error => {
      console.error('Failed to delete user:', error);
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete user' });
    }
  );
}


  confirmDeleteSelected() {
      this.deleteUsersDialog = false;
      this.Users = this.Users.filter(val => !this.selectedUsers.includes(val));
      this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'Users Deleted', life: 3000 });
      this.selectedUsers = [];
  }

  confirmDelete() {
      this.deleteUserDialog = false;
      this.Users = this.Users.filter(val => val.id !== this.User.id);
      this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'User Deleted', life: 3000 });
      this.User = {};
  }

  hideDialog() {
      this.UserDialog = false;
      this.submitted = false;
  }

  // saveUser() {
  //     this.submitted = true;

  //     if (this.User.username?.trim()) {
  //         if (this.User.id) {
  //             // @ts-ignore
  //             this.User.inventoryStatus = this.User.inventoryStatus.value ? this.User.inventoryStatus.value : this.User.inventoryStatus;
  //             this.Users[this.findIndexById(this.User.id)] = this.User;
  //             this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'User Updated', life: 3000 });
  //         } else {
  //             this.User.id = this.createId();
  //             this.User.email = this.createId();
  //             this.User.image = 'User-placeholder.svg';
  //             // @ts-ignore
  //             this.User.inventoryStatus = this.User.inventoryStatus ? this.User.inventoryStatus.value : 'INSTOCK';
  //             this.Users.push(this.User);
  //             this.messageService.add({ severity: 'success', summary: 'Successful', detail: 'User Created', life: 3000 });
  //         }

  //         this.Users = [...this.Users];
  //         this.UserDialog = false;
  //         this.User = {};
  //     }
  // }

  findIndexById(id: string): number {
      let index = -1;
      for (let i = 0; i < this.Users.length; i++) {
          if (this.Users[i].id === id) {
              index = i;
              break;
          }
      }

      return index;
  }

  createId(): string {
      let id = '';
      const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
      for (let i = 0; i < 5; i++) {
          id += chars.charAt(Math.floor(Math.random() * chars.length));
      }
      return id;
  }

  onGlobalFilter(table: Table, event: Event) {
      table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
  }
}
