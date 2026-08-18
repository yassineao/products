import { Product } from './Product';

export interface Cart {
  id: number;
  price: number;
  products: Product[];
  stats: string;
}
