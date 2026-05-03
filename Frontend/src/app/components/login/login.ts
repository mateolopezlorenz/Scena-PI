import { Component } from '@angular/core';
import { Authservice } from '../../services/authservice';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

//Componente del formulario de login para autenticar usuarios existentes.
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterModule, CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss'],
})
export class Login {

  loginData = {
    email: '',
    password: ''
  }

  constructor(private authService: Authservice, private router: Router) {}

  onSubmit() {

    if (!this.loginData.email || !this.loginData.password) {
      alert('Todos los datos son obligatorios!');
      return;
    }


    this.authService.login(this.loginData).subscribe({
      next: (res: any) => {

        this.authService.saveSession(res);

        this.router.navigate(["/home"])
      },
      error: (err: any) => {
        
        alert('Error en el login: ' + err.error.message);
        console.error('Error en el login:', err);
      }
    });

  }
}