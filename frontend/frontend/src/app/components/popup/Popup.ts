import { Component, Input } from '@angular/core';
import { Product } from '../../core/interfaces/Product';

@Component({
  selector: 'popup-shop-page',
  templateUrl: './Popup.html',
})
export class PopupShopPage {
  @Input() product!: Product;
}
