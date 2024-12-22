import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ActivityDashboardComponent } from './activity-dashboard.component';

const routes: Routes = [];

@NgModule({
  imports: [RouterModule.forChild([
    { path: '', component: ActivityDashboardComponent }
])],
exports: [RouterModule]
})
export class ActivityDashboardRoutingModule { }
