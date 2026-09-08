import { DatePipe } from "@angular/common";
import { Component, inject, OnInit, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { ApiService } from "../../core/services/api.service";
import { ToastService } from "../../core/services/toast.service";
interface Activity {
  id: string;
  type: string;
  subject: string;
  description?: string;
  dueAt: string;
  completedAt?: string;
  state: "PENDING" | "OVERDUE" | "COMPLETED";
}
@Component({
  standalone: true,
  imports: [DatePipe, ReactiveFormsModule],
  template: `<section class="page">
      <div class="page-head">
        <h1>Atividades</h1>
        <button class="btn" (click)="show.set(true)">+ Nova atividade</button>
      </div>
      <div class="tabs">
        <button [class.active]="filter() === ''" (click)="filter.set('')">
          Todas</button
        ><button
          [class.active]="filter() === 'PENDING'"
          (click)="filter.set('PENDING')"
        >
          Pendentes</button
        ><button
          [class.active]="filter() === 'OVERDUE'"
          (click)="filter.set('OVERDUE')"
        >
          Atrasadas</button
        ><button
          [class.active]="filter() === 'COMPLETED'"
          (click)="filter.set('COMPLETED')"
        >
          Concluídas
        </button>
      </div>
      <div class="card table-wrap">
        <table>
          <thead>
            <tr>
              <th>Atividade</th>
              <th>Tipo</th>
              <th>Prazo</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            @for (a of filtered(); track a.id) {
              <tr>
                <td>
                  <b>{{ a.subject }}</b
                  ><br /><small>{{ a.description }}</small>
                </td>
                <td>{{ labels[a.type] || a.type }}</td>
                <td>{{ a.dueAt | date: "dd/MM/yyyy HH:mm" }}</td>
                <td>
                  <span class="badge" [class.overdue]="a.state === 'OVERDUE'">{{
                    a.state
                  }}</span>
                </td>
                <td>
                  @if (!a.completedAt) {
                    <button class="btn secondary" (click)="complete(a)">
                      Concluir
                    </button>
                  }
                </td>
              </tr>
            } @empty {
              <tr>
                <td colspan="5" class="empty">
                  Nenhuma atividade neste estado.
                </td>
              </tr>
            }
          </tbody>
        </table>
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
          <h2>Nova atividade</h2>
          <div class="form-grid">
            <label
              >Tipo<select class="field" formControlName="type">
                <option value="TASK">Tarefa</option>
                <option value="CALL">Ligação</option>
                <option value="MEETING">Reunião</option>
                <option value="FOLLOW_UP">Follow-up</option>
                <option value="REMINDER">Lembrete</option>
              </select></label
            ><label
              >Prazo<input
                class="field"
                type="datetime-local"
                formControlName="dueAt" /></label
            ><label class="span-2"
              >Assunto<input class="field" formControlName="subject" /></label
            ><label class="span-2"
              >Descrição<textarea
                class="field"
                formControlName="description"
              ></textarea>
            </label>
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
      .tabs {
        display: flex;
        gap: 0.3rem;
        margin-bottom: 1rem;
      }
      .tabs button {
        border: 0;
        background: transparent;
        color: var(--muted);
        padding: 0.6rem 0.85rem;
        border-radius: 9px;
      }
      .tabs .active {
        background: var(--primary-soft);
        color: var(--primary);
        font-weight: 700;
      }
      .overdue {
        background: #ffe9ed;
        color: var(--danger);
      }
      small {
        color: var(--muted);
      }
    `,
  ],
})
export class ActivitiesComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);
  private toast = inject(ToastService);
  rows = signal<Activity[]>([]);
  filter = signal("");
  show = signal(false);
  labels: Record<string, string> = {
    TASK: "Tarefa",
    CALL: "Ligação",
    MEETING: "Reunião",
    FOLLOW_UP: "Follow-up",
    REMINDER: "Lembrete",
  };
  form = this.fb.nonNullable.group({
    type: ["TASK", Validators.required],
    subject: ["", Validators.required],
    description: [""],
    dueAt: ["", Validators.required],
    customerId: [null],
    leadId: [null],
    opportunityId: [null],
    ownerId: [null],
  });
  filtered() {
    return this.rows().filter(
      (x) => !this.filter() || x.state === this.filter(),
    );
  }
  ngOnInit() {
    this.load();
  }
  load() {
    this.api
      .page<Activity>("/api/activities", { size: 100 })
      .subscribe((x) => this.rows.set(x.content));
  }
  save() {
    const r = this.form.getRawValue();
    this.api
      .post("/api/activities", { ...r, dueAt: new Date(r.dueAt).toISOString() })
      .subscribe(() => {
        this.show.set(false);
        this.toast.show("Atividade criada");
        this.load();
      });
  }
  complete(a: Activity) {
    this.api.patch(`/api/activities/${a.id}/complete`, {}).subscribe(() => {
      this.toast.show("Atividade concluída");
      this.load();
    });
  }
}
