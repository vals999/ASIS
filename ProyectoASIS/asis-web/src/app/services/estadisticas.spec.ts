import { TestBed } from '@angular/core/testing';

import { Estadisticas } from './estadisticas';

describe('Estadisticas', () => {
  let service: Estadisticas;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Estadisticas);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
