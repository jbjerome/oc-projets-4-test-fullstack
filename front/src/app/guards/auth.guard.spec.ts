import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';

import { AuthGuard } from './auth.guard';
import { SessionService } from '../core/service/session.service';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let router: { navigate: jest.Mock };
  let sessionService: SessionService;

  beforeEach(() => {
    router = { navigate: jest.fn() };
    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        SessionService,
        { provide: Router, useValue: router },
      ],
    });
    guard = TestBed.inject(AuthGuard);
    sessionService = TestBed.inject(SessionService);
  });

  it('returns true when the user is logged in', () => {
    sessionService.isLogged = true;
    expect(guard.canActivate()).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('redirects to /login and returns false when the user is not logged in', () => {
    sessionService.isLogged = false;
    expect(guard.canActivate()).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['login']);
  });
});
