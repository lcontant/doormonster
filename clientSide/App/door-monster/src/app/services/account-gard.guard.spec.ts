import { TestBed, async, inject } from '@angular/core/testing';

import { AccountGard } from './account-gard.service';

describe('AccountGard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AccountGard]
    });
  });

  it('should ...', inject([AccountGard], (guard: AccountGard) => {
    expect(guard).toBeTruthy();
  }));
});
