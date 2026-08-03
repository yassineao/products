import { Component } from '@angular/core';
import { AboutComponent } from '../../components/about/About';
import { CraftsmanshipComponent } from '../../components/craftsmanship/Craftsmanship';
import { TestimonialsComponent } from '../../components/testimonials/Testimonials';

@Component({
  selector: 'app-about-page',
  imports: [AboutComponent, CraftsmanshipComponent, TestimonialsComponent],
  templateUrl: './AboutPage.html',
})
export class AboutPage {}
