import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UselessFactsDetailComponentComponent } from './UselessFactsDetailComponent.component';

describe('UselessFactsDetailComponentComponent', () => {
  let component: UselessFactsDetailComponentComponent;
  let fixture: ComponentFixture<UselessFactsDetailComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UselessFactsDetailComponentComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UselessFactsDetailComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
