import { Component, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';

//Interfaz para los filtros de búsqueda de eventos.
export interface EventFilters {
  category: string;
  date: string;
  search: string;
}

//Componente de filtros para buscar eventos por categoría, fecha y texto.
@Component({
  selector: 'event-filters',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './event-filters.html',
  styleUrls: ['./event-filters.scss']
})
export class EventFiltersComponent {
  category: string = '';
  date: string = '';
  search: string = '';

  @Output() filtersChanged = new EventEmitter<EventFilters>();

  applyFilters() {
    this.filtersChanged.emit({
      category: this.category,
      date: this.date,
      search: this.search
    });
  }

  resetFilters() {
    this.category = '';
    this.date = '';
    this.search = '';
    this.filtersChanged.emit({ category: '', date: '', search: '' });
  }
}
