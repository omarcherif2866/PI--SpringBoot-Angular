import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UIkitRoutingModule } from './uikit-routing.module';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { StorageService } from '../../service/storage.service';
import { UserService } from '../../service/user.service';
import { AuthInterceptor } from '../../interceptors/auth.interceptor';

@NgModule({
	imports: [
		CommonModule,
		UIkitRoutingModule,
		HttpClientModule,
	],
	providers: [
		StorageService,
		UserService,
		{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
	  ],
})
export class UIkitModule { }
