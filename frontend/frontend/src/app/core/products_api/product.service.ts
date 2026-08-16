import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product } from '../interfaces/Product';
import { Observable } from 'rxjs';
import { environment } from '../../../../environment';

@Injectable({
  providedIn: 'root',
})
export class ProductsService {
  private httpClient = inject(HttpClient);
  private apiUrl: string = environment.apiUrl;

  getProducts(): Observable<Product[]> {
    return this.httpClient.get<Product[]>(`${this.apiUrl}/product`);
  }

  getActiveProducts(): Observable<Product[]>{
    return this.httpClient.get<Product[]>(`${this.apiUrl}/products/active`);
  }

  addProduct(Product: Product): void {
    this.httpClient.post(`${this.apiUrl}/product`, Product, {
      withCredentials: true,
    });
  }

  updateProduct(Product: Product): void {
    this.httpClient.put(`${this.apiUrl}/product`, Product, {
      withCredentials: true,
    })
  }

  deleteProduct(ProductId: String): void {
    this.httpClient.delete(`${this.apiUrl}/product/${ProductId}`,{
      withCredentials: true,
    });
  }

}
