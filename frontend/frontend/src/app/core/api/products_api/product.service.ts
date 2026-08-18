import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product } from '../../interfaces/Product';
import { catchError, forkJoin, map, Observable, of, shareReplay, switchMap, tap, throwError } from 'rxjs';
import { environment } from '../../../../../environment';
import { ProductImageService } from '../supabase_api/product_image.service';

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



  getActiveProducts(): Observable<Product[]>{
    return this.httpClient.get<Product[]>(`${this.apiUrl}/products/active`).pipe(
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
    );;
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
