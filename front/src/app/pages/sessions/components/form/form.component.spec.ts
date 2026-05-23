import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { expect } from '@jest/globals';

import { FormComponent } from './form.component';
import { SessionService } from '../../../../core/service/session.service';

const teachers = [
  { id: 1, firstName: 'Jane', lastName: 'Doe', createdAt: new Date(), updatedAt: new Date() },
];

const setup = async (opts: { admin?: boolean; url?: string } = {}) => {
  const admin = opts.admin ?? true;
  const url = opts.url ?? '/sessions/create';
  const navigate = jest.fn();
  const router: any = { navigate, get url() { return url; } };

  await TestBed.configureTestingModule({
    imports: [FormComponent, BrowserAnimationsModule],
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      { provide: SessionService, useValue: { sessionInformation: { admin, id: 1 } } },
      { provide: Router, useValue: router },
      { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '7' } } } },
    ],
  }).compileComponents();

  const fixture = TestBed.createComponent(FormComponent);
  const httpMock = TestBed.inject(HttpTestingController);
  fixture.detectChanges();
  return { fixture, component: fixture.componentInstance, httpMock, router };
};

const flushTeachers = (httpMock: HttpTestingController) => {
  httpMock.expectOne('api/teacher').flush(teachers);
};

describe('FormComponent', () => {
  it('redirects non-admin users to /sessions', async () => {
    const ctx = await setup({ admin: false });
    expect(ctx.router.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  describe('create mode', () => {
    let ctx: Awaited<ReturnType<typeof setup>>;
    let fixture: ComponentFixture<FormComponent>;

    beforeEach(async () => {
      ctx = await setup({ url: '/sessions/create' });
      fixture = ctx.fixture;
      flushTeachers(ctx.httpMock);
    });

    it('opens in create mode and renders the Create title', () => {
      expect(ctx.component.onUpdate).toBe(false);
      expect((fixture.nativeElement as HTMLElement).textContent).toContain('Create session');
    });

    it('initialises an empty form with required validators', () => {
      const form = ctx.component.sessionForm!;
      expect(form).toBeDefined();
      expect(form.valid).toBe(false);
      ['name', 'date', 'teacher_id', 'description'].forEach(c =>
        expect(form.get(c)!.hasError('required')).toBe(true)
      );
    });

    it('disables Save while the form is invalid', () => {
      const save = fixture.debugElement.query(By.css('button[type="submit"]'));
      expect(save.nativeElement.disabled).toBe(true);

      ctx.component.sessionForm!.patchValue({
        name: 'Hatha', date: '2026-02-01', teacher_id: 1, description: 'desc',
      });
      fixture.detectChanges();
      expect(save.nativeElement.disabled).toBe(false);
    });

    it('submit() POSTs api/session, opens a snackbar and goes back to sessions', () => {
      const openSpy = jest.fn();
      (ctx.component as any).matSnackBar = { open: openSpy };

      ctx.component.sessionForm!.patchValue({
        name: 'Hatha', date: '2026-02-01', teacher_id: 1, description: 'desc',
      });
      ctx.component.submit();

      const req = ctx.httpMock.expectOne('api/session');
      expect(req.request.method).toBe('POST');
      req.flush({});

      expect(openSpy).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
      expect(ctx.router.navigate).toHaveBeenCalledWith(['sessions']);
    });
  });

  describe('update mode', () => {
    it('loads the session into the form and PUTs api/session/:id on submit', async () => {
      const ctx = await setup({ url: '/sessions/update/7' });
      const session = {
        id: 7, name: 'Yin', description: 'slow', date: new Date('2026-02-15'),
        teacher_id: 1, users: [],
      };
      ctx.httpMock.expectOne('api/session/7').flush(session);
      ctx.fixture.detectChanges();
      flushTeachers(ctx.httpMock);

      expect(ctx.component.onUpdate).toBe(true);
      expect((ctx.fixture.nativeElement as HTMLElement).textContent).toContain('Update session');
      expect(ctx.component.sessionForm!.value).toEqual({
        name: 'Yin',
        date: '2026-02-15',
        teacher_id: 1,
        description: 'slow',
      });

      const openSpy = jest.fn();
      (ctx.component as any).matSnackBar = { open: openSpy };
      ctx.component.submit();

      const req = ctx.httpMock.expectOne('api/session/7');
      expect(req.request.method).toBe('PUT');
      req.flush(session);

      expect(openSpy).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
      expect(ctx.router.navigate).toHaveBeenCalledWith(['sessions']);
    });
  });
});
