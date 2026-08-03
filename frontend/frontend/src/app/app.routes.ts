import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    title: 'Maison Atlas | Moroccan Clothing',
    loadComponent: () => import('./pages/home/Home').then((module) => module.HomePage),
  },
  {
    path: 'shop',
    title: 'Shop | Maison Atlas',
    loadComponent: () => import('./pages/shop/Shop').then((module) => module.ShopPage),
  },
  {
    path: 'about',
    title: 'Our Story | Maison Atlas',
    loadComponent: () => import('./pages/about/AboutPage').then((module) => module.AboutPage),
  },
  {
    path: 'contact',
    title: 'Contact | Maison Atlas',
    loadComponent: () => import('./pages/contact/Contact').then((module) => module.ContactPage),
  },
  { path: '**', redirectTo: '' },
];
