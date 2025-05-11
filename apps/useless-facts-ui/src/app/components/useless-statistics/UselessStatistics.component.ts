import { Component, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UselessFact, UselessStatistics } from '../../models';
import { UselessFactService } from '../../services/useless-fact.service';
import { UselessFactDetailComponent } from '../useless-fact-detail/UselessFactDetail.component';

@Component({
  selector: 'app-useless-statistics',
  imports: [
    RouterModule,
    CommonModule,
    UselessFactDetailComponent,
    FormsModule,
  ],
  templateUrl: './UselessStatistics.component.html',
  styleUrl: './UselessStatistics.component.css',
})
export class UselessStatisticsComponent implements OnInit {
  stats: UselessStatistics | null = null;
  shortenedUrls: string[] = [];
  loadingFact = false;
  error: { message: string; status: number } | null = null;
  selectedFact: UselessFact | null = null;
  requireLogin = false;
  username = '';
  password = '';

  constructor(
    private uselessFactService: UselessFactService,
    private location: Location
  ) {}

  ngOnInit() {
    this.requireLogin = true;
  }

  loadStatistics() {
    this.uselessFactService
      .getStatistics(this.username, this.password)
      .subscribe({
        next: (data) => {
          this.stats = data;
          this.shortenedUrls = Object.keys(data.factsAccessCount || {});
          this.requireLogin = false;
        },
        error: (err) => {
          this.error = {
            message: err.error?.message || 'An unexpected error occurred.',
            status: err.status || 500,
          };
          this.closeLogin();
        },
      });
  }

  openModal(shortenedUrl: string) {
    this.loadingFact = true;
    this.error = null;

    this.uselessFactService.getFactByShortUrl(shortenedUrl).subscribe({
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

  closeLogin() {
    this.location.back();
  }
}
