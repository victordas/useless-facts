import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UselessStatisticsComponent } from './UselessStatistics.component';

describe('UselessStatisticsComponent', () => {
  let component: UselessStatisticsComponent;
  let fixture: ComponentFixture<UselessStatisticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UselessStatisticsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UselessStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
