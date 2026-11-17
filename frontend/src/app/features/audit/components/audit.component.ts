import { Component, computed, inject, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import {
  AuditResponse,
  AuditStatusResponse,
  FindingResponse,
} from "../models/audit.model";
import { AuditService } from "../services/audit.service";
import { finalize, switchMap } from "rxjs";
import { MatTableDataSource, MatTableModule } from "@angular/material/table";
import { MatChipsModule } from "@angular/material/chips";

@Component({
  selector: "app-audit",
  standalone: true,
  imports: [ReactiveFormsModule, MatTableModule, MatChipsModule],
  templateUrl: "audit.html",
  styleUrl: "./audit.css"
})
export class AuditComponent {
  private readonly fb = inject(FormBuilder);
  private readonly audit = inject(AuditService);

  readonly loading = signal(false);
  readonly fieldErrors = signal<string[]>([]);

  readonly auditResult = signal<AuditStatusResponse | null>(null);
  readonly auditFindings = computed<FindingResponse[]>(
    () => this.auditResult()?.auditFindings ?? [],
  );

  auditForm = this.fb.nonNullable.group({
    url: ["", [Validators.required, Validators.maxLength(200)]],
  });

  displayedColumns: string[] = [
    "severity",
    "title",
    "lcpSavingsMs",
    "effortMinutes",
  ];
 

  scoreColor(score?: number): string {
    if (score === undefined) return "white";
    if (score >= 90) return "green";
    if (score >= 50) return "orange";
    return "red";
  }

  formatGain(ms: number): string {
    if (ms <= 0) return "—";
    return (ms / 1000).toFixed(1).replace(".", ",") + " s";
  }

  formatEffort(minutes: number): string {
    if (minutes < 60) return `${minutes} min`;
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return m === 0 ? `${h} h` : `${h} h ${m}`;
  }

  submit(): void {
    if (this.auditForm.invalid) {
      this.auditForm.markAllAsTouched();
      this.fieldErrors.set(["Please correct the highlighted fields."]);
      return;
    }

    this.loading.set(true);
    this.fieldErrors.set([]);
    this.auditResult.set(null);

    const { url } = this.auditForm.getRawValue();

    this.audit
      .submitAudit(url)
      .pipe(
        switchMap((res) => this.audit.pollAudit(res.jobId)),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (response) => this.auditResult.set(response),

        error: (err) => console.error(err),
      });
  }
}
