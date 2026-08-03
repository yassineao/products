import { isPlatformBrowser } from '@angular/common';
import {
  Component,
  HostListener,
  Input,
  OnDestroy,
  OnInit,
  PLATFORM_ID,
  computed,
  inject,
  signal,
} from '@angular/core';

export type FocusRailItem = {
  id: string | number;
  title: string;
  description?: string;
  imageSrc: string;
  href?: string;
  meta?: string;
};

const DEMO_ITEMS: FocusRailItem[] = [
  {
    id: 1,
    title: 'Neon Tokyo',
    description: 'Experience the vibrant nightlife and illuminated streets of Shinjuku.',
    meta: 'Urban • Travel',
    imageSrc:
      'https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?q=80&w=1200&auto=format&fit=crop',
    href: '#tokyo',
  },
  {
    id: 2,
    title: 'Nordic Silence',
    description: 'Minimalist architecture meeting the raw beauty of the Icelandic coast.',
    meta: 'Design • Nature',
    imageSrc:
      'https://images.unsplash.com/photo-1470770841072-f978cf4d019e?q=80&w=1200&auto=format&fit=crop',
    href: '#nordic',
  },
  {
    id: 3,
    title: 'Sahara Echoes',
    description: 'Wandering through the timeless dunes under an endless golden sun.',
    meta: 'Adventure • Heat',
    imageSrc:
      'https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?q=80&w=1200&auto=format&fit=crop',
    href: '#sahara',
  },
  {
    id: 4,
    title: 'Cyber Future',
    description: 'A glimpse into a technological singularity where AI meets humanity.',
    meta: 'Tech • AI',
    imageSrc:
      'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?q=80&w=1200&auto=format&fit=crop',
    href: '#cyber',
  },
  {
    id: 5,
    title: 'Deep Ocean',
    description: 'The crushing pressure and alien beauty of the Mariana Trench.',
    meta: 'Science • Deep',
    imageSrc:
      'https://images.unsplash.com/photo-1682687220742-aba13b6e50ba?q=80&w=1200&auto=format&fit=crop',
    href: '#ocean',
  },
];

@Component({
  selector: 'hero-carousel',
  templateUrl: './hero_carousel.html',
  styleUrl: './hero_carousel.css',
  host: {
    '(mouseenter)': 'setHovering(true)',
    '(mouseleave)': 'setHovering(false)',
  },
})
export class HeroCarouselComponent implements OnInit, OnDestroy {
  @Input() items: FocusRailItem[] = DEMO_ITEMS;
  @Input() initialIndex = 0;
  @Input() loop = true;
  @Input() autoPlay = false;
  @Input() interval = 4000;

  protected readonly active = signal(0);
  protected readonly offsets = [-2, -1, 0, 1, 2];
  protected readonly activeItem = computed(() => this.items[this.active()]);

  private readonly platformId = inject(PLATFORM_ID);
  private timer?: ReturnType<typeof setInterval>;
  private lastWheelTime = 0;
  private pointerStartX?: number;
  private hovering = false;

  ngOnInit(): void {
    this.active.set(this.normalize(this.initialIndex));
    this.startAutoPlay();
  }

  ngOnDestroy(): void {
    this.stopAutoPlay();
  }

  protected previous(): void {
    if (!this.items.length || (!this.loop && this.active() === 0)) return;
    this.active.update((index) => this.normalize(index - 1));
  }

  protected next(): void {
    if (!this.items.length || (!this.loop && this.active() === this.items.length - 1)) return;
    this.active.update((index) => this.normalize(index + 1));
  }

  protected itemAt(offset: number): FocusRailItem {
    return this.items[this.normalize(this.active() + offset)];
  }

  protected isVisible(offset: number): boolean {
    const index = this.active() + offset;
    return this.loop || (index >= 0 && index < this.items.length);
  }

  protected selectOffset(offset: number): void {
    if (offset) this.active.update((index) => this.normalize(index + offset));
  }

  protected cardTransform(offset: number): string {
    const distance = Math.abs(offset);
    const x = offset * 320;
    const z = -distance * 180;
    const scale = offset === 0 ? 1 : 0.85;
    return `translate3d(${x}px, 0, ${z}px) rotateY(${offset * -20}deg) scale(${scale})`;
  }

  protected cardFilter(offset: number): string {
    const distance = Math.abs(offset);
    return `blur(${offset === 0 ? 0 : distance * 6}px) brightness(${offset === 0 ? 1 : 0.5})`;
  }

  protected cardOpacity(offset: number): number {
    return offset === 0 ? 1 : Math.max(0.1, 1 - Math.abs(offset) * 0.5);
  }

  protected cardZIndex(offset: number): number {
    return 20 - Math.abs(offset);
  }

  protected onWheel(event: WheelEvent): void {
    const now = Date.now();
    if (now - this.lastWheelTime < 400) return;

    const delta = Math.abs(event.deltaX) > Math.abs(event.deltaY) ? event.deltaX : event.deltaY;
    if (Math.abs(delta) <= 20) return;

    delta > 0 ? this.next() : this.previous();
    this.lastWheelTime = now;
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

  protected setHovering(value: boolean): void {
    this.hovering = value;
    value ? this.stopAutoPlay() : this.startAutoPlay();
  }

  @HostListener('keydown', ['$event'])
  protected onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'ArrowLeft') this.previous();
    if (event.key === 'ArrowRight') this.next();
  }

  private normalize(index: number): number {
    const count = this.items.length;
    if (!count) return 0;
    if (!this.loop) return Math.max(0, Math.min(count - 1, index));
    return ((index % count) + count) % count;
  }

  private startAutoPlay(): void {
    if (!this.autoPlay || this.hovering || !isPlatformBrowser(this.platformId) || this.timer) return;
    this.timer = setInterval(() => this.next(), this.interval);
  }

  private stopAutoPlay(): void {
    if (this.timer) clearInterval(this.timer);
    this.timer = undefined;
  }
}
