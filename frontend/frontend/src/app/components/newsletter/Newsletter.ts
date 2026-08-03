import { Component, signal } from '@angular/core';

@Component({
  selector: 'newsletter-signup',
  templateUrl: './Newsletter.html',
})
export class NewsletterComponent {
  protected readonly email = signal('');
  protected readonly submitted = signal(false);

  protected subscribe(event: Event): void {
    event.preventDefault();
    if (!this.email().trim()) return;
    this.submitted.set(true);
  }
}
