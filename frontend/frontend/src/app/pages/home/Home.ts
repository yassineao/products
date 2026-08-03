import { Component } from '@angular/core';
import { HeroCarouselComponent } from '../../shared/hero/component';
import { NewArrivalsSection } from '../../components/newArrivalsSection/newArrivalsSection';
import { FaqComponent } from '../../shared/faq/Faq';
import { AboutComponent } from '../../components/about/About';
import { CollectionGridComponent } from '../../components/collectionGrid/CollectionGrid';
import { CraftsmanshipComponent } from '../../components/craftsmanship/Craftsmanship';
import { TestimonialsComponent } from '../../components/testimonials/Testimonials';
import { NewsletterComponent } from '../../components/newsletter/Newsletter';

@Component({
  selector: 'app-home-page',
  imports: [
    HeroCarouselComponent,
    NewArrivalsSection,
    FaqComponent,
    AboutComponent,
    CollectionGridComponent,
    CraftsmanshipComponent,
    TestimonialsComponent,
    NewsletterComponent,
  ],
  templateUrl: './Home.html',
})
export class HomePage {}
