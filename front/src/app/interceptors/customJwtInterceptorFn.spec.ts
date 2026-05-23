import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { expect } from '@jest/globals';

import { customJwtInterceptorFn } from './customJwtInterceptorFn';
import { SessionService } from '../core/service/session.service';

describe('customJwtInterceptorFn', () => {
  let httpMock: HttpTestingController;
  let sessionService: SessionService;
  let http: HttpClient;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SessionService,
        provideHttpClient(withInterceptors([customJwtInterceptorFn])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    sessionService = TestBed.inject(SessionService);
  });

  afterEach(() => httpMock.verify());

  it('does not add an Authorization header when the user is logged out', () => {
    http.get('/api/anything').subscribe();
    const req = httpMock.expectOne('/api/anything');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush({});
  });

  it('adds Bearer <token> when the user is logged in', () => {
    sessionService.logIn({
      token: 'abc123', type: 'Bearer', id: 1,
      username: 'a', firstName: 'a', lastName: 'b', admin: false,
    });

    http.get('/api/anything').subscribe();
    const req = httpMock.expectOne('/api/anything');
    expect(req.request.headers.get('Authorization')).toBe('Bearer abc123');
    req.flush({});
  });
});
