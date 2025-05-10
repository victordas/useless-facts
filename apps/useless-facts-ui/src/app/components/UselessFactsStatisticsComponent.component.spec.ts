import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UselessFactsStatisticsComponentComponent } from './UselessFactsStatisticsComponent.component';

describe('UselessFactsStatisticsComponentComponent', () => {
  let component: UselessFactsStatisticsComponentComponent;
  let fixture: ComponentFixture<UselessFactsStatisticsComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UselessFactsStatisticsComponentComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UselessFactsStatisticsComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
