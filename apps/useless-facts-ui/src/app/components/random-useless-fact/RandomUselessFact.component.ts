import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UselessFact } from '../../models';
import { UselessFactService } from '../../services/useless-fact.service';
import { UselessFactDetailComponent } from '../useless-fact-detail/UselessFactDetail.component';

@Component({
  selector: 'app-random-useless-fact',
  imports: [RouterModule, CommonModule, UselessFactDetailComponent],
  templateUrl: './RandomUselessFact.component.html',
  styleUrl: './RandomUselessFact.component.css',
})
export class RandomUselessFactComponent {
  fact: UselessFact | null = null;
  error: { message: string; status: number } | null = null;

  constructor(private uselessFactService: UselessFactService) {}

  fetchRandomFact() {
    this.uselessFactService.getRandomFact().subscribe({
      next: (data) => {
        this.fact = data;
        this.error = null;
      },
      error: (err) => {
        this.fact = null;
        this.error = {
          message: err.error?.message || 'An unexpected error occurred.',
          status: err.status || 500,
        };
      },
    });
  }
}
