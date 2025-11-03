import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ChallengeListDTO } from '../models/challenge.model';

@Injectable({
  providedIn: 'root'
})
export class ChallengeService {
  private apiUrl = 'http://localhost:8080/api/challenge';

  constructor(private _httpClient: HttpClient) { }

  getAllChallenge(): Observable<ChallengeListDTO[]> {
    return this._httpClient.get<ChallengeListDTO[]>(`${this.apiUrl}/allChallenge`);
  }
}
