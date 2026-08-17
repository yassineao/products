import { Component, computed, DestroyRef, inject, Input, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ProductsService } from '../../core/api/products_api/product.service';
import { CategoryService } from '../../core/api/category_api/category.service';
import { Category } from '../../core/interfaces/Category';
import { map } from 'rxjs';
import { PopupShopPage } from '../popup/Popup';
import { Product } from '../../core/interfaces/Product';

const ALL_CATEGORY: Category = {
  id: '0',
  name: 'All',
  description: 'All',
  active: true,
};

@Component({
  selector: 'Catalogue',
  templateUrl: './Catalogue.html',
  imports: [PopupShopPage],
})
export class CataloguePage implements OnInit {
  @Input()
  CompoOrPage: boolean = false;

  @Input()
  productItems: Product[] | null = null;

  private readonly categoriesService = inject(CategoryService);
  protected readonly categories = toSignal(
    this.categoriesService.getCategories().pipe(map((categories) => [ALL_CATEGORY, ...categories])),
    { initialValue: [ALL_CATEGORY] },
  );
  protected readonly selectedCategory = signal('All');
  private readonly destroyRef = inject(DestroyRef);
  private readonly productsService = inject(ProductsService);
  protected readonly products = signal<Product[]>([]);

  ngOnInit(): void {
    if (this.productItems !== null) {
      this.products.set(this.productItems);
      return;
    }

    this.productsService.getProducts().pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (products) => this.products.set(products),
      error: () => this.products.set([]),
    });
  }

  protected readonly visibleProducts = computed(() => {
    const category = this.selectedCategory();
    const products = this.products();
    const newProducts =
      category === 'All'
        ? products
        : products.filter((product) => product.category.name === category);

    return !this.CompoOrPage?
      newProducts
      : newProducts.slice(0,4)
      ;
  });
  protected showPopup = signal(false);

  openPopup() {
    console.log(this.showPopup);
    this.showPopup.set(true);
  }

  closePopup() {
    console.log(this.showPopup);
    this.showPopup.set(false);
  }
}
