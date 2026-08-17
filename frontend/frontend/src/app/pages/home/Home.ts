import { isPlatformBrowser } from '@angular/common';
import { ApplicationRef, Component, inject, PLATFORM_ID } from '@angular/core';
import { HeroCarousselComponent } from '../../components/hero_caroussel/Hero';
import { NewArrivalsSection } from '../../components/newArrivalsSection/newArrivalsSection';
import { FaqComponent } from '../../shared/faq/Faq';
import { AboutComponent } from '../../components/about/About';
import { CollectionGridComponent } from '../../components/collectionGrid/CollectionGrid';
import { CraftsmanshipComponent } from '../../components/craftsmanship/Craftsmanship';
import { TestimonialsComponent } from '../../components/testimonials/Testimonials';
import { NewsletterComponent } from '../../components/newsletter/Newsletter';
import { UserService } from '../../core/api/user_api/user.service';
import { ProductsService } from '../../core/api/products_api/product.service';
import { filter, switchMap, take } from 'rxjs';
import { CataloguePage } from '../../components/catalogue/Catalogue';

@Component({
  selector: 'app-home-page',
  imports: [
    HeroCarousselComponent,
    NewArrivalsSection,
    FaqComponent,
    AboutComponent,
    CollectionGridComponent,
    CraftsmanshipComponent,
    TestimonialsComponent,
    NewsletterComponent,
    CataloguePage,
  ],
  templateUrl: './Home.html',
})
export class HomePage {
  private readonly applicationRef = inject(ApplicationRef);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly userService = inject(UserService);
  private readonly productService = inject(ProductsService);

  ngOnInit(): void {
    this.userService.health();

    if (isPlatformBrowser(this.platformId)) {
      this.applicationRef.isStable
        .pipe(
          filter(Boolean),
          take(1),
          switchMap(() => this.productService.getProducts()),
        )
        .subscribe((products) => {
          localStorage.setItem('products', JSON.stringify(products));
        });
    }
  }
}
