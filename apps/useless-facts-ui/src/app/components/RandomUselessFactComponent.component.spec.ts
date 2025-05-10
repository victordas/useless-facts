import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RandomUselessFactComponentComponent } from './RandomUselessFactComponent.component';

describe('RandomUselessFactComponentComponent', () => {
  let component: RandomUselessFactComponentComponent;
  let fixture: ComponentFixture<RandomUselessFactComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RandomUselessFactComponentComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RandomUselessFactComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
