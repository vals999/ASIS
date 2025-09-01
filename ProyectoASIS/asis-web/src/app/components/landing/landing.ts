import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header';
import { HeroComponent } from '../hero/hero';
import { AboutComponent } from '../about/about';
import { ContactComponent } from '../contact/contact';
import { FooterComponent } from '../footer/footer';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, HeaderComponent, HeroComponent, AboutComponent, ContactComponent, FooterComponent],
  templateUrl: './landing.html',
  styleUrls: ['./landing.css']
})
export class LandingComponent {

  constructor(private router: Router) {}

  goToLogin() {
    this.router.navigate(['/login']);
  }

  goToContact() {
    // Scroll to contact section
    document.getElementById('contacto')?.scrollIntoView({ 
      behavior: 'smooth' 
    });
  }
}