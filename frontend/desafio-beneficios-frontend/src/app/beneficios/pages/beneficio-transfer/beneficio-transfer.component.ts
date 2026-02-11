import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { BeneficioApiService } from '../../services/beneficio-api';
import { TransferRequest } from '../../models/beneficio.model';

@Component({
  selector: 'app-beneficio-transfer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './beneficio-transfer.component.html',
  styleUrl: './beneficio-transfer.component.scss',
})
export class BeneficioTransferComponent {
  private fb = inject(FormBuilder);
  private api = inject(BeneficioApiService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  saving = false;
  error: string | null = null;
  success: string | null = null;


  form = this.fb.group({
    fromId: [null as number | null, [Validators.required, Validators.min(0)]],
    toId: [null as number | null, [Validators.required, Validators.min(0)]],
    valor: [null as number | null, [Validators.required, Validators.min(0)]],
  });

  cancel() {
    this.router.navigate(['/beneficios']);
  }

  private toNullIfZero(v: unknown): number | null {
    const n = Number(v);
    if (!Number.isFinite(n)) return null;
    return n === 0 ? null : n;
  }

  submit() {
    this.error = null;
    this.success = null;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.cdr.detectChanges();
      return;
    }

    const payload: TransferRequest = {
      fromId: this.toNullIfZero(this.form.value.fromId),
      toId: this.toNullIfZero(this.form.value.toId),
      valor: Number(this.form.value.valor),
    };

    this.saving = true;
    this.cdr.detectChanges();

    this.api
      .transfer(payload)
      .pipe(
        finalize(() => {
          this.saving = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.success = 'Transferência realizada com sucesso.';
          this.form.reset();
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.error = err?.error?.message || err?.message || 'erro inesperado';
          this.cdr.detectChanges();
        },
      });
  }
}