import { CurrencyPipe, DatePipe } from "@angular/common";
import { Component, inject, OnInit, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { ApiService } from "../../core/services/api.service";
import { ToastService } from "../../core/services/toast.service";
interface Opportunity {
  id: string;
  name: string;
  customerName?: string;
  ownerName?: string;
  value: number;
  expectedCloseDate?: string;
  priority: string;
  stageId: string;
}
interface Stage {
  id: string;
  name: string;
  position: number;
  closingProbability: number;
  opportunities: Opportunity[];
}
interface Pipeline {
  id: string;
  name: string;
  stages: Stage[];
}
@Component({
  standalone: true,
  imports: [CurrencyPipe, DatePipe, ReactiveFormsModule],
  template: `<section class="page pipeline">
    <div class="page-head">
      <div>
        <h1>Pipeline</h1>
        <small>{{ data()?.name }}</small>
      </div>
      <button class="btn" (click)="open()">+ Oportunidade</button>
    </div>
    @if (loading()) {
      <div class="board">
        <div class="stage card"><div class="skeleton"></div></div>
        <div class="stage card"><div class="skeleton"></div></div>
      </div>
    } @else {
      <div class="board">
        @for (s of data()?.stages; track s.id) {
          <section
            class="stage"
            (dragover)="$event.preventDefault()"
            (drop)="drop(s)"
          >
            <header>
              <b>{{ s.name }}</b
              ><span>{{ s.opportunities.length }}</span>
            </header>
            <div class="sum">
              {{ total(s) | currency: "BRL" : "symbol" : "1.0-0" }} ·
              {{ s.closingProbability }}%
            </div>
            @for (o of s.opportunities; track o.id) {
              <article
                class="deal card"
                draggable="true"
                (dragstart)="drag.set(o)"
              >
                <div class="priority" [class]="o.priority.toLowerCase()"></div>
                <b>{{ o.name }}</b>
                <p>{{ o.customerName || "Cliente não informado" }}</p>
                <strong>{{ o.value | currency: "BRL" }}</strong>
                <footer>
                  <span
                    >◷
                    {{
                      o.expectedCloseDate
                        ? (o.expectedCloseDate | date: "dd/MM")
                        : "—"
                    }}</span
                  ><span>{{ o.ownerName || "Sem responsável" }}</span>
                </footer>
              </article>
            } @empty {
              <div class="dropzone">Solte uma oportunidade aqui</div>
            }
          </section>
        }
      </div>
    }
  </section>
  @if (show()) {
    <div class="modal-backdrop" (click)="show.set(false)"><form class="modal" [formGroup]="form" (ngSubmit)="create()" (click)="$event.stopPropagation()">
      <h2>Nova oportunidade</h2><div class="form-grid">
        <label class="span-2">Nome<input class="field" formControlName="name" /></label>
        <label>Valor<input class="field" type="number" min="0" step="0.01" formControlName="value" /></label>
        <label>Etapa<select class="field" formControlName="stageId">@for (stage of data()?.stages; track stage.id) {<option [value]="stage.id">{{ stage.name }}</option>}</select></label>
        <label>Prioridade<select class="field" formControlName="priority"><option value="LOW">Baixa</option><option value="MEDIUM">Média</option><option value="HIGH">Alta</option></select></label>
        <label>Fechamento previsto<input class="field" type="date" formControlName="expectedCloseDate" /></label>
      </div><div class="actions"><button type="button" class="btn secondary" (click)="show.set(false)">Cancelar</button><button class="btn" [disabled]="form.invalid">Criar</button></div>
    </form></div>
  }`,
  styles: [
    `
      .pipeline {
        max-width: none;
      }
      .page-head small {
        color: var(--muted);
      }
      .board {
        display: flex;
        gap: 1rem;
        overflow-x: auto;
        padding-bottom: 1rem;
        min-height: 65vh;
      }
      .stage {
        background: color-mix(in srgb, var(--surface) 55%, var(--bg));
        border: 1px solid var(--border);
        border-radius: 14px;
        padding: 0.8rem;
        min-width: 285px;
        width: 285px;
      }
      .stage > header {
        display: flex;
        justify-content: space-between;
      }
      .stage > header span {
        background: var(--border);
        padding: 0.15rem 0.45rem;
        border-radius: 50px;
        font-size: 0.75rem;
      }
      .sum {
        font-size: 0.77rem;
        color: var(--muted);
        margin: 0.5rem 0 1rem;
      }
      .deal {
        padding: 1rem;
        margin: 0.7rem 0;
        cursor: grab;
        position: relative;
      }
      .deal:active {
        cursor: grabbing;
      }
      .deal p {
        font-size: 0.8rem;
        color: var(--muted);
        margin: 0.3rem 0 0.8rem;
      }
      .deal strong {
        font-size: 0.95rem;
      }
      .deal footer {
        display: flex;
        justify-content: space-between;
        border-top: 1px solid var(--border);
        margin-top: 0.8rem;
        padding-top: 0.7rem;
        color: var(--muted);
        font-size: 0.72rem;
      }
      .priority {
        position: absolute;
        left: 0;
        top: 12px;
        width: 4px;
        height: 24px;
        background: #789;
      }
      .priority.high {
        background: #e44;
      }
      .priority.low {
        background: #18a875;
      }
      .dropzone {
        border: 1px dashed var(--border);
        border-radius: 10px;
        padding: 1.5rem 0.5rem;
        text-align: center;
        color: var(--muted);
        font-size: 0.8rem;
      }
    `,
  ],
})
export class PipelineComponent implements OnInit {
  private api = inject(ApiService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  data = signal<Pipeline | null>(null);
  loading = signal(true);
  drag = signal<Opportunity | null>(null);
  show = signal(false);
  form = this.fb.nonNullable.group({ name: ["", Validators.required], value: [0, [Validators.required, Validators.min(0)]], stageId: ["", Validators.required], priority: ["MEDIUM"], expectedCloseDate: [""] });
  ngOnInit() {
    this.load();
  }
  load() {
    this.api
      .get<Pipeline>("/api/pipeline")
      .subscribe({
        next: (x) => this.data.set(x),
        complete: () => this.loading.set(false),
      });
  }
  total(s: Stage) {
    return s.opportunities.reduce((a, x) => a + x.value, 0);
  }
  open() {
    this.form.reset({ value: 0, priority: "MEDIUM", stageId: this.data()?.stages[0]?.id || "" });
    this.show.set(true);
  }
  create() {
    const raw = this.form.getRawValue();
    this.api.post<Opportunity>("/api/opportunities", { ...raw, customerId: null, leadId: null, ownerId: null, probability: null, expectedCloseDate: raw.expectedCloseDate || null }).subscribe(() => { this.toast.show("Oportunidade criada"); this.show.set(false); this.load(); });
  }
  drop(stage: Stage) {
    const deal = this.drag();
    if (!deal || deal.stageId === stage.id) return;
    this.api
      .patch<Opportunity>(`/api/opportunities/${deal.id}/stage`, {
        stageId: stage.id,
      })
      .subscribe(() => {
        this.toast.show(`Movido para ${stage.name}`);
        this.load();
      });
  }
}
