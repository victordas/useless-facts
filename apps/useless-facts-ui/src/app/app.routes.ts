import { Routes } from '@angular/router';
import { 
  AllUselessFactsComponent, 
  RandomUselessFactComponent, 
  UselessFactDetailComponent, 
  UselessStatisticsComponent 
} from './components';

export const routes: Routes = [
  {
    path: 'random',
    component: RandomUselessFactComponent
  },
  {
    path: 'detail',
    component: UselessFactDetailComponent
  },
  {
    path: 'list',
    component: AllUselessFactsComponent
  },
  {
    path: 'statistics',
    component: UselessStatisticsComponent
  },
  {
    path: '',
    redirectTo: '/random',
    pathMatch: 'full'
  }
];