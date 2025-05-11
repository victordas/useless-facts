import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PaginatedResponse, UselessFact, UselessStatistics } from '../models';

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

  getAllFacts(page?: number, size?: number): Observable<PaginatedResponse> {
    return this.http.get<PaginatedResponse>(`${this.api}/facts`, {
      params: {
        page: page ? page.toString() : '',
        size: size ? size.toString() : '',
      },
    });
  }

  getStatistics(
    username: string,
    password: string
  ): Observable<UselessStatistics> {
    let headers = new HttpHeaders();
    if(username && password) {
      const encoded = this.encodeBasicAuth(username, password);
      headers = new HttpHeaders({
        Authorization: `Basic ${encoded}`,
      });
    }
    return this.http.get<UselessStatistics>(`/admin/statistics`, {  headers , withCredentials: true });
  }

  private encodeBasicAuth(username: string, password: string): string {
    const str = `${username}:${password}`;
    const utf8Bytes = new TextEncoder().encode(str);
    const base64 = window.btoa(String.fromCharCode(...utf8Bytes));
    return base64;
  }
}
