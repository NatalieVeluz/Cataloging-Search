import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PinnedBooks } from './pinned-books';

describe('PinnedBooks', () => {
  let component: PinnedBooks;
  let fixture: ComponentFixture<PinnedBooks>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PinnedBooks]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PinnedBooks);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
