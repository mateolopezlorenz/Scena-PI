import { TestBed } from '@angular/core/testing';

import { UserEventService } from './userEventService';

describe('UserEventService', () => {
  let service: UserEventService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserEventService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
