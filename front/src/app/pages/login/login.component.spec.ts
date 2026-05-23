import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { expect } from '@jest/globals';
import { throwError } from 'rxjs';

import { LoginComponent } from './login.component';
import { AuthService } from '../../core/service/auth.service';
import { SessionService } from '../../core/service/session.service';
import { SessionInformation } from '../../core/models/sessionInformation.interface';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let router: { navigate: jest.Mock };
  let sessionService: SessionService;
  let authService: AuthService;

  beforeEach(async () => {
    router = { navigate: jest.fn() };
    await TestBed.configureTestingModule({
      imports: [LoginComponent, BrowserAnimationsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        SessionService,
        AuthService,
        { provide: Router, useValue: router },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    authService = TestBed.inject(AuthService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('form validation', () => {
    it('marks the form invalid when fields are empty (required)', () => {
      expect(component.form.valid).toBe(false);
      expect(component.form.controls.email.errors).toEqual({ required: true });
      expect(component.form.controls.password.errors).toEqual({ required: true });
    });

    it('rejects an invalid email', () => {
      component.form.controls.email.setValue('not-an-email');
      component.form.controls.password.setValue('secret');
      expect(component.form.controls.email.errors).toEqual({ email: true });
      expect(component.form.valid).toBe(false);
    });

    it('disables the submit button while the form is invalid', () => {
      const submit = fixture.debugElement.query(By.css('button[type="submit"]'));
      expect(submit.nativeElement.disabled).toBe(true);

      component.form.controls.email.setValue('a@b.com');
      component.form.controls.password.setValue('secret');
      fixture.detectChanges();
      expect(submit.nativeElement.disabled).toBe(false);
    });
  });

  describe('submit()', () => {
    const validResponse: SessionInformation = {
      token: 't', type: 'Bearer', id: 1,
      username: 'a@b.com', firstName: 'Jane', lastName: 'Doe', admin: false,
    };

    beforeEach(() => {
      component.form.controls.email.setValue('a@b.com');
      component.form.controls.password.setValue('secret');
    });

    it('logs the user in and navigates to /sessions on success', () => {
      const httpMock = TestBed.inject(HttpTestingController);
      const logInSpy = jest.spyOn(sessionService, 'logIn');

      component.submit();
      const req = httpMock.expectOne('/api/auth/login');
      expect(req.request.method).toBe('POST');
      req.flush(validResponse);

      expect(logInSpy).toHaveBeenCalledWith(validResponse);
      expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
      expect(component.onError).toBe(false);
    });

    it('sets onError to true when credentials are wrong', () => {
      jest.spyOn(authService, 'login').mockReturnValue(throwError(() => new Error('401')));

      component.submit();

      expect(component.onError).toBe(true);
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('renders an "An error occurred" message when onError is true', () => {
      component.onError = true;
      fixture.detectChanges();
      const errEl: HTMLElement = fixture.nativeElement.querySelector('.error');
      expect(errEl).not.toBeNull();
      expect(errEl.textContent).toContain('An error occurred');
    });
  });
});
