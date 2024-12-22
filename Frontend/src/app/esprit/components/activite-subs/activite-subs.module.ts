import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ActiviteSubsRoutingModule } from './activite-subs-routing.module';
import { FormsModule } from '@angular/forms';
import { ListDemoRoutingModule } from '../uikit/list/listdemo-routing.module';
import { DataViewModule } from 'primeng/dataview';
import { PickListModule } from 'primeng/picklist';
import { OrderListModule } from 'primeng/orderlist';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { RatingModule } from 'primeng/rating';
import { ButtonModule } from 'primeng/button';
import { ActiviteSubsComponent } from './activite-subs.component';


@NgModule({
  declarations: [ActiviteSubsComponent],
  imports: [
		CommonModule,
		FormsModule,
		ActiviteSubsRoutingModule,
		DataViewModule,
		PickListModule,
		OrderListModule,
		InputTextModule,
		DropdownModule,
		RatingModule,
		ButtonModule,
    ActiviteSubsRoutingModule
  ]
})
export class ActiviteSubsModule { }
