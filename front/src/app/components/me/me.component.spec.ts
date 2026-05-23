import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { expect } from '@jest/globals';

import { MeComponent } from './me.component';
import { SessionService } from '../../core/service/session.service';
import { User } from '../../core/models/user.interface';

const mkUser = (admin = false): User => ({
  id: 1, email: 'a@b.com', firstName: 'Jane', lastName: 'Doe',
  admin, password: '', createdAt: new Date('2026-01-01'),
  updatedAt: new Date('2026-01-02'),
});

const setup = async (admin: boolean) => {
  const sessionService = {
    sessionInformation: { admin, id: 1 },
    logOut: jest.fn(),
  };
  const router = { navigate: jest.fn() };

  await TestBed.configureTestingModule({
    imports: [MeComponent, BrowserAnimationsModule],
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      { provide: SessionService, useValue: sessionService },
      { provide: Router, useValue: router },
    ],
  }).compileComponents();

  const fixture = TestBed.createComponent(MeComponent);
  const httpMock = TestBed.inject(HttpTestingController);
  fixture.detectChanges();
  httpMock.expectOne('api/user/1').flush(mkUser(admin));
  fixture.detectChanges();
  return { fixture, component: fixture.componentInstance, httpMock, router, sessionService };
};

describe('MeComponent', () => {
  it('renders the user’s name and email', async () => {
    const { fixture } = await setup(false);
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('Jane DOE');
    expect(text).toContain('a@b.com');
  });

  it('shows "You are admin" for admin users and hides the delete button', async () => {
    const { fixture } = await setup(true);
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('You are admin');
    const deleteBtn = fixture.debugElement.query(By.css('button[color="warn"]'));
    expect(deleteBtn).toBeNull();
  });

  it('shows the delete button for non-admin users', async () => {
    const { fixture } = await setup(false);
    const deleteBtn = fixture.debugElement.query(By.css('button[color="warn"]'));
    expect(deleteBtn).not.toBeNull();
  });

  it('delete() calls DELETE api/user/:id, logs the user out and navigates home', async () => {
    const ctx = await setup(false);
    const openSpy = jest.fn();
    (ctx.component as any).matSnackBar = { open: openSpy };

    ctx.component.delete();
    const req = ctx.httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);

    expect(openSpy).toHaveBeenCalledWith('Your account has been deleted !', 'Close', { duration: 3000 });
    expect(ctx.sessionService.logOut).toHaveBeenCalled();
    expect(ctx.router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('back() calls window.history.back()', async () => {
    const { component } = await setup(true);
    const spy = jest.spyOn(window.history, 'back').mockImplementation(() => undefined);
    component.back();
    expect(spy).toHaveBeenCalled();
    spy.mockRestore();
  });
});
