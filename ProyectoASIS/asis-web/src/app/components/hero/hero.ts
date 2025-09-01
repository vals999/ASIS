import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './hero.html',
  styleUrls: ['./hero.css']
})
export class HeroComponent {

  constructor(private router: Router) {}

  scheduleAppointment() {
    // Por ahora, scroll al contacto - puedes implementar la lógica de agendamiento más tarde
    this.scrollToSection('contacto');
  }

  learnAboutServices() {
    this.scrollToSection('servicios');
  }

  private scrollToSection(sectionId: string) {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ 
        behavior: 'smooth',
        block: 'start'
      });
    }
  }
}