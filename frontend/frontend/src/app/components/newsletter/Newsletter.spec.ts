import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NewsletterComponent } from './Newsletter';

describe('NewsletterComponent', () => {
  let fixture: ComponentFixture<NewsletterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [NewsletterComponent] }).compileComponents();
    fixture = TestBed.createComponent(NewsletterComponent);
    fixture.detectChanges();
  });

  it('shows a confirmation after a valid subscription', () => {
    const input = fixture.nativeElement.querySelector('input') as HTMLInputElement;
    const form = fixture.nativeElement.querySelector('form') as HTMLFormElement;

    input.value = 'hello@example.com';
    input.dispatchEvent(new Event('input'));
    form.dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Welcome to the Maison');
    expect(fixture.nativeElement.textContent).toContain('hello@example.com');
  });
});
