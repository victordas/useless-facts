import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AllUselessFactsComponent, RandomUselessFactComponent, UselessFactDetailComponent, UselessStatisticsComponent } from './components';

@Component({
  imports: [
    RouterModule, 
    RandomUselessFactComponent,
    UselessFactDetailComponent,
    AllUselessFactsComponent,
    UselessStatisticsComponent
  ],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent { }
