import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Beneficio } from '../../models/beneficio.model';
import { Observable } from 'rxjs';
import { BeneficioApiService } from '../../services/beneficio-api';

@Component({
  selector: 'app-beneficio-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './beneficio-list.component.html',
  styleUrl: './beneficio-list.component.scss',
})
export class BeneficioListComponent {
  private api = inject(BeneficioApiService);
  beneficios$: Observable<Beneficio[]> = this.api.list();
}