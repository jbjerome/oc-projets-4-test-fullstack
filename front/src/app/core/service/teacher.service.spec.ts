import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';
import { Teacher } from '../models/teacher.interface';

const mkTeacher = (id = 1): Teacher => ({
  id, firstName: 'Jane', lastName: 'Doe',
  createdAt: new Date(), updatedAt: new Date(),
});

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TeacherService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('all() should GET api/teacher', () => {
    const list = [mkTeacher(1), mkTeacher(2)];
    let received: Teacher[] | undefined;

    service.all().subscribe(r => (received = r));

    const req = httpMock.expectOne('api/teacher');
    expect(req.request.method).toBe('GET');
    req.flush(list);
    expect(received).toEqual(list);
  });

  it('detail() should GET api/teacher/:id', () => {
    let received: Teacher | undefined;
    service.detail('7').subscribe(r => (received = r));

    const req = httpMock.expectOne('api/teacher/7');
    expect(req.request.method).toBe('GET');
    const t = mkTeacher(7);
    req.flush(t);
    expect(received).toEqual(t);
  });
});
