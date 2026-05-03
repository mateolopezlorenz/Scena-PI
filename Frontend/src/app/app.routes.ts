import { Routes } from '@angular/router';
import { Register } from './components/register/register';
import { Login } from './components/login/login';
import { EventForm } from './components/event-form/event-form';
import { Home } from './components/home/home';
import { EventList } from './components/event-list/event-list';
import { LocalForm } from './components/local-form/local-form';
import { LocalList } from './components/local-list/local-list';
import { Event } from './components/eventID/event';
import { UserLikesComponent } from './components/user-likes/user-likes';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'register', component: Register },
  { path: 'login', component: Login },
  { path: 'home', component: Home },
  { path: 'event-list', component: EventList },
  { path: 'event/:id', component: Event },
  { path: 'event', component: EventForm, canActivate: [authGuard] },
  { path: 'local', component: LocalForm, canActivate: [authGuard] },
  { path: 'local-list', component: LocalList },
  { path: 'user-likes', component: UserLikesComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '/home' }
];
