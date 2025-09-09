import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule, isPlatformBrowser, DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './hero.html',
  styleUrls: ['./hero.css']
})
export class HeroComponent {

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    @Inject(DOCUMENT) private document: Document
  ) {}

  scheduleAppointment() {
    // Por ahora, scroll al contacto - puedes implementar la lógica de agendamiento más tarde
    this.scrollToSection('contacto');
  }

  learnAboutServices() {
    this.scrollToSection('servicios');
  }

  private scrollToSection(sectionId: string) {
    if (!isPlatformBrowser(this.platformId)) return;
    
    const element = this.document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ 
        behavior: 'smooth',
        block: 'start'
      });
    }
  }
}