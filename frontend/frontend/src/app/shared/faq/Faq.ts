import { Component, signal } from '@angular/core';

@Component({
  selector: 'faq',
  templateUrl: './Faq.html',
})
export class FaqComponent {
  protected readonly openIndex = signal<number | null>(0);
  protected readonly faqs = [
    {
      question: 'Where are your pieces made?',
      answer: 'Our collections are developed with independent makers and specialist workshops, with an emphasis on skilled handwork and responsible production.',
    },
    {
      question: 'Do you ship internationally?',
      answer: 'Yes. We ship worldwide with tracked delivery. Timing and duties vary by destination and are shown clearly at checkout.',
    },
    {
      question: 'How should I care for handcrafted pieces?',
      answer: 'Each piece includes individual care guidance. In general, gentle cleaning, cool water and natural drying best preserve colour and texture.',
    },
    {
      question: 'Can I return an order?',
      answer: 'Unworn items can be returned within 30 days of delivery. Limited artisan pieces must be returned with their original packaging and tags.',
    },
  ];

  protected toggle(index: number): void {
    this.openIndex.update((open) => (open === index ? null : index));
  }
}
