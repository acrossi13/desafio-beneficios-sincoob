import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

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

  saving = false;

  form = this.fb.group({
    fromId: [null as number | null],
    toId: [null as number | null],
    valor: [null as number | null],
  });

  cancel() {
    this.router.navigate(['/beneficios']);
  }

  submit() {
    const payload: TransferRequest = {
      fromId: Number(this.form.value.fromId),
      toId: Number(this.form.value.toId),
      valor: Number(this.form.value.valor),
    };

    this.saving = true;
    this.api
      .transfer(payload)
      .pipe(finalize(() => (this.saving = false)))
      .subscribe(() => {

      });
  }
}