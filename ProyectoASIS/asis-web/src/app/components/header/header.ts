import { Component, OnInit, OnDestroy, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.html',
  styleUrls: ['./header.css']
})
export class HeaderComponent implements OnInit, OnDestroy {

  constructor(private router: Router, private elementRef: ElementRef) {}

  ngOnInit() {
    // Agregar listener para el evento scroll
    window.addEventListener('scroll', this.onScroll.bind(this));
  }

  ngOnDestroy() {
    // Limpiar el listener cuando el componente se destruya
    window.removeEventListener('scroll', this.onScroll.bind(this));
  }

  private onScroll() {
    const header = this.elementRef.nativeElement.querySelector('header');
    if (header) {
      if (window.scrollY > 50) {
        header.classList.add('scrolled');
      } else {
        header.classList.remove('scrolled');
      }
    }
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }

  goToHome() {
    this.router.navigate(['/']);
  }

  scrollToSection(sectionId: string) {
    const element = document.getElementById(sectionId);
    if (element) {
      // Ajustar el offset para compensar el header fijo
      const headerHeight = 80; // Altura aproximada del header
      const elementPosition = element.offsetTop - headerHeight;
      
      window.scrollTo({
        top: elementPosition,
        behavior: 'smooth'
      });
    }
  }

  scheduleAppointment() {
    // Por ahora, scroll al contacto - puedes implementar la lógica de agendamiento más tarde
    this.scrollToSection('contacto');
  }
}