import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountPasswordResetComponent } from './account-password-reset.component';

describe('AccountPasswordResetComponent', () => {
  let component: AccountPasswordResetComponent;
  let fixture: ComponentFixture<AccountPasswordResetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccountPasswordResetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountPasswordResetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
