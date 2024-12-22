import { NgModule } from '@angular/core';
import { CommonModule, LocationStrategy, PathLocationStrategy } from '@angular/common';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { AppLayoutModule } from './layout/app.layout.module';
import { ProductService } from './esprit/service/product.service';
import { CountryService } from './esprit/service/country.service';
import { CustomerService } from './esprit/service/customer.service';
import { EventService } from './esprit/service/event.service';
import { IconService } from './esprit/service/icon.service';
import { NodeService } from './esprit/service/node.service';
import { PhotoService } from './esprit/service/photo.service';
import { ReservationComponent } from './esprit/components/reservation/reservation.component';
import { UserChartsComponent } from './esprit/components/user-charts/user-charts.component';
import { TableModule } from 'primeng/table';
import { FormsModule } from '@angular/forms';
import { FileUploadModule } from 'primeng/fileupload';
import { ButtonModule } from 'primeng/button';
import { RippleModule } from 'primeng/ripple';
import { ToastModule } from 'primeng/toast';
import { ToolbarModule } from 'primeng/toolbar';
import { RatingModule } from 'primeng/rating';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { DropdownModule } from 'primeng/dropdown';
import { RadioButtonModule } from 'primeng/radiobutton';
import { InputNumberModule } from 'primeng/inputnumber';
import { DialogModule } from 'primeng/dialog';
import { CalendarModule } from 'primeng/calendar';
import { MultiSelectModule } from 'primeng/multiselect';
import { FullCalendarModule } from '@fullcalendar/angular';
import { StorageService } from './esprit/service/storage.service';
import { UserService } from './esprit/service/user.service';
import { AuthInterceptor } from './esprit/interceptors/auth.interceptor';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ChartModule } from 'primeng/chart';

@NgModule({
    declarations: [AppComponent, ReservationComponent],
    imports: [
        AppRoutingModule,
        AppLayoutModule,
        CommonModule,
        TableModule,
        FileUploadModule,
        FormsModule,
        ButtonModule,
        RippleModule,
        ToastModule,
        ToolbarModule,
        RatingModule,
        InputTextModule,
        InputTextareaModule,
        DropdownModule,
        RadioButtonModule,
        InputNumberModule,
        CalendarModule,
        DialogModule,
        MultiSelectModule,
        FullCalendarModule,
        HttpClientModule,
        ChartModule,
        UserChartsComponent
    ],
    providers: [
        { provide: LocationStrategy, useClass: PathLocationStrategy },
        CountryService, CustomerService, EventService, IconService, NodeService,
        PhotoService, ProductService,
        StorageService,
		UserService,
		{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
    ],
    bootstrap: [AppComponent],
})
export class AppModule {}