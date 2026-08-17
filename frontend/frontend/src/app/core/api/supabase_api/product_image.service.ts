import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environment';
import { ProductImage } from '../../interfaces/ProductImage';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProductImageService {
  private http = inject(HttpClient);
  private apiUrl: string = environment.apiUrl;

  uploadPicture = (productImage: ProductImage) => {
    this.http.post(`${this.apiUrl}/product-image/upload`, productImage, {
      withCredentials: true,
    });
  };
  deletePicture = (productId: Number) => {
    this.http.delete(`${this.apiUrl}/product-image/product/${productId}`);
  };
  getProductImages(productId: string): Observable<ProductImage[]> {
    return this.http.get<ProductImage[]>(`${this.apiUrl}/product-image/product/${productId}`);
  }
}
