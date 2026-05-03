import { Component, OnInit } from '@angular/core';
import { UserEventService } from '../../services/userEventService';
import { Events } from '../../models/eventModel';
import { CommonModule } from '@angular/common';
import { EventList } from '../event-list/event-list';

//Componente que muestra los eventos a los que el usuario ha dado like.
@Component({
  selector: 'user-likes',
  templateUrl: './user-likes.html',
  imports: [CommonModule, EventList]
})
export class UserLikesComponent implements OnInit {
  likedEvents: Events[] = [];
  loaded = false;
  error: string = '';

  constructor(private userEventService: UserEventService) {}
  ngOnInit() {
    this.getLikedEvents();
  }
  getLikedEvents() {
    this.userEventService.getLikedByUser().subscribe({
      next: (events) => {
        this.likedEvents = events;
        this.loaded = true;
        this.error = events.length ? '' : 'No tienes eventos con MG.';
      },
      error: () => {
        this.likedEvents = [];
        this.loaded = true;
        this.error = 'Error al cargar tus MG.';
      }
    });
  }
}
