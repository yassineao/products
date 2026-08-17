export interface ProductImage {
  id: string;
  bucketName: string;
  objectPath: string;
  altText: string;
  width: number;
  height: number;
  signedUrl: string;
  mainImage: boolean;
  productIds: string[];
}
