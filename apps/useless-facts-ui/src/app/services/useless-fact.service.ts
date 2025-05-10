import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UselessFact } from '../models';

@Injectable({ providedIn: 'root' })
export class UselessFactService {
  private api = 'http://localhost:4400';

  constructor(private http: HttpClient) {}

  getRandomFact(): Observable<UselessFact> {
    return this.http.post<UselessFact>(`${this.api}/facts`, {});
  }

  getFactByShortUrl(shortUrl: string): Observable<UselessFact> {
    return this.http.get<UselessFact>(`${this.api}/facts/${shortUrl}`);
  }

  getAllFacts(): Observable<UselessFact[]> {
    return this.http.get<UselessFact[]>(`${this.api}/facts`);
  }

  getStatistics(): Observable<UselessFact[]> {
    return this.http.get<UselessFact[]>(`/admin/statistics`);
  }
}
