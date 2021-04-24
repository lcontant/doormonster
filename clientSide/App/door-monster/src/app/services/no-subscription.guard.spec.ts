import { TestBed, async, inject } from '@angular/core/testing';

import { NoSubscriptionGuard } from './no-subscription.guard';

describe('NoSubscriptionGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [NoSubscriptionGuard]
    });
  });

  it('should ...', inject([NoSubscriptionGuard], (guard: NoSubscriptionGuard) => {
    expect(guard).toBeTruthy();
  }));
});
