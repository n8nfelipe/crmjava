import { HttpClient } from "@angular/common/http";
import { Component, inject } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
@Component({
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `<section class="page">
    <div class="page-head"><h1>Relatórios</h1></div>
    <article class="card report">
      <div class="art">↗</div>
      <div>
        <h3>Vendas por período</h3>
        <p>Oportunidades, clientes, valores, vendedores e situação.</p>
      </div>
      <form [formGroup]="form" (ngSubmit)="download()">
        <input class="field" type="date" formControlName="from" /><input
          class="field"
          type="date"
          formControlName="to"
        /><button class="btn" [disabled]="form.invalid">Exportar CSV</button>
      </form>
    </article>
  </section>`,
  styles: [
    `
      .report {
        padding: 1.3rem;
        display: grid;
        grid-template-columns: auto 1fr auto;
        align-items: center;
        gap: 1rem;
      }
      .art {
        font-size: 1.5rem;
        color: var(--primary);
        background: var(--primary-soft);
        padding: 0.8rem;
        border-radius: 12px;
      }
      .report h3,
      .report p {
        margin: 0.2rem;
      }
      .report p {
        color: var(--muted);
      }
      form {
        display: flex;
        gap: 0.5rem;
      }
      @media (max-width: 850px) {
        .report {
          grid-template-columns: auto 1fr;
        }
        .report form {
          grid-column: 1/-1;
          flex-wrap: wrap;
        }
      }
    `,
  ],
})
export class ReportsComponent {
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);
  private now = new Date();
  form = this.fb.nonNullable.group({
    from: [`${this.now.getFullYear()}-01-01`, Validators.required],
    to: [this.now.toISOString().slice(0, 10), Validators.required],
  });
  download() {
    const from = this.form.controls.from.value,
      to = this.form.controls.to.value;
    this.http
      .get(`/api/reports/sales?from=${from}&to=${to}`, { responseType: "blob" })
      .subscribe((blob) => {
        const a = document.createElement("a");
        a.href = URL.createObjectURL(blob);
        a.download = `vendas-${from}-${to}.csv`;
        a.click();
        URL.revokeObjectURL(a.href);
      });
  }
}
