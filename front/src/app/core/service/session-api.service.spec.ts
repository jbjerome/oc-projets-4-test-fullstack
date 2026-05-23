import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../models/session.interface';

const mkSession = (id = 1): Session => ({
  id,
  name: `Session ${id}`,
  description: 'desc',
  date: new Date('2026-01-01'),
  teacher_id: 1,
  users: [],
});

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SessionApiService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('all() should GET api/session and return list', () => {
    const list = [mkSession(1), mkSession(2)];
    let received: Session[] | undefined;

    service.all().subscribe(r => (received = r));

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush(list);
    expect(received).toEqual(list);
  });

  it('detail() should GET api/session/:id', () => {
    let received: Session | undefined;
    service.detail('42').subscribe(r => (received = r));

    const req = httpMock.expectOne('api/session/42');
    expect(req.request.method).toBe('GET');
    const s = mkSession(42);
    req.flush(s);
    expect(received).toEqual(s);
  });

  it('delete() should DELETE api/session/:id', () => {
    let done = false;
    service.delete('42').subscribe(() => (done = true));

    const req = httpMock.expectOne('api/session/42');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
    expect(done).toBe(true);
  });

  it('create() should POST api/session with the session', () => {
    const s = mkSession();
    let received: Session | undefined;

    service.create(s).subscribe(r => (received = r));

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(s);
    req.flush(s);
    expect(received).toEqual(s);
  });

  it('update() should PUT api/session/:id', () => {
    const s = mkSession(7);
    let received: Session | undefined;

    service.update('7', s).subscribe(r => (received = r));

    const req = httpMock.expectOne('api/session/7');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(s);
    req.flush(s);
    expect(received).toEqual(s);
  });

  it('participate() should POST api/session/:id/participate/:userId with null body', () => {
    let done = false;
    service.participate('5', '9').subscribe(() => (done = true));

    const req = httpMock.expectOne('api/session/5/participate/9');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();
    req.flush(null);
    expect(done).toBe(true);
  });

  it('unParticipate() should DELETE api/session/:id/participate/:userId', () => {
    let done = false;
    service.unParticipate('5', '9').subscribe(() => (done = true));

    const req = httpMock.expectOne('api/session/5/participate/9');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
    expect(done).toBe(true);
  });
});
