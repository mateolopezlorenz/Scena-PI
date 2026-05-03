import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Authservice } from '../../services/authservice';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { RegisterRequest } from '../../models';
import { Router } from '@angular/router'

//Componente del formulario de registro para crear nuevos usuarios.
@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterModule],
  standalone: true,
  templateUrl: './register.html',
  styleUrls: ['./register.scss'],
})
export class Register {
  registerData: RegisterRequest = {
    name: '',
    email: '',
    password: '',
    password2: ''
  }


  constructor(private authService: Authservice, private router: Router) {}

  onSubmit() {
    if (this.registerData.password !== this.registerData.password2) {
      alert('Las contraseñas no coinciden');
      return;
    }

    this.authService.register(this.registerData).subscribe({
      next: () => {
        alert('Registro hecho.')
        this.router.navigate(["/login"]);
      },
      error: (err) => {
        let errorMessage = 'Error en el registro';
        if (err.error && err.error.message) {
          errorMessage = err.error.message;
        } else if (typeof err.error === 'string') {
          errorMessage = err.error;
        } else if (err.message) {
          errorMessage = err.message;
        }

        alert('Error en el registro: ' + errorMessage);
        console.error('Error en el registro:', err);
      }
    })

  }
}
