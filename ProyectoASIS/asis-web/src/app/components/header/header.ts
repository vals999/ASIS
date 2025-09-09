import { Component, OnInit, OnDestroy, ElementRef, Inject, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { isPlatformBrowser, DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.html',
  styleUrls: ['./header.css']
})
export class HeaderComponent implements OnInit, OnDestroy {

  constructor(
    private router: Router, 
    private elementRef: ElementRef,
    @Inject(PLATFORM_ID) private platformId: Object,
    @Inject(DOCUMENT) private document: Document
  ) {}

  ngOnInit() {
    // Solo agregar listener si estamos en el navegador
    if (isPlatformBrowser(this.platformId)) {
      window.addEventListener('scroll', this.onScroll.bind(this));
    }
  }

  ngOnDestroy() {
    // Solo remover listener si estamos en el navegador
    if (isPlatformBrowser(this.platformId)) {
      window.removeEventListener('scroll', this.onScroll.bind(this));
    }
  }

  private onScroll() {
    if (!isPlatformBrowser(this.platformId)) return;
    
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
    if (!isPlatformBrowser(this.platformId)) return;
    
    const element = this.document.getElementById(sectionId);
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