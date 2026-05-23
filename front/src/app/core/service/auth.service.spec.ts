import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { expect } from '@jest/globals';

import { AuthService } from './auth.service';
import { LoginRequest } from '../models/loginRequest.interface';
import { RegisterRequest } from '../models/registerRequest.interface';
import { SessionInformation } from '../models/sessionInformation.interface';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('register() should POST /api/auth/register with the request body', () => {
    const body: RegisterRequest = {
      email: 'a@b.com',
      firstName: 'Jane',
      lastName: 'Doe',
      password: 'secret',
    };
    let done = false;

    service.register(body).subscribe(() => (done = true));

    const req = httpMock.expectOne('/api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(body);
    req.flush(null);
    expect(done).toBe(true);
  });

  it('login() should POST /api/auth/login and return SessionInformation', () => {
    const body: LoginRequest = { email: 'a@b.com', password: 'secret' };
    const response: SessionInformation = {
      token: 't', type: 'Bearer', id: 1,
      username: 'a@b.com', firstName: 'Jane', lastName: 'Doe', admin: false,
    };
    let received: SessionInformation | undefined;

    service.login(body).subscribe(r => (received = r));

    const req = httpMock.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(body);
    req.flush(response);
    expect(received).toEqual(response);
  });
});
