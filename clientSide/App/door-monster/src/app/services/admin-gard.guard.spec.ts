import { TestBed, async, inject } from '@angular/core/testing';

import { AdminGard } from './admin-gard.service';

describe('AdminGard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AdminGard]
    });
  });

  it('should ...', inject([AdminGard], (guard: AdminGard) => {
    expect(guard).toBeTruthy();
  }));
});
