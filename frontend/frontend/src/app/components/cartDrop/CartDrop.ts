import { Component, HostListener, inject, signal } from '@angular/core';
import { Cart_session } from '../../core/api/cart_api/Cart_session';
import { Product } from '../../core/interfaces/Product';

@Component({
  selector: 'app-cart-drop',
  templateUrl: './CartDrop.html',
})
export class CartDropComponent {
  private readonly cartSession = inject(Cart_session);

  protected readonly open = signal(false);
  protected readonly products = this.cartSession.products;
  protected readonly itemCount = this.cartSession.itemCount;
  protected readonly total = this.cartSession.total;

  protected toggle(): void {
    this.open.update((open) => !open);
  }

  protected remove(product: Product): void {
    this.cartSession.delete_from_cart(product);
  }

  @HostListener('document:keydown.escape')
  @HostListener('document:click')
  protected close(): void {
    this.open.set(false);
  }
}
