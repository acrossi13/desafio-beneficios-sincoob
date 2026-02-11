import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BeneficioApiService } from '../../services/beneficio-api';
import { Beneficio } from '../../models/beneficio.model';

@Component({
  selector: 'app-beneficio-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './beneficio-form.component.html',
  styleUrl: './beneficio-form.component.scss',
})
export class BeneficioFormComponent {
  private fb = inject(FormBuilder);
  private api = inject(BeneficioApiService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  saving = false;
  success: string | null = null;
  error: string | null = null;

  id: number | null = null;

  form = this.fb.group({
    nome: ['', [Validators.required]],
    descricao: [''],
    valor: [0, [Validators.required]],
    ativo: [true, [Validators.required]],
  });

  get isEdit(): boolean {
    return this.id !== null;
  }

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;

    if (this.id) {
      this.api.get(this.id).subscribe({
        next: (b: Beneficio) => {
          this.form.patchValue({
            nome: b.nome,
            descricao: b.descricao,
            valor: b.valor,
            ativo: b.ativo,
          });
        },
        error: () => (this.error = 'Não foi possível carregar o benefício.'),
      });
    }
  }

  cancel() {
    this.router.navigate(['/beneficios']);
  }

  submit() {
    this.success = null;
    this.error = null;

    this.saving = true;
    const payload = this.form.value as any;

    const req$ = this.isEdit
      ? this.api.update(this.id!, payload)
      : this.api.create(payload);

    req$.subscribe({
      next: () => {
        this.success = this.isEdit ? 'Benefício atualizado.' : 'Benefício criado.';
        this.saving = false;
        if (!this.isEdit) this.form.reset({ ativo: true, valor: 0 });
      },
      error: () => {
        this.error = 'Erro ao salvar.';
        this.saving = false;
      },
    });
  }
}