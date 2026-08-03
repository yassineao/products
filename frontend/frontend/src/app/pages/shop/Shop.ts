import { Component, computed, signal } from '@angular/core';

type Category = 'All' | 'Women' | 'Men' | 'Accessories';

type Product = {
  name: string;
  category: Exclude<Category, 'All'>;
  price: number;
  image: string;
  badge?: string;
};

@Component({
  selector: 'app-shop-page',
  templateUrl: './Shop.html',
})
export class ShopPage {
  protected readonly categories: Category[] = ['All', 'Women', 'Men', 'Accessories'];
  protected readonly selectedCategory = signal<Category>('All');
  protected readonly products: Product[] = [
    { name: 'Ivory Artisan Shirt', category: 'Women', price: 29, badge: 'New', image: 'https://images.unsplash.com/photo-1598554747436-c9293d6a588f?q=80&w=800&auto=format&fit=crop' },
    { name: 'Atlas Woven Layer', category: 'Men', price: 39, image: 'https://images.unsplash.com/photo-1508427953056-b00b8d78ebf5?q=80&w=800&auto=format&fit=crop' },
    { name: 'Terracotta Draped Dress', category: 'Women', price: 49, badge: 'Bestseller', image: 'https://images.unsplash.com/photo-1608234807905-4466023792f5?q=80&w=800&auto=format&fit=crop' },
    { name: 'Burgundy Silhouette', category: 'Women', price: 59, image: 'https://images.unsplash.com/photo-1667243038099-b257ab263bfd?q=80&w=800&auto=format&fit=crop' },
    { name: 'Medina Tailored Jacket', category: 'Men', price: 89, image: 'https://images.unsplash.com/photo-1617127365659-c47fa864d8bc?q=80&w=800&auto=format&fit=crop' },
    { name: 'Sahara Linen Shirt', category: 'Men', price: 54, image: 'https://images.unsplash.com/photo-1603252109303-2751441dd157?q=80&w=800&auto=format&fit=crop' },
    { name: 'Handwoven Market Bag', category: 'Accessories', price: 34, badge: 'Artisan made', image: 'https://images.unsplash.com/photo-1559563458-527698bf5295?q=80&w=800&auto=format&fit=crop' },
    { name: 'Golden Thread Scarf', category: 'Accessories', price: 27, image: 'https://images.unsplash.com/photo-1601924994987-69e26d50dc26?q=80&w=800&auto=format&fit=crop' },
  ];

  protected readonly visibleProducts = computed(() => {
    const category = this.selectedCategory();
    return category === 'All' ? this.products : this.products.filter((product) => product.category === category);
  });
}
