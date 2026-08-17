import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environment';
import { Observable } from 'rxjs';
import { Category } from '../../interfaces/Category';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}`;

  getCategories = (): Observable<Category[]> => {
    return this.http.get<Category[]>(`${this.apiUrl}/category`);
  };

  addCategory = (category: Category) => {
    this.http.post(
      `${this.apiUrl}/category`,
      category,
      {
        withCredentials: true
      }

    );
  }

  deleteCategory = (id: number) => {
    this.http.delete(`${this.apiUrl}/category/${id}`,
      {
        withCredentials: true
      }
      );
  }

}
