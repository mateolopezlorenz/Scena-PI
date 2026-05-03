import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { Authservice } from '../../services/authservice';

//Componente de la barra de navegación con enlaces y gestión de sesión.
@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.scss'],
})
export class Navbar {

  username: string | null = null;
  isMenuOpen = false;

  constructor(private router: Router, private authService: Authservice) {
    this.router.events.subscribe(() => {
      this.checkUser();
      this.isMenuOpen = false;
    });
  }

  ngOnInit() {
    this.checkUser();
  }

  checkUser() {
    this.username = localStorage.getItem('name');
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  logout() {
    this.authService.logout();
    this.username = null;
    this.router.navigate(['/login']);
  }
}
