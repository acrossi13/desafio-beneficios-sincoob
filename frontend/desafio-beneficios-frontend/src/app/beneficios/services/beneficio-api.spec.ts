import { TestBed } from '@angular/core/testing';

import { BeneficioApiService } from './beneficio-api.service';

describe('BeneficioApiService', () => {
  let service: BeneficioApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BeneficioApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
