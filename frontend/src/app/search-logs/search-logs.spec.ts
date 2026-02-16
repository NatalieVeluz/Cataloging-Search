import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SearchLogsComponent } from './search-logs';

describe('SearchLogsComponent', () => {
  let component: SearchLogsComponent;
  let fixture: ComponentFixture<SearchLogsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchLogsComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(SearchLogsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
