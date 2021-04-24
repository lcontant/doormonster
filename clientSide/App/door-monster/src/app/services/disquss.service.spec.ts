import { TestBed, inject } from '@angular/core/testing';

import { DoorMonsterDisqusService } from './door-monster-disqus.service';

describe('DoorMonsterDisqusService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DoorMonsterDisqusService]
    });
  });

  it('should be created', inject([DoorMonsterDisqusService], (service: DoorMonsterDisqusService) => {
    expect(service).toBeTruthy();
  }));
});
