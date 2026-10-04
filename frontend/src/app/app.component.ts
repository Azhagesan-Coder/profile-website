import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { PortfolioService } from './portfolio.service';
import { Profile } from './profile';
@Component({selector: 'app-root', standalone: true, imports: [FormsModule], templateUrl: './app.component.html'})
export class AppComponent {
  private service = inject(PortfolioService);
  profile = signal<Profile | null>(null);
  loadError = signal(false);
  active = signal('All');
  groups = computed(() => this.profile()?.skills.filter(s => this.active() === 'All' || s.category === this.active()) ?? []);
  light = signal(false);
  busy = signal(false);
  feedback = signal('');
  success = signal(false);
  form = {name: '', email: '', message: '', website: ''};
  constructor() {
    try { this.light.set(localStorage.getItem('portfolio-theme') === 'light'); } catch {}
    this.applyTheme();
    this.load();
  }
  async load() {
    this.loadError.set(false);
    try { this.profile.set(await this.service.load()); }
    catch { this.loadError.set(true); }
  }
  toggleTheme() {
    this.light.update(v => !v); this.applyTheme();
    try { localStorage.setItem('portfolio-theme', this.light() ? 'light' : 'dark'); } catch {}
  }
  private applyTheme() { document.documentElement.dataset['theme'] = this.light() ? 'light' : 'dark'; }
  async submit(f: NgForm) {
    if (f.invalid || this.busy()) { f.control.markAllAsTouched(); return; }
    this.busy.set(true); this.feedback.set(''); this.success.set(false);
    try {
      const result = await this.service.send(this.form);
      this.feedback.set(result.message); this.success.set(true);
      f.resetForm({name: '', email: '', message: '', website: ''});
    } catch (e) {
      this.feedback.set(e instanceof HttpErrorResponse
        ? (e.error?.message ?? 'Message could not be sent. Please try again or use the email link.')
        : e instanceof Error ? e.message : 'Message could not be sent.');
    } finally { this.busy.set(false); }
  }
}
