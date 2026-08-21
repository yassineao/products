import { Routes } from '@angular/router';
import { adminGuard } from './core/guards/admin.guard';

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
  {
    path: 'admin',
    title: 'Admin sign in | Maison Atlas',
    loadComponent: () => import('./pages/admin/Admin').then((module) => module.AdminPage),
  },
  {
    path: 'admin/products',
    title: 'Manage products | Maison Atlas',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./pages/admin-products/AdminProducts').then((module) => module.AdminProductsPage),
  },
  { path: '**', redirectTo: '' },
];
