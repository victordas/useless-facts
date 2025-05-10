import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UselessFact } from '../../models';

@Component({
  selector: 'app-useless-fact-detail',
  imports: [RouterModule, CommonModule],
  templateUrl: './UselessFactDetail.component.html',
  styleUrl: './UselessFactDetail.component.css',
})
export class UselessFactDetailComponent {
  @Input() fact!: UselessFact | null;
  @Input() error!: { message: string, status: number } | null;

  get hasError(): boolean {
    return this.error !== null;
  }
}
