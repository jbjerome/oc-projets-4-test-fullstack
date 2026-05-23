import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';

import { UnauthGuard } from './unauth.guard';
import { SessionService } from '../core/service/session.service';

describe('UnauthGuard', () => {
  let guard: UnauthGuard;
  let router: { navigate: jest.Mock };
  let sessionService: SessionService;

  beforeEach(() => {
    router = { navigate: jest.fn() };
    TestBed.configureTestingModule({
      providers: [
        UnauthGuard,
        SessionService,
        { provide: Router, useValue: router },
      ],
    });
    guard = TestBed.inject(UnauthGuard);
    sessionService = TestBed.inject(SessionService);
  });

  it('returns true when the user is logged out', () => {
    sessionService.isLogged = false;
    expect(guard.canActivate()).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('redirects and returns false when the user is logged in', () => {
    sessionService.isLogged = true;
    expect(guard.canActivate()).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['rentals']);
  });
});
