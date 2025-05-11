import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UselessFact } from '../../models';
import { UselessFactService } from '../../services/useless-fact.service';
import { UselessFactDetailComponent } from '../useless-fact-detail/UselessFactDetail.component';

@Component({
  selector: 'app-all-useless-facts',
  imports: [RouterModule, CommonModule, UselessFactDetailComponent],
  templateUrl: './AllUselessFacts.component.html',
  styleUrl: './AllUselessFacts.component.css',
})
export class AllUselessFactsComponent implements OnInit {
  facts: UselessFact[] = [];
  currentPage = 1;
  totalPages = 1;
  selectedFact: UselessFact | null = null;
  error: { message: string; status: number } | null = null;
  size = 6;
  loadingFact = false;

  constructor(private uselessFactService: UselessFactService) {}

  ngOnInit() {
    this.loadFacts(this.currentPage, this.size);
  }

  loadFacts(page: number, size: number) {
    this.uselessFactService.getAllFacts(page, size).subscribe((response) => {
      this.facts = response.items;
      this.totalPages = response.totalPages;
    });
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.loadFacts(this.currentPage, this.size);
    }
  }

  openModal(fact: UselessFact) {
    this.loadingFact = true;
    this.uselessFactService.getFactByShortUrl(fact.shortenedUrl).subscribe({
      next: (fullFact) => {
        this.selectedFact = fullFact;
        this.loadingFact = false;
      },
      error: (err) => {
        this.error = {
          message: err.error?.message || 'An unexpected error occurred.',
          status: err.status || 500,
        };
        this.loadingFact = false;
      },
    });
  }

  closeModal() {
    this.selectedFact = null;
  }
}
