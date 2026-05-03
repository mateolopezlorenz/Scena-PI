import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LocalService } from '../../services/localService';
import { Local } from '../../models';

//Componente que muestra la lista de locales con opciones de edición y eliminación.
@Component({
  selector: 'app-local-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './local-list.html',
  styleUrls: ['./local-list.scss'],
})
export class LocalList implements OnInit {

  locals: Local[] = [];
  isLoggedIn = false;
  editingLocalId: number | null = null;

  editData = {
    name: '',
    latitude: 0,
    longitude: 0,
    ubication: '',
    capacity: 1,
    rooms: 1
  };

  constructor(private localService: LocalService) {}

  ngOnInit() {
    this.isLoggedIn = !!localStorage.getItem('token');
    this.loadLocals();
  }

  loadLocals() {
    this.localService.getLocals().subscribe({
      next: (data) => this.locals = data,
      error: (err) => console.error('Error al cargar locales', err)
    });
  }

  startEdit(local: Local) {
    this.editingLocalId = local.id;
    this.editData = {
      name: local.name,
      latitude: local.latitude,
      longitude: local.longitude,
      ubication: local.ubication,
      capacity: local.capacity,
      rooms: local.rooms
    };
  }

  cancelEdit() {
    this.editingLocalId = null;
  }

  saveEdit(id: number) {
    this.localService.updateLocal(id, this.editData).subscribe({
      next: () => {
        this.editingLocalId = null;
        this.loadLocals();
      },
      error: (err) => {
        const msg = err.error?.message || err.statusText || 'Error desconocido';
        alert('Error al actualizar: ' + msg);
      }
    });
  }

  deleteLocal(id: number) {
    if (!confirm('¿Estás seguro de que quieres eliminar este local?')) return;
    this.localService.deleteLocal(id).subscribe({
      next: () => this.loadLocals(),
      error: (err) => {
        const msg = err.error?.message || err.statusText || 'Error desconocido';
        alert('Error al eliminar: ' + msg);
      }
    });
  }
}
