import { Component, inject, OnInit, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { finalize } from "rxjs";
import { ApiService } from "../../core/services/api.service";
import { ToastService } from "../../core/services/toast.service";
interface Customer {
  id: string;
  type: "PERSON" | "COMPANY";
  name: string;
  document?: string;
  email?: string;
  phone?: string;
  createdAt: string;
}
@Component({
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `<section class="page">
      <div class="page-head">
        <h1>Clientes</h1>
        <button class="btn" (click)="open()">+ Novo cliente</button>
      </div>
      <div class="card toolbar">
        <input
          class="field"
          placeholder="Buscar por nome, e-mail ou documento"
          [value]="search()"
          (input)="search.set($any($event.target).value); load()"
        /><select
          class="field type"
          (change)="type.set($any($event.target).value); load()"
        >
          <option value="">Todos os tipos</option>
          <option value="PERSON">Pessoa física</option>
          <option value="COMPANY">Empresa</option>
        </select>
      </div>
      <div class="card table-wrap">
        <table>
          <thead>
            <tr>
              <th>Cliente</th>
              <th>Tipo</th>
              <th>Documento</th>
              <th>Contato</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            @for (c of rows(); track c.id) {
              <tr>
                <td>
                  <b>{{ c.name }}</b
                  ><br /><small>{{ c.email || "Sem e-mail" }}</small>
                </td>
                <td>
                  <span class="badge">{{
                    c.type === "PERSON" ? "Pessoa física" : "Empresa"
                  }}</span>
                </td>
                <td>{{ c.document || "—" }}</td>
                <td>{{ c.phone || "—" }}</td>
                <td>
                  <button class="icon-btn danger" (click)="remove(c)">×</button>
                </td>
              </tr>
            } @empty {
              <tr>
                <td colspan="5" class="empty">
                  Nenhum cliente encontrado. Cadastre o primeiro.
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>
      <div class="pager">
        <span>{{ total() }} clientes</span
        ><button
          class="btn secondary"
          [disabled]="page() === 0"
          (click)="previous()"
        >
          Anterior</button
        ><button
          class="btn secondary"
          [disabled]="(page() + 1) * 20 >= total()"
          (click)="next()"
        >
          Próxima
        </button>
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
          <h2>Novo cliente</h2>
          <div class="form-grid">
            <label
              >Tipo<select class="field" formControlName="type">
                <option value="PERSON">Pessoa física</option>
                <option value="COMPANY">Empresa</option>
              </select></label
            ><label
              >Nome<input class="field" formControlName="name" />
              @if (
                form.controls.name.touched &&
                form.controls.name.hasError("required")
              ) {
                <small class="field-error">Informe o nome do cliente.</small>
              }
            </label
            ><label
              >CPF/CNPJ<input class="field" formControlName="document" /></label
            ><label
              >E-mail<input
                class="field"
                type="email"
                formControlName="email" />
              @if (
                form.controls.email.touched &&
                form.controls.email.hasError("email")
              ) {
                <small class="field-error">Informe um e-mail válido.</small>
              }
            </label
            ><label
              >Telefone<input class="field" formControlName="phone" /></label
            ><label
              >Endereço<input class="field" formControlName="address" /></label
            ><label class="span-2"
              >Observações<textarea
                class="field"
                rows="4"
                formControlName="notes"
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
            ><button type="submit" class="btn" [disabled]="saving()">
              {{ saving() ? "Salvando..." : "Salvar" }}
            </button>
          </div>
        </form>
      </div>
    }`,
  styles: [
    `
      .type {
        max-width: 190px;
      }
      .pager {
        display: flex;
        justify-content: flex-end;
        align-items: center;
        gap: 0.6rem;
        margin-top: 1rem;
      }
      .pager span {
        margin-right: auto;
        color: var(--muted);
      }
      small {
        color: var(--muted);
      }
      .field-error {
        color: var(--danger);
      }
      td .danger {
        width: 32px;
        height: 32px;
      }
    `,
  ],
})
export class CustomersComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);
  private toast = inject(ToastService);
  rows = signal<Customer[]>([]);
  total = signal(0);
  page = signal(0);
  search = signal("");
  type = signal("");
  show = signal(false);
  saving = signal(false);
  form = this.fb.nonNullable.group({
    type: ["PERSON" as const, Validators.required],
    name: ["", Validators.required],
    document: [""],
    email: ["", Validators.email],
    phone: [""],
    address: [""],
    notes: [""],
    ownerId: [null],
  });
  ngOnInit() {
    this.load();
  }
  previous() {
    this.page.update((value) => value - 1);
    this.load();
  }
  next() {
    this.page.update((value) => value + 1);
    this.load();
  }
  load() {
    this.api
      .page<Customer>("/api/customers", {
        search: this.search(),
        type: this.type(),
        page: this.page(),
        size: 20,
        sort: "createdAt,desc",
      })
      .subscribe((x) => {
        this.rows.set(x.content);
        this.total.set(x.totalElements);
      });
  }
  open() {
    this.form.reset({
      type: "PERSON",
      name: "",
      document: "",
      email: "",
      phone: "",
      address: "",
      notes: "",
      ownerId: null,
    });
    this.show.set(true);
  }
  save() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.toast.show("Revise os campos destacados", "error");
      return;
    }

    const value = this.form.getRawValue();
    const optional = (text: string) => text.trim() || null;
    const payload = {
      ...value,
      name: value.name.trim(),
      document: optional(value.document),
      email: optional(value.email),
      phone: optional(value.phone),
      address: optional(value.address),
      notes: optional(value.notes),
    };

    if (!payload.name) {
      this.form.controls.name.setErrors({ required: true });
      this.form.controls.name.markAsTouched();
      this.toast.show("Informe o nome do cliente", "error");
      return;
    }

    this.saving.set(true);
    this.api
      .post<Customer>("/api/customers", payload)
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: () => {
          this.show.set(false);
          this.toast.show("Cliente criado");
          this.load();
        },
      });
  }
  remove(c: Customer) {
    if (!confirm(`Excluir ${c.name}? Esta ação não pode ser desfeita.`)) return;
    this.api.delete(`/api/customers/${c.id}`).subscribe(() => {
      this.toast.show("Cliente excluído");
      this.load();
    });
  }
}
