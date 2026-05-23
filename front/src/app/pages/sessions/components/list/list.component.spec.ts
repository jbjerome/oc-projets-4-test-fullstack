import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { expect } from '@jest/globals';

import { ListComponent } from './list.component';
import { SessionService } from '../../../../core/service/session.service';
import { Session } from '../../../../core/models/session.interface';

const sessions: Session[] = [
  { id: 1, name: 'Hatha', description: 'Beginner', date: new Date('2026-02-01'), teacher_id: 1, users: [] },
  { id: 2, name: 'Vinyasa', description: 'Flow', date: new Date('2026-02-08'), teacher_id: 2, users: [] },
];

const setupWith = async (admin: boolean) => {
  await TestBed.configureTestingModule({
    imports: [ListComponent, BrowserAnimationsModule],
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      provideRouter([]),
      { provide: SessionService, useValue: { sessionInformation: { admin, id: 1 } } },
    ],
  }).compileComponents();

  const fixture = TestBed.createComponent(ListComponent);
  const httpMock = TestBed.inject(HttpTestingController);
  fixture.detectChanges();
  const req = httpMock.expectOne('api/session');
  req.flush(sessions);
  fixture.detectChanges();
  return { fixture, component: fixture.componentInstance };
};

describe('ListComponent', () => {
  it('should create', async () => {
    const { component } = await setupWith(true);
    expect(component).toBeTruthy();
  });

  it('renders one card per session returned by the API', async () => {
    const { fixture } = await setupWith(false);
    const items: HTMLElement[] = fixture.nativeElement.querySelectorAll('.item');
    expect(items.length).toBe(2);
    expect(items[0].textContent).toContain('Hatha');
    expect(items[1].textContent).toContain('Vinyasa');
  });

  it('shows the Create and Edit buttons when the user is admin', async () => {
    const { fixture } = await setupWith(true);
    const labels = fixture.debugElement
      .queryAll(By.css('button span'))
      .map(d => (d.nativeElement as HTMLElement).textContent?.trim());
    expect(labels).toContain('Create');
    expect(labels).toContain('Edit');
    expect(labels).toContain('Detail');
  });

  it('hides the Create and Edit buttons for non-admin users', async () => {
    const { fixture } = await setupWith(false);
    const labels = fixture.debugElement
      .queryAll(By.css('button span'))
      .map(d => (d.nativeElement as HTMLElement).textContent?.trim());
    expect(labels).not.toContain('Create');
    expect(labels).not.toContain('Edit');
    expect(labels).toContain('Detail');
  });

  it('exposes the connected user through the `user` getter', async () => {
    const { component } = await setupWith(true);
    expect(component.user).toEqual({ admin: true, id: 1 });
  });
});
