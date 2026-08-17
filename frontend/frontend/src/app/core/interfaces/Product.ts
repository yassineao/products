import { Category } from './Category';
import { ProductImage } from './ProductImage';

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  tags: string[];
  active: boolean;
  category: Category;
  productImage: ProductImage[];
}
