import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ActiviteComponent } from './activite.component';

const routes: Routes = [];

@NgModule({
  imports: [RouterModule.forChild([
    { path: '', component: ActiviteComponent }
])],
  exports: [RouterModule]
})
export class ActiviteRoutingModule { }
