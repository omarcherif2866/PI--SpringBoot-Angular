import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ActiviteSubsComponent } from './activite-subs.component';

const routes: Routes = [];

@NgModule({
  imports: [RouterModule.forChild([
    { path: '', component: ActiviteSubsComponent }
])],
  exports: [RouterModule]
})
export class ActiviteSubsRoutingModule { }
