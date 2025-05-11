import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RandomUselessFactComponent } from './RandomUselessFact.component';

describe('RandomUselessFactComponent', () => {
  let component: RandomUselessFactComponent;
  let fixture: ComponentFixture<RandomUselessFactComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RandomUselessFactComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RandomUselessFactComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
