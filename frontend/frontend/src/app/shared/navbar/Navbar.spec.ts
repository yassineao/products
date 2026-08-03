import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Navbar } from './Navbar';
import { provideRouter } from '@angular/router';

describe('Navbar', () => {
  let fixture: ComponentFixture<Navbar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [Navbar], providers: [provideRouter([])] }).compileComponents();
    fixture = TestBed.createComponent(Navbar);
    fixture.detectChanges();
  });

  it('opens and closes the mobile menu', () => {
    const toggle = fixture.nativeElement.querySelector('[aria-controls="navbar-menu"]') as HTMLButtonElement;
    const menu = fixture.nativeElement.querySelector('#navbar-menu') as HTMLElement;

    expect(toggle.getAttribute('aria-expanded')).toBe('false');
    expect(menu.classList.contains('visible')).toBe(false);

    toggle.click();
    fixture.detectChanges();

    expect(toggle.getAttribute('aria-expanded')).toBe('true');
    expect(menu.classList.contains('visible')).toBe(true);

    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape' }));
    fixture.detectChanges();

    expect(toggle.getAttribute('aria-expanded')).toBe('false');
    expect(menu.classList.contains('visible')).toBe(false);
  });

  it('closes the menu after selecting a navigation link', () => {
    const toggle = fixture.nativeElement.querySelector('[aria-controls="navbar-menu"]') as HTMLButtonElement;
    const link = fixture.nativeElement.querySelector('#navbar-menu a') as HTMLAnchorElement;

    toggle.click();
    link.click();
    fixture.detectChanges();

    expect(toggle.getAttribute('aria-expanded')).toBe('false');
  });
});
