import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AlluselessFactsComponentComponent } from './AlluselessFactsComponent.component';

describe('AlluselessFactsComponentComponent', () => {
  let component: AlluselessFactsComponentComponent;
  let fixture: ComponentFixture<AlluselessFactsComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlluselessFactsComponentComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AlluselessFactsComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
