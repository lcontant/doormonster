import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SecretUploadComponent } from './secret-upload.component';

describe('SecretUploadComponent', () => {
  let component: SecretUploadComponent;
  let fixture: ComponentFixture<SecretUploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SecretUploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SecretUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
