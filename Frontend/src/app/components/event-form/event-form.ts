import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { EventService } from '../../services/eventService';
import { LocalService } from '../../services/localService';
import { Local } from '../../models';
import { MapView } from '../map-view/map-view';

//Componente del formulario para crear nuevos eventos con selección de local o ubicación manual.
@Component({
  selector: 'app-event-form',
  standalone: true,
  imports: [FormsModule, CommonModule, MapView],
  templateUrl: './event-form.html',
  styleUrls: ['./event-form.scss'],
})
export class EventForm implements OnInit {

  showMapSelector: boolean = false;
  locals: Local[] = [];

  //Datos del evento que se enviarán al backend
  eventData = {
    name: '',
    description: '',
    category: '',
    startDate: '',
    endDate: '',
    latitude: null as number | null,
    longitude: null as number | null,
    address: '',
    localId: null as number | null,
    status: ''
  };

  constructor(
    private eventService: EventService,
    private localService: LocalService,
    private router: Router
  ) {}

  ngOnInit() {
    this.localService.getLocals().subscribe({
      next: (data) => this.locals = data,
      error: (err) => console.error('Error al cargar locales:', err)
    });
  }

  onSubmit() {
    this.eventService.createEvent(this.eventData).subscribe({
      next: (res: any) => {
        alert('Evento creado correctamente: ' + res.name);
        this.router.navigate(['/event-list']);
      },
      error: (err: any) => {
        const msg = err.error?.message || err.statusText || 'Error desconocido';
        alert('Error al crear el evento: ' + msg);
        console.error('Error al crear el evento:', err);
      }
    });
  }

  onLocalSelected() {
    const local = this.locals.find(l => l.id === this.eventData.localId);
    if (local) {
      this.eventData.latitude = local.latitude;
      this.eventData.longitude = local.longitude;
      this.eventData.address = local.ubication;
    }
  }

  onCoordinatesSelected(coordinates: { latitude: number, longitude: number, address: string }) {
    this.eventData.latitude = coordinates.latitude;
    this.eventData.longitude = coordinates.longitude;
    this.eventData.address = coordinates.address;
    this.showMapSelector = false;
  }
}
