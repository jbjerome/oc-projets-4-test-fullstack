import { Component, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, Observable, map, switchMap } from 'rxjs';
import { Teacher } from '../../../../core/models/teacher.interface';
import { SessionService } from '../../../../core/service/session.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { Session } from '../../../../core/models/session.interface';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { MaterialModule } from "../../../../shared/material.module";
import { CommonModule } from "@angular/common";

interface DetailVm {
  session: Session;
  teacher: Teacher;
  isParticipate: boolean;
}

@Component({
  selector: 'app-detail',
  imports: [CommonModule, MaterialModule],
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent {
  public isAdmin: boolean;
  public sessionId: string;
  public userId: string;

  private route = inject(ActivatedRoute);
  private sessionService = inject(SessionService);
  private sessionApiService = inject(SessionApiService);
  private teacherService = inject(TeacherService);
  private matSnackBar = inject(MatSnackBar);
  private router = inject(Router);

  private refresh$ = new BehaviorSubject<void>(undefined);

  public vm$: Observable<DetailVm>;

  constructor() {
    this.sessionId = this.route.snapshot.paramMap.get('id')!;
    this.isAdmin = this.sessionService.sessionInformation!.admin;
    this.userId = this.sessionService.sessionInformation!.id.toString();

    this.vm$ = this.refresh$.pipe(
      switchMap(() => this.sessionApiService.detail(this.sessionId)),
      switchMap(session => this.teacherService.detail(session.teacher_id.toString()).pipe(
        map(teacher => ({
          session,
          teacher,
          isParticipate: session.users.some(u => u === this.sessionService.sessionInformation!.id),
        })),
      )),
    );
  }

  public back(): void {
    window.history.back();
  }

  public delete(): void {
    this.sessionApiService
      .delete(this.sessionId)
      .subscribe(() => {
        this.matSnackBar.open('Session deleted !', 'Close', { duration: 3000 });
        this.router.navigate(['sessions']);
      });
  }

  public participate(): void {
    this.sessionApiService.participate(this.sessionId, this.userId)
      .subscribe(() => this.refresh$.next());
  }

  public unParticipate(): void {
    this.sessionApiService.unParticipate(this.sessionId, this.userId)
      .subscribe(() => this.refresh$.next());
  }
}
