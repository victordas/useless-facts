import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UselessFactDetailComponent } from './UselessFactDetail.component';

describe('UselessFactDetailComponent', () => {
  let component: UselessFactDetailComponent;
  let fixture: ComponentFixture<UselessFactDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UselessFactDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UselessFactDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
