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

interface ProductRequest {
  name: string;
  description: string;
  price: number;
  quantity: number;
  tags: string[];
  active: boolean;
  categoryId: string;
}

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
    return this.httpClient.get<Product[]>(`${this.apiUrl}/product/active`).pipe(
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

  private toProductRequest(product: Product): ProductRequest {
    return {
      name: product.name,
      description: product.description,
      price: product.price,
      quantity: product.quantity,
      tags: product.tags,
      active: product.active,
      categoryId: product.category.id,
    };
  }

  private invalidateProductsCache(): void {
    this.productsRequest = undefined;
    this.productsLoaded.set(false);
  }

  addProduct(product: Product): Observable<void> {
    return this.httpClient
      .post<void>(`${this.apiUrl}/product`, [this.toProductRequest(product)], {
        withCredentials: true,
      })
      .pipe(tap(() => this.invalidateProductsCache()));
  }

  updateProduct(product: Product): Observable<void> {
    return this.httpClient
      .patch<void>(`${this.apiUrl}/product/${product.id}`, this.toProductRequest(product), {
        withCredentials: true,
      })
      .pipe(tap(() => this.invalidateProductsCache()));
  }

  deleteProduct(productId: string): Observable<void> {
    return this.httpClient
      .delete<void>(`${this.apiUrl}/product/${productId}`, {
        withCredentials: true,
      })
      .pipe(tap(() => this.invalidateProductsCache()));
  }
}
