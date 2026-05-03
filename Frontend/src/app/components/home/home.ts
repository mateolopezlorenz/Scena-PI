import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

//Componente de la página de inicio que muestra el nombre del usuario logueado.
@Component({
  selector: 'home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.html',
  styleUrls: ['./home.scss'],
})
export class Home {

  username: string | null = '';

  ngOnInit() {
    this.username = localStorage.getItem('name');
  }
}
