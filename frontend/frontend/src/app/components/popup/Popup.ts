import { Component, Input, OnChanges, signal, SimpleChanges } from '@angular/core';
import { Product } from '../../core/interfaces/Product';
import { CartButtonComponent } from '../cartButton/CartButton';

@Component({
  selector: 'popup-shop-page',
  templateUrl: './Popup.html',
  imports: [CartButtonComponent],
})
export class PopupShopPage implements OnChanges {
  @Input() product!: Product ;

  protected readonly currentColor = signal('');
  protected readonly currentSize = signal('');

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['product']) {
      this.currentColor.set(this.product.color?.[0] ?? '');
      this.currentSize.set(this.product.size?.[0] ?? '');
    }
  }

  protected select_color(color: string): void {
    this.currentColor.set(color);
  }

  protected select_size(size: string): void {
    this.currentSize.set(size);
  }
}
