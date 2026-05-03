import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LocalService } from '../../services/localService';
import { FormsModule } from '@angular/forms';

//Componente del formulario para crear nuevos locales.
@Component({
  selector: 'app-local-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './local-form.html',
  styleUrl: './local-form.scss',
})
export class LocalForm {

  localData = {
    name: '',
    latitude: 0,
    longitude: 0,
    ubication: '',
    capacity: 1,
    rooms: 1
  };

  constructor(private localService: LocalService, private router: Router) {}

  onSubmit() {
    this.localService.crearLocal(this.localData).subscribe({
      next: () => {
        alert('Local creado con éxito');
        this.router.navigate(['/local-list']);
      },
      error: (err: any) => {
        const msg = err.error?.message || err.statusText || 'Error desconocido';
        alert('Error al crear el local: ' + msg);
      }
    });
  }
}
