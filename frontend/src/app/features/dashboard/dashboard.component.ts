import { CurrencyPipe, DecimalPipe } from "@angular/common";
import { Component, inject, OnInit, signal } from "@angular/core";
import { RouterLink } from "@angular/router";
import { ApiService } from "../../core/services/api.service";
interface Dashboard {
  totalCustomers: number;
  newLeads: number;
  openOpportunities: number;
  wonSales: number;
  forecastRevenue: number;
  conversionRate: number;
  overdueActivities: number;
  performance: { period: string; revenue: number; won: number }[];
}
@Component({
  standalone: true,
  imports: [CurrencyPipe, DecimalPipe, RouterLink],
  template: `<section class="page">
    <div class="page-head">
      <div>
        <h1>Visão geral</h1>
        <small>Acompanhe a saúde da sua operação comercial</small>
      </div>
      <a class="btn" href="/pipeline">Abrir pipeline</a>
    </div>
    @if (loading()) {
      <div class="metrics">
        @for (x of [1, 2, 3, 4, 5, 6]; track x) {
          <div class="card metric">
            <div class="skeleton"></div>
            <div class="skeleton wide"></div>
          </div>
        }
      </div>
    } @else if (data(); as d) {
      <div class="metrics">
        <article class="card metric">
          <span>Clientes</span><b>{{ d.totalCustomers }}</b
          ><i>◉</i>
        </article>
        <article class="card metric">
          <span>Novos leads</span><b>{{ d.newLeads }}</b
          ><i>◎</i>
        </article>
        <article class="card metric">
          <span>Em andamento</span><b>{{ d.openOpportunities }}</b
          ><i>▥</i>
        </article>
        <article class="card metric">
          <span>Vendas fechadas</span><b>{{ d.wonSales }}</b
          ><i>✓</i>
        </article>
        <article class="card metric">
          <span>Receita prevista</span
          ><b>{{ d.forecastRevenue | currency: "BRL" : "symbol" : "1.0-0" }}</b
          ><i>↗</i>
        </article>
        <article class="card metric">
          <span>Conversão</span><b>{{ d.conversionRate | number: "1.1-1" }}%</b
          ><i>◒</i>
        </article>
      </div>
      <div class="lower">
        <article class="card chart">
          <h3>Desempenho comercial</h3>
          <div class="bars">
            @for (p of d.performance; track p.period) {
              <div class="bar-wrap">
                <span class="bar" [style.height.%]="height(p.revenue)"></span
                ><small>{{ p.period.slice(5) }}</small>
              </div>
            } @empty {
              <div class="empty">As vendas ganhas aparecerão aqui</div>
            }
          </div>
        </article>
        <article class="card attention">
          <h3>Precisa de atenção</h3>
          <b>{{ d.overdueActivities }}</b>
          <p>atividades atrasadas</p>
          <a routerLink="/activities">Ver atividades →</a>
        </article>
      </div>
    }
  </section>`,
  styles: [
    `
      .page-head small {
        color: var(--muted);
      }
      .metrics {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 1rem;
      }
      .metric {
        position: relative;
        padding: 1.2rem;
        min-height: 125px;
      }
      .metric span {
        color: var(--muted);
        font-size: 0.85rem;
      }
      .metric b {
        display: block;
        font-size: 1.7rem;
        margin-top: 0.65rem;
      }
      .metric i {
        position: absolute;
        right: 1rem;
        top: 1rem;
        background: var(--primary-soft);
        color: var(--primary);
        padding: 0.55rem;
        border-radius: 10px;
        font-style: normal;
      }
      .metric .wide {
        width: 55%;
        margin-top: 22px;
        height: 28px;
      }
      .lower {
        display: grid;
        grid-template-columns: 2fr 1fr;
        gap: 1rem;
        margin-top: 1rem;
      }
      .chart,
      .attention {
        padding: 1.2rem;
      }
      .bars {
        height: 230px;
        display: flex;
        gap: 1rem;
        align-items: flex-end;
        padding-top: 1rem;
      }
      .bar-wrap {
        height: 100%;
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: flex-end;
        align-items: center;
        gap: 0.5rem;
      }
      .bar {
        width: min(36px, 80%);
        min-height: 3px;
        background: linear-gradient(var(--primary), #a69fff);
        border-radius: 6px 6px 2px 2px;
      }
      .bar-wrap small {
        color: var(--muted);
      }
      .attention b {
        display: block;
        font-size: 3rem;
        color: var(--danger);
        margin-top: 2.3rem;
      }
      .attention p {
        color: var(--muted);
      }
      .attention a {
        color: var(--primary);
      }
      @media (max-width: 950px) {
        .metrics {
          grid-template-columns: repeat(2, 1fr);
        }
      }
      @media (max-width: 650px) {
        .metrics,
        .lower {
          grid-template-columns: 1fr;
        }
        .page-head .btn {
          display: none;
        }
      }
    `,
  ],
})
export class DashboardComponent implements OnInit {
  private api = inject(ApiService);
  data = signal<Dashboard | null>(null);
  loading = signal(true);
  ngOnInit() {
    this.api
      .get<Dashboard>("/api/dashboard")
      .subscribe({
        next: (x) => this.data.set(x),
        complete: () => this.loading.set(false),
      });
  }
  height(v: number) {
    const max = Math.max(
      ...(this.data()?.performance.map((x) => x.revenue) || [1]),
    );
    return Math.max(3, (v / max) * 100);
  }
}
