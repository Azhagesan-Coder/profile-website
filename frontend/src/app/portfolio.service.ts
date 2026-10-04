import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { Profile } from './profile';
@Injectable({ providedIn: 'root' })
export class PortfolioService {
  private http = inject(HttpClient);
  private base = '';
  async load(): Promise<Profile> {
    try {
      const config = await firstValueFrom(this.http.get<{apiBaseUrl: string}>('config.json'));
      this.base = config.apiBaseUrl.replace(/\/$/, '');
    } catch { this.base = ''; }
    // No configured API means the portable, static profile is the primary source.
    if (this.base || location.hostname === 'localhost') {
      try { return await firstValueFrom(this.http.get<Profile>(this.base + '/api/profile', {timeout: 5000})); }
      catch { /* Keep the portfolio usable when its API is unavailable. */ }
    }
    return firstValueFrom(this.http.get<Profile>('profile.json'));
  }
  async send(payload: {name: string; email: string; message: string; website: string}) {
    if (!this.base && location.hostname !== 'localhost') {
      throw new Error('The contact service is not connected yet. Please use the email link.');
    }
    return firstValueFrom(this.http.post<{message: string}>(this.base + '/api/contact', payload, {timeout: 15000}));
  }
}
