import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Router, provideRouter } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { firstValueFrom } from 'rxjs';
import { expect } from '@jest/globals';

import { AppComponent } from './app.component';
import { SessionService } from './core/service/session.service';

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;
  let sessionService: SessionService;
  let navigateSpy: jest.SpyInstance;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent, BrowserAnimationsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        SessionService,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('$isLogged() forwards SessionService.$isLogged()', async () => {
    await expect(firstValueFrom(component.$isLogged())).resolves.toBe(false);
    sessionService.logIn({
      token: 't', type: 'Bearer', id: 1,
      username: 'a', firstName: 'Jane', lastName: 'Doe', admin: false,
    });
    await expect(firstValueFrom(component.$isLogged())).resolves.toBe(true);
  });

  it('shows Login/Register links while logged-out', () => {
    const links = fixture.debugElement.queryAll(By.css('a.link')).map(d => d.nativeElement.textContent.trim());
    expect(links).toEqual(expect.arrayContaining(['Login', 'Register']));
  });

  it('shows Sessions/Account/Logout once the user logs in', () => {
    sessionService.logIn({
      token: 't', type: 'Bearer', id: 1,
      username: 'a', firstName: 'Jane', lastName: 'Doe', admin: false,
    });
    fixture.detectChanges();
    const links = fixture.debugElement.queryAll(By.css('span.link')).map(d => d.nativeElement.textContent.trim());
    expect(links).toEqual(['Sessions', 'Account', 'Logout']);
  });

  it('logout() calls SessionService.logOut() and navigates home', () => {
    const logOutSpy = jest.spyOn(sessionService, 'logOut');
    component.logout();
    expect(logOutSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  });

  it('clicking the Logout link triggers logout()', () => {
    sessionService.logIn({
      token: 't', type: 'Bearer', id: 1,
      username: 'a', firstName: 'Jane', lastName: 'Doe', admin: false,
    });
    fixture.detectChanges();
    const logoutEl = fixture.debugElement.queryAll(By.css('span.link'))
      .find(d => d.nativeElement.textContent.trim() === 'Logout');
    expect(logoutEl).toBeDefined();
    logoutEl!.nativeElement.click();
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  });
});
