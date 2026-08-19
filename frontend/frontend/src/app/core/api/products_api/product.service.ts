import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product } from '../../interfaces/Product';
import {
  catchError,
  forkJoin,
  map,
  Observable,
  of,
  shareReplay,
  switchMap,
  tap,
  throwError,
} from 'rxjs';
import { environment } from '../../../../../environment';
import { ProductImageService } from '../supabase_api/product_image.service';
import { ProductColorSize } from '../../interfaces/ProductColorSize';

@Injectable({
  providedIn: 'root',
})
export class ProductsService {
  private httpClient = inject(HttpClient);
  private apiUrl: string = environment.apiUrl;
  private productImageService = inject(ProductImageService);
  private productsRequest?: Observable<Product[]>;
  readonly productsLoaded = signal(false);

  getProducts(): Observable<Product[]> {
    if (this.productsRequest) {
      return this.productsRequest;
    }

    this.productsRequest = this.httpClient.get<Product[]>(`${this.apiUrl}/product`).pipe(
      map((products) => products.map((product) => this.parseProductOptions(product))),
      switchMap((products) => {
        if (products.length === 0) {
          return of<Product[]>([]);
        }

        return forkJoin(
          products.map((product) =>
            this.productImageService.getProductImages(product.id).pipe(
              map((images) => ({
                ...product,
                productImage: images,
              })),
            ),
          ),
        );
      }),

      tap(() => this.productsLoaded.set(true)),
      catchError((error) => {
        this.productsRequest = undefined;
        this.productsLoaded.set(false);
        return throwError(() => error);
      }),
      shareReplay({ bufferSize: 1, refCount: false }),
    );

    return this.productsRequest;
  }


  getActiveProducts(): Observable<Product[]> {
    return this.httpClient.get<Product[]>(`${this.apiUrl}/products/active`).pipe(
      map((products) => products.map((product) => this.parseProductOptions(product))),
      switchMap((products) =>
        products.length === 0
          ? of([])
          : forkJoin(
              products.map((product) =>
                this.productImageService.getProductImages(product.id).pipe(
                  map((images) => ({
                    ...product,
                    productImage: images,
                  })),
                ),
              ),
            ),
      ),
    );
  }



  private parseProductOptions(product: Product): Product {
    if (!product.name.includes('[')) {
      return {
        ...product,
      };
    }
    const name = product.name.slice(0, product.name.indexOf('[')).trim();
    const size = product.name
      .slice(product.name.indexOf('[') + 1, product.name.indexOf(']'))
      .trim();
    const color = product.name.slice(product.name.indexOf(']') + 1).trim();

    return {
      ...product,
      name: name || product.name,
      size,
      color,
    };
  }

  private parseOptionList(value: string): string[] {
    return value
      .split(/[,|]/)
      .map((option) => option.trim())
      .filter(Boolean);
  }

  addProduct(Product: Product): void {
    this.httpClient.post(`${this.apiUrl}/product`, Product, {
      withCredentials: true,
    });
  }

  updateProduct(Product: Product): void {
    this.httpClient.put(`${this.apiUrl}/product`, Product, {
      withCredentials: true,
    });
  }

  deleteProduct(ProductId: String): void {
    this.httpClient.delete(`${this.apiUrl}/product/${ProductId}`, {
      withCredentials: true,
    });
  }
}
