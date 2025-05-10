import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-random-useless-fact',  
  imports: [RouterModule, CommonModule],
  templateUrl: './RandomUselessFact.component.html',
  styleUrl: './RandomUselessFact.component.css',
})
export class RandomUselessFactComponent {}
