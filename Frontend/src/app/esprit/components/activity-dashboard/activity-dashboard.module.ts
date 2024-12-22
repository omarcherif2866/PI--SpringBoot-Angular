import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ActivityDashboardRoutingModule } from './activity-dashboard-routing.module';
import { ActivityDashboardComponent } from './activity-dashboard.component';
import { FormsModule } from '@angular/forms';
import { ChartModule } from 'primeng/chart';
import { MenuModule } from 'primeng/menu';
import { TableModule } from 'primeng/table';
import { StyleClassModule } from 'primeng/styleclass';
import { PanelMenuModule } from 'primeng/panelmenu';
import { ButtonModule } from 'primeng/button';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ChartModule,
    MenuModule,
    TableModule,
    StyleClassModule,
    PanelMenuModule,
    ButtonModule,
    ActivityDashboardRoutingModule
],
declarations: [ActivityDashboardComponent]
})
export class ActivityDashboardModule { }
