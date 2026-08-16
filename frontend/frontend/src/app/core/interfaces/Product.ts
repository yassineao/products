import { Category } from './Category';

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  tags: string[];
  active: boolean;
  category: Category;
  imageUrl: string;
}
