import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-contact-page',
  templateUrl: './Contact.html',
})
export class ContactPage {
  protected readonly sent = signal(false);

  protected send(event: Event): void {
    event.preventDefault();
    this.sent.set(true);
  }
}
