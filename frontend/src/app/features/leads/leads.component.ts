import { Component, inject, OnInit, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { ApiService } from "../../core/services/api.service";
import { ToastService } from "../../core/services/toast.service";
interface Lead {
  id: string;
  name: string;
  email?: string;
  phone?: string;
  source?: string;
  temperature: "COLD" | "WARM" | "HOT";
  status: string;
  ownerName?: string;
}
@Component({
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `<section class="page">
      <div class="page-head">
        <h1>Leads</h1>
        <button class="btn" (click)="show.set(true)">+ Novo lead</button>
      </div>
      <div class="card toolbar">
        <input
          class="field"
          placeholder="Buscar lead"
          (input)="search = $any($event.target).value; load()"
        /><select
          class="field narrow"
          (change)="status = $any($event.target).value; load()"
        >
          <option value="">Todos</option>
          <option>NEW</option>
          <option>QUALIFIED</option>
          <option>CONVERTED</option>
        </select>
      </div>
      <div class="cards">
        @for (l of rows(); track l.id) {
          <article class="card lead">
            <div class="temp" [class]="l.temperature.toLowerCase()"></div>
            <div>
              <b>{{ l.name }}</b>
              <p>{{ l.email || l.phone || "Sem contato" }}</p>
            </div>
            <span class="badge">{{ l.status }}</span
            ><select
              class="field"
              [value]="l.status"
              (change)="change(l, $any($event.target).value)"
            >
              <option>NEW</option>
              <option>CONTACTED</option>
              <option>QUALIFIED</option>
              <option>DISQUALIFIED</option>
            </select>
          </article>
        } @empty {
          <div class="card empty">Nenhum lead encontrado.</div>
        }
      </div>
    </section>
    @if (show()) {
      <div class="modal-backdrop" (click)="show.set(false)">
        <form
          class="modal"
          [formGroup]="form"
          (ngSubmit)="save()"
          (click)="$event.stopPropagation()"
        >
          <h2>Novo lead</h2>
          <div class="form-grid">
            <label>Nome<input class="field" formControlName="name" /></label
            ><label
              >E-mail<input
                class="field"
                type="email"
                formControlName="email" /></label
            ><label
              >Telefone<input class="field" formControlName="phone" /></label
            ><label
              >Origem<input class="field" formControlName="source" /></label
            ><label
              >Temperatura<select class="field" formControlName="temperature">
                <option value="COLD">Frio</option>
                <option value="WARM">Morno</option>
                <option value="HOT">Quente</option>
              </select></label
            ><label
              >Status<input class="field" formControlName="status"
            /></label>
          </div>
          <div class="actions">
            <button
              type="button"
              class="btn secondary"
              (click)="show.set(false)"
            >
              Cancelar</button
            ><button class="btn" [disabled]="form.invalid">Salvar</button>
          </div>
        </form>
      </div>
    }`,
  styles: [
    `
      .narrow {
        max-width: 190px;
      }
      .cards {
        display: grid;
        gap: 0.7rem;
      }
      .lead {
        display: grid;
        grid-template-columns: 5px 1fr auto 160px;
        gap: 1rem;
        align-items: center;
        padding: 1rem;
      }
      .lead p {
        margin: 0.25rem 0;
        color: var(--muted);
        font-size: 0.85rem;
      }
      .temp {
        height: 45px;
        border-radius: 9px;
        background: #8090a8;
      }
      .temp.warm {
        background: #f2a83b;
      }
      .temp.hot {
        background: #ea526a;
      }
      @media (max-width: 650px) {
        .lead {
          grid-template-columns: 5px 1fr;
        }
        .lead > .badge,
        .lead > select {
          grid-column: 2;
        }
      }
    `,
  ],
})
export class LeadsComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);
  private toast = inject(ToastService);
  rows = signal<Lead[]>([]);
  show = signal(false);
  search = "";
  status = "";
  form = this.fb.nonNullable.group({
    name: ["", Validators.required],
    email: ["", Validators.email],
    phone: [""],
    source: [""],
    temperature: ["WARM", Validators.required],
    status: ["NEW", Validators.required],
    ownerId: [null],
  });
  ngOnInit() {
    this.load();
  }
  load() {
    this.api
      .page<Lead>("/api/leads", {
        search: this.search,
        status: this.status,
        size: 50,
      })
      .subscribe((x) => this.rows.set(x.content));
  }
  save() {
    this.api.post<Lead>("/api/leads", this.form.getRawValue()).subscribe(() => {
      this.toast.show("Lead criado");
      this.show.set(false);
      this.form.reset({ temperature: "WARM", status: "NEW" });
      this.load();
    });
  }
  change(l: Lead, status: string) {
    this.api
      .patch<Lead>(`/api/leads/${l.id}/status`, { status })
      .subscribe((x) => {
        Object.assign(l, x);
        this.rows.update((a) => [...a]);
        this.toast.show("Status atualizado");
      });
  }
}
