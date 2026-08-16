import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ProductsService } from '../../core/products_api/product.service';
import { CategoryService } from '../../core/category_api/category.service';
import { Category } from '../../core/interfaces/Category';
import { map } from 'rxjs';

const ALL_CATEGORY: Category = {
  id: '0',
  name: 'All',
  description: 'All',
  active: true,
};

@Component({
  selector: 'app-shop-page',
  templateUrl: './Shop.html',
})
export class ShopPage {
  private readonly categoriesService = inject(CategoryService);
  protected readonly categories = toSignal(
    this.categoriesService
      .getCategories()
      .pipe(map((categories) => [ALL_CATEGORY, ...categories])),
    { initialValue: [ALL_CATEGORY] },
  );
  protected readonly selectedCategory = signal('All');
  private readonly productsService = inject(ProductsService);
  protected readonly products = toSignal(this.productsService.getProducts(), {
    initialValue: [],
  });

  protected readonly visibleProducts = computed(() => {
    const category = this.selectedCategory();
    const products = this.products();

    return category === 'All'
      ? products
      : products.filter((product) => product.category.name === category);
  });
}
