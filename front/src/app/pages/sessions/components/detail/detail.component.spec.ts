import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { expect } from '@jest/globals';

import { DetailComponent } from './detail.component';
import { SessionService } from '../../../../core/service/session.service';

const fakeSession = (overrides: Partial<{ users: number[] }> = {}) => ({
  id: 5, name: 'Hatha class', description: 'desc',
  date: new Date('2026-02-01'), teacher_id: 9, users: [],
  createdAt: new Date(), updatedAt: new Date(),
  ...overrides,
});
const fakeTeacher = {
  id: 9, firstName: 'Jane', lastName: 'Doe',
  createdAt: new Date(), updatedAt: new Date(),
};

const setup = async (opts: { admin?: boolean; userId?: number; users?: number[] } = {}) => {
  const admin = opts.admin ?? false;
  const userId = opts.userId ?? 1;
  const users = opts.users ?? [];
  const router = { navigate: jest.fn() };
  const snack = { open: jest.fn() };

  await TestBed.configureTestingModule({
    imports: [DetailComponent, BrowserAnimationsModule],
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '5' } } } },
      { provide: SessionService, useValue: { sessionInformation: { admin, id: userId } } },
      { provide: Router, useValue: router },
      { provide: MatSnackBar, useValue: snack },
    ],
  }).compileComponents();

  const fixture = TestBed.createComponent(DetailComponent);
  const httpMock = TestBed.inject(HttpTestingController);
  fixture.detectChanges();

  httpMock.expectOne('api/session/5').flush(fakeSession({ users }));
  httpMock.expectOne('api/teacher/9').flush(fakeTeacher);
  fixture.detectChanges();
  return { fixture, component: fixture.componentInstance, httpMock, router, snack };
};

describe('DetailComponent', () => {
  let fixture: ComponentFixture<DetailComponent>;

  it('reads sessionId, isAdmin, userId from route + session service', async () => {
    const ctx = await setup({ admin: true, userId: 1 });
    expect(ctx.component.sessionId).toBe('5');
    expect(ctx.component.isAdmin).toBe(true);
    expect(ctx.component.userId).toBe('1');
  });

  it('displays the session info (name, teacher, attendees) in the template', async () => {
    const ctx = await setup({ admin: false, users: [42] });
    fixture = ctx.fixture;
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('Hatha Class');
    expect(text).toContain('Jane');
    expect(text).toContain('DOE');
    expect(text).toContain('1 attendees');
  });

  it('shows the Delete button for admins', async () => {
    const ctx = await setup({ admin: true });
    const labels = ctx.fixture.debugElement
      .queryAll(By.css('button span'))
      .map(d => (d.nativeElement as HTMLElement).textContent?.trim());
    expect(labels).toContain('Delete');
    expect(labels).not.toContain('Participate');
    expect(labels).not.toContain('Do not participate');
  });

  it('shows Participate when the user is not enrolled', async () => {
    const ctx = await setup({ admin: false, userId: 1, users: [] });
    const labels = ctx.fixture.debugElement
      .queryAll(By.css('button span'))
      .map(d => (d.nativeElement as HTMLElement).textContent?.trim());
    expect(labels).toContain('Participate');
    expect(labels).not.toContain('Do not participate');
    expect(labels).not.toContain('Delete');
  });

  it('shows "Do not participate" when the user is already enrolled', async () => {
    const ctx = await setup({ admin: false, userId: 1, users: [1] });
    const labels = ctx.fixture.debugElement
      .queryAll(By.css('button span'))
      .map(d => (d.nativeElement as HTMLElement).textContent?.trim());
    expect(labels).toContain('Do not participate');
    expect(labels).not.toContain('Participate');
  });

  it('delete() calls the API, shows a snackbar and navigates back to /sessions', async () => {
    const ctx = await setup({ admin: true });
    const openSpy = jest.fn();
    (ctx.component as any).matSnackBar = { open: openSpy };

    ctx.component.delete();
    const req = ctx.httpMock.expectOne('api/session/5');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);

    expect(openSpy).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(ctx.router.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('participate() POSTs and refreshes the view-model', async () => {
    const ctx = await setup({ admin: false, userId: 1, users: [] });
    ctx.component.participate();
    ctx.httpMock.expectOne('api/session/5/participate/1').flush(null);
    ctx.httpMock.expectOne('api/session/5').flush(fakeSession({ users: [1] }));
    ctx.httpMock.expectOne('api/teacher/9').flush(fakeTeacher);
  });

  it('unParticipate() DELETEs and refreshes the view-model', async () => {
    const ctx = await setup({ admin: false, userId: 1, users: [1] });
    ctx.component.unParticipate();
    ctx.httpMock.expectOne('api/session/5/participate/1').flush(null);
    ctx.httpMock.expectOne('api/session/5').flush(fakeSession({ users: [] }));
    ctx.httpMock.expectOne('api/teacher/9').flush(fakeTeacher);
  });

  it('back() calls window.history.back()', async () => {
    const ctx = await setup({ admin: true });
    const spy = jest.spyOn(window.history, 'back').mockImplementation(() => undefined);
    ctx.component.back();
    expect(spy).toHaveBeenCalled();
    spy.mockRestore();
  });
});
