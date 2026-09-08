import { Component, inject, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { AuthService } from "../../core/auth/auth.service";
@Component({
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `<main class="auth">
    <section>
      <div class="logo">N</div>
      <h1>Bem-vindo de volta</h1>
      <p>Entre para continuar no Nexus CRM</p>
      <form [formGroup]="form" (ngSubmit)="submit()">
        <label
          >E-mail<input
            class="field"
            type="email"
            formControlName="email"
            autocomplete="username" /></label
        ><label
          >Senha<input
            class="field"
            type="password"
            formControlName="password"
            autocomplete="current-password" /></label
        ><button class="btn" [disabled]="form.invalid || loading()">
          {{ loading() ? "Entrando..." : "Entrar" }}
        </button>
      </form>
    </section>
  </main>`,
  styles: [
    `
      .auth {
        min-height: 100vh;
        display: grid;
        place-items: center;
        padding: 1rem;
        background:
          radial-gradient(circle at 15% 20%, #e7e3ff, transparent 30%),
          var(--bg);
      }
      section {
        width: min(420px, 100%);
        padding: 2.2rem;
        background: var(--surface);
        border: 1px solid var(--border);
        border-radius: 22px;
        box-shadow: var(--shadow);
      }
      .logo {
        width: 50px;
        height: 50px;
        display: grid;
        place-items: center;
        border-radius: 14px;
        background: var(--primary);
        color: white;
        font-size: 1.5rem;
        font-weight: 700;
      }
      h1 {
        margin: 1.4rem 0 0.3rem;
      }
      p {
        color: var(--muted);
        margin: 0 0 1.7rem;
      }
      form,
      label {
        display: grid;
        gap: 0.55rem;
      }
      form {
        gap: 1.15rem;
      }
      label {
        font-size: 0.88rem;
        color: var(--muted);
      }
      button {
        margin-top: 0.4rem;
      }
    `,
  ],
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  loading = signal(false);
  form = this.fb.nonNullable.group({
    email: ["", [Validators.required, Validators.email]],
    password: ["", Validators.required],
  });
  submit() {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.auth
      .login(this.form.controls.email.value, this.form.controls.password.value)
      .subscribe({
        next: () => void this.router.navigateByUrl("/dashboard"),
        error: () => this.loading.set(false),
      });
  }
}
