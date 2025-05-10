import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AllUselessFactsComponent } from './AllUselessFacts.component';

describe('AllUselessFactsComponent', () => {
  let component: AllUselessFactsComponent;
  let fixture: ComponentFixture<AllUselessFactsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllUselessFactsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AllUselessFactsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
