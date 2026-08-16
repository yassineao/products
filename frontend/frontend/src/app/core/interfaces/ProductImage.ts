import { Product } from './Product';

export interface ProductImage {
  id: string;
  altText: string;
  width: number;
  height: number;
  productId: String;
  mainPage: boolean;
  file: File;
}
