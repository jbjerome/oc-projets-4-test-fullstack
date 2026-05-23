import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { expect } from '@jest/globals';
import { throwError } from 'rxjs';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../core/service/auth.service';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let router: { navigate: jest.Mock };
  let authService: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    router = { navigate: jest.fn() };
    await TestBed.configureTestingModule({
      imports: [RegisterComponent, BrowserAnimationsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        AuthService,
        { provide: Router, useValue: router },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('form validation', () => {
    it('is invalid while all fields are empty (required)', () => {
      expect(component.form.valid).toBe(false);
      ['email', 'firstName', 'lastName', 'password'].forEach(name => {
        expect(component.form.get(name)!.errors).toEqual({ required: true });
      });
    });

    it('rejects invalid email', () => {
      component.form.patchValue({
        email: 'nope', firstName: 'Jane', lastName: 'Doe', password: 'secret',
      });
      expect(component.form.get('email')!.errors).toEqual({ email: true });
      expect(component.form.valid).toBe(false);
    });

    it('disables submit until valid', () => {
      const submit = fixture.debugElement.query(By.css('button[type="submit"]'));
      expect(submit.nativeElement.disabled).toBe(true);

      component.form.patchValue({
        email: 'a@b.com', firstName: 'Jane', lastName: 'Doe', password: 'secret',
      });
      fixture.detectChanges();
      expect(submit.nativeElement.disabled).toBe(false);
    });
  });

  describe('submit()', () => {
    beforeEach(() => {
      component.form.patchValue({
        email: 'a@b.com', firstName: 'Jane', lastName: 'Doe', password: 'secret',
      });
    });

    it('navigates to /login after a successful registration', () => {
      component.submit();
      const req = httpMock.expectOne('/api/auth/register');
      expect(req.request.method).toBe('POST');
      req.flush(null);

      expect(router.navigate).toHaveBeenCalledWith(['/login']);
      expect(component.onError).toBe(false);
    });

    it('sets onError to true when the API fails', () => {
      jest.spyOn(authService, 'register').mockReturnValue(throwError(() => new Error('boom')));

      component.submit();

      expect(component.onError).toBe(true);
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('renders an error message when onError is true', () => {
      component.onError = true;
      fixture.detectChanges();
      const err: HTMLElement = fixture.nativeElement.querySelector('.error');
      expect(err.textContent).toContain('An error occurred');
    });
  });
});
