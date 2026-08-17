import { isPlatformBrowser } from '@angular/common';
import { Component, NgZone, OnDestroy, OnInit, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

type HeroSlide = {
  imageSrc: string;
  imageAlt: string;
  eyebrow: string;
  title: string;
  description: string;
  ctaText: string;
  ctaUrl: string;
  accent: string;
};

@Component({
  selector: 'hero-caroussel',
  imports: [RouterLink],
  templateUrl: './Hero.html',
})
export class HeroCarousselComponent implements OnInit, OnDestroy {
  protected readonly slides: HeroSlide[] = [
    {
      imageSrc: 'https://images.unsplash.com/photo-1539109136881-3be0616acf4b?q=85&w=1800&auto=format&fit=crop',
      imageAlt: 'Woman wearing an elegant flowing garment',
      eyebrow: 'The new collection',
      title: 'Moroccan soul, modern silhouette.',
      description: 'Expressive pieces shaped by Marrakech colour, natural texture and the ease of contemporary dressing.',
      ctaText: 'Explore the collection',
      ctaUrl: '/shop',
      accent: '01',
    },
    {
      imageSrc: 'https://images.unsplash.com/photo-1608234807905-4466023792f5?q=85&w=1800&auto=format&fit=crop',
      imageAlt: 'Woman wearing a richly coloured draped dress',
      eyebrow: 'Limited artisan edit',
      title: 'Colour that carries a story.',
      description: 'Terracotta, burgundy and gold meet fluid forms designed to move beautifully from day into evening.',
      ctaText: 'Shop limited pieces',
      ctaUrl: '/shop',
      accent: '02',
    },
    {
      imageSrc: 'https://images.unsplash.com/photo-1667243038099-b257ab263bfd?q=85&w=1800&auto=format&fit=crop',
      imageAlt: 'Contemporary fashion styled in warm earthy tones',
      eyebrow: 'Crafted in small batches',
      title: 'Made slowly. Worn often.',
      description: 'Thoughtful wardrobe foundations created with independent makers and finished with distinctive detail.',
      ctaText: 'Discover our story',
      ctaUrl: '/about',
      accent: '03',
    },
  ];

  protected readonly currentIndex = signal(0);
  protected readonly currentSlide = computed(() => this.slides[this.currentIndex()]);

  private readonly platformId = inject(PLATFORM_ID);
  private readonly ngZone = inject(NgZone);
  private autoplayTimer?: ReturnType<typeof setInterval>;
  private pointerStartX?: number;

  ngOnInit(): void {
    this.startAutoplay();
  }

  ngOnDestroy(): void {
    this.stopAutoplay();
  }

  protected previous(): void {
    this.currentIndex.update((index) => (index - 1 + this.slides.length) % this.slides.length);
    this.restartAutoplay();
  }

  protected next(): void {
    this.currentIndex.update((index) => (index + 1) % this.slides.length);
    this.restartAutoplay();
  }

  protected select(index: number): void {
    this.currentIndex.set(index);
    this.restartAutoplay();
  }

  protected pauseAutoplay(): void {
    this.stopAutoplay();
  }

  protected resumeAutoplay(): void {
    this.startAutoplay();
  }

  protected onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'ArrowLeft') this.previous();
    if (event.key === 'ArrowRight') this.next();
  }

  protected onPointerDown(event: PointerEvent): void {
    this.pointerStartX = event.clientX;
  }

  protected onPointerUp(event: PointerEvent): void {
    if (this.pointerStartX === undefined) return;
    const distance = event.clientX - this.pointerStartX;
    this.pointerStartX = undefined;
    if (Math.abs(distance) < 50) return;
    distance < 0 ? this.next() : this.previous();
  }

  private restartAutoplay(): void {
    this.stopAutoplay();
    this.startAutoplay();
  }

  private startAutoplay(): void {
    if (!isPlatformBrowser(this.platformId) || this.autoplayTimer) return;
    this.ngZone.runOutsideAngular(() => {
      this.autoplayTimer = setInterval(() => {
        this.ngZone.run(() => this.next());
      }, 6000);
    });
  }

  private stopAutoplay(): void {
    if (this.autoplayTimer) clearInterval(this.autoplayTimer);
    this.autoplayTimer = undefined;
  }
}
