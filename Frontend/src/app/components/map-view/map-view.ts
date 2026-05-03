import { Component, AfterViewInit, Input, Output, EventEmitter, OnChanges, SimpleChanges, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import * as L from 'leaflet';
import { EventService } from '../../services/eventService';
import { Events } from '../../models';

let mapInstanceId = 0;

//Componente del mapa interactivo con Leaflet para mostrar y seleccionar ubicaciones de eventos.
@Component({
  selector: 'app-map-view',
  standalone: true,
  imports: [],
  templateUrl: './map-view.html',
  styleUrls: ['./map-view.scss']
})
export class MapView implements AfterViewInit, OnChanges, OnDestroy {
  private map: any;
  private selectedMarker: any = null;
  private markersLayer: L.LayerGroup = L.layerGroup();
  private _events: Events[] = [];

  mapId: string;
  
  @Input() isInteractive: boolean = false;
  @Input() events: Events[] | null = null;
  @Output() coordinatesSelected = new EventEmitter<{ latitude: number, longitude: number, address: string }>();

  constructor(private eventService: EventService, private router: Router) {
    this.mapId = 'map-' + (++mapInstanceId);
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.mapaInicial();
      if (this.events !== null) {
        this._events = this.events;
        this.agregarMarcadores();
      } else {
        this.loadEvents();
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['events'] && !changes['events'].firstChange && this.map) {
      this._events = this.events || [];
      this.agregarMarcadores();
    }
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  private loadEvents(): void {
    this.eventService.getAllEvents().subscribe({
      next: (events: Events[]) => {
        this._events = events;
        this.agregarMarcadores();
      },
      error: (err) => {
        console.error('Error al cargar los eventos:', err);
      }
    });
  }

  private agregarMarcadores(): void {
    this.markersLayer.clearLayers();

    this._events.forEach((event) => {
      const lat = Number(event.latitude);
      const lng = Number(event.longitude);

      const popupContent = document.createElement('div');
      popupContent.innerHTML = 
        `<b>${event.name}</b><br>` +
        `Categoría: ${event.category}<br>` +
        `Fecha: ${new Date(event.startDate).toLocaleDateString('es-ES')}<br>` +
        `<a href="#" class="event-link" data-event-id="${event.id}" style="color: #0066cc; text-decoration: none; font-weight: bold;">Ver más</a>`;

      const marker = L.marker([lat, lng]).addTo(this.markersLayer);
      marker.bindPopup(popupContent);

      popupContent.addEventListener('click', (e: any) => {
        if (e.target.classList.contains('event-link')) {
          e.preventDefault();
          const eventId = e.target.getAttribute('data-event-id');
          this.router.navigate(['/event', eventId]);
        }
      });
    });
  }

  private mapaInicial(): void {
    const southWest = L.latLng(39.20, 2.25);
    const northEast = L.latLng(40.05, 3.50);
    const mallorcaBounds = L.latLngBounds(southWest, northEast);

    this.map = L.map(this.mapId, {
      center: [39.695, 3.018],
      zoom: 10,
      minZoom: 9.8,
      maxZoom: 16,
      maxBounds: mallorcaBounds,
      maxBoundsViscosity: 1.0
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    this.markersLayer.addTo(this.map);

    this.fixLeafletIcons();
    if (this.isInteractive) {
      this.addMapClickListener();
    }
  }

  private addMapClickListener(): void {
    this.map.on('click', (e: any) => {
      const lat = e.latlng.lat;
      const lng = e.latlng.lng;
      this.selectCoordinates(lat, lng);
    });
  }

  private selectCoordinates(lat: number, lng: number): void {
    if (this.selectedMarker) {
      this.map.removeLayer(this.selectedMarker);
    }

    const selectedIcon = L.icon({
      iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-blue.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });

    this.selectedMarker = L.marker([lat, lng], { icon: selectedIcon })
      .bindPopup(`<b>Coordenadas seleccionadas</b><br>Lat: ${lat.toFixed(4)}<br>Lng: ${lng.toFixed(4)}`)
      .addTo(this.map)
      .openPopup();

    this.coordinatesSelected.emit({
      latitude: lat,
      longitude: lng,
      address: `${lat.toFixed(4)}, ${lng.toFixed(4)}`
    });
  }

  private fixLeafletIcons(): void {
    const defaultIcon = L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png',
      iconRetinaUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      tooltipAnchor: [16, -28],
      shadowSize: [41, 41]
    });
    L.Marker.prototype.options.icon = defaultIcon;
  }
}