import { HttpClient } from "@angular/common/http";
import { Component, inject, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { ActivatedRoute, RouterLink } from "@angular/router";
@Component({
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `<main class="reset card">
    <h1>Nova senha</h1>
    @if (done()) {
      <p>Senha alterada. <a routerLink="/login">Entrar</a></p>
    } @else {
      <form [formGroup]="form" (ngSubmit)="submit()">
        <input
          class="field"
          type="password"
          placeholder="Mínimo de 10 caracteres"
          formControlName="password"
        /><button class="btn" [disabled]="form.invalid">Alterar senha</button>
      </form>
    }
  </main>`,
  styles: [
    `
      .reset {
        max-width: 440px;
        margin: 12vh auto;
        padding: 2rem;
      }
      form {
        display: grid;
        gap: 1rem;
      }
    `,
  ],
})
export class ResetPasswordComponent {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  done = signal(false);
  form = this.fb.nonNullable.group({
    password: ["", [Validators.required, Validators.minLength(10)]],
  });
  submit() {
    this.http
      .post("/api/auth/reset-password", {
        token: this.route.snapshot.queryParamMap.get("token"),
        newPassword: this.form.controls.password.value,
      })
      .subscribe(() => this.done.set(true));
  }
}
