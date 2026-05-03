import { Injectable, Injector } from '@angular/core';
import {HttpRequest, HttpHandler, HttpEvent, HttpInterceptor,} from '@angular/common/http';
import { Observable } from 'rxjs';

//Interceptor que añade automáticamente el token JWT en la cabecera Authorization de cada petición HTTP.
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private injector: Injector) {}

  //Clona la petición original y le añade el token Bearer si existe en localStorage.
  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = localStorage.getItem('token');

    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
    }

    return next.handle(req);
  }
}

