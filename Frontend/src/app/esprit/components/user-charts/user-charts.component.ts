import { Component, OnInit } from '@angular/core';
import { ChartModule } from 'primeng/chart';
import { UserService } from '../../service/user.service';

@Component({
  selector: 'app-user-charts',
  standalone: true,
  imports: [ChartModule],
  templateUrl: './user-charts.component.html',
  styleUrls: ['./user-charts.component.scss']
})
export class UserChartsComponent implements OnInit {
  totalUsers: number;
  activatedMembers: number;
  blockedMembers: number;
  roleMemberCount: number;
  roleResponsibleCount: number;

  // Define chart data and options
  activatedBlockedChartData: any;
  roleDistributionChartData: any;
  pieChartOptions: any = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      tooltip: {
        enabled: true,
      },
    },
  };

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.fetchUserData();
  }

  fetchUserData(): void {
    this.userService.getUsers().subscribe(
      (users: any[]) => {
        // Calculate totalUsers
        this.totalUsers = users.length;

        // Calculate activatedMembers and blockedMembers
        this.calculateActivatedMembers(users);
        this.calculateBlockedMembers(users);

        // Calculate role counts
        this.calculateRoleCounts(users);

        // Update chart data
        this.updateChartData();
      },
      (error) => {
        console.error('Error fetching users:', error);
        // Handle error as needed
      }
    );
  }

  calculateActivatedMembers(users: any[]): void {
    this.activatedMembers = users.filter(user => user.activated === true || user.activated === 1).length;
    console.log(this.activatedMembers);
  }

  calculateBlockedMembers(users: any[]): void {
    this.blockedMembers = users.filter(user => user.activated === false || user.activated === 0).length;
    console.log(this.blockedMembers);
  }

  calculateRoleCounts(users: any[]): void {
    this.roleMemberCount = users.filter(user => user.role === 'ROLE_MEMBRE').length;
    this.roleResponsibleCount = users.filter(user => user.role === 'ROLE_RESPONSABLE').length;
    console.log(this.roleMemberCount);
    console.log(this.roleResponsibleCount);
  }

  updateChartData(): void {
    this.activatedBlockedChartData = {
      labels: ['Activated Members', 'Blocked Members'],
      datasets: [
        {
          data: [this.activatedMembers, this.blockedMembers],
          backgroundColor: ['#42A5F5', '#FF6384'],
        },
      ],
    };

    this.roleDistributionChartData = {
      labels: ['Members', 'Responsibles'],
      datasets: [
        {
          data: [this.roleMemberCount, this.roleResponsibleCount],
          backgroundColor: ['#42A5F5', '#FFCE56'],
        },
      ],
    };
  }
}
