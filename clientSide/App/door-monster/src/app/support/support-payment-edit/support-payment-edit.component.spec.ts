import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SupportPaymentEditComponent } from './support-payment-edit.component';

describe('SupportPaymentEditComponent', () => {
  let component: SupportPaymentEditComponent;
  let fixture: ComponentFixture<SupportPaymentEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SupportPaymentEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SupportPaymentEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
