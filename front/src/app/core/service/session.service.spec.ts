import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { SessionInformation } from '../models/sessionInformation.interface';

const fakeUser: SessionInformation = {
  token: 't', type: 'Bearer', id: 1,
  username: 'a@b.com', firstName: 'Jane', lastName: 'Doe', admin: true,
};

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created and start logged-out', () => {
    expect(service).toBeTruthy();
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('logIn() should set sessionInformation, flip isLogged and emit true', async () => {
    const emitted: boolean[] = [];
    service.$isLogged().subscribe(v => emitted.push(v));

    service.logIn(fakeUser);

    expect(service.sessionInformation).toEqual(fakeUser);
    expect(service.isLogged).toBe(true);
    await expect(firstValueFrom(service.$isLogged())).resolves.toBe(true);
    expect(emitted).toEqual([false, true]);
  });

  it('logOut() should clear sessionInformation, flip isLogged and emit false', () => {
    service.logIn(fakeUser);
    const emitted: boolean[] = [];
    service.$isLogged().subscribe(v => emitted.push(v));

    service.logOut();

    expect(service.sessionInformation).toBeUndefined();
    expect(service.isLogged).toBe(false);
    expect(emitted).toEqual([true, false]);
  });
});
