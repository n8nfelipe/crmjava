import { CurrencyPipe } from "@angular/common";
import { Component, inject, OnInit, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { ApiService } from "../../core/services/api.service";
import { ToastService } from "../../core/services/toast.service";

interface Product { id: string; name: string; category?: string; price: number; active: boolean }

@Component({
  standalone: true,
  imports: [CurrencyPipe, ReactiveFormsModule],
  template: `
    <section class="page">
      <div class="page-head"><h1>Produtos e serviços</h1><button class="btn" (click)="show.set(true)">+ Novo produto</button></div>
      <div class="card table-wrap"><table><thead><tr><th>Produto</th><th>Categoria</th><th>Preço</th><th>Status</th><th></th></tr></thead><tbody>
        @for (product of rows(); track product.id) {
          <tr><td><b>{{ product.name }}</b></td><td>{{ product.category || "—" }}</td><td>{{ product.price | currency: "BRL" }}</td><td><span class="badge">{{ product.active ? "Ativo" : "Inativo" }}</span></td><td><button class="icon-btn" (click)="edit(product)">✎</button> <button class="icon-btn" (click)="remove(product)">×</button></td></tr>
        } @empty { <tr><td colspan="5" class="empty">Cadastre produtos e serviços para associá-los às oportunidades.</td></tr> }
      </tbody></table></div>
    </section>
    @if (show()) {
      <div class="modal-backdrop" (click)="close()"><form class="modal" [formGroup]="form" (ngSubmit)="save()" (click)="$event.stopPropagation()">
        <h2>{{ editing() ? "Editar" : "Novo" }} produto</h2><div class="form-grid">
          <label>Nome<input class="field" formControlName="name" /></label><label>Categoria<input class="field" formControlName="category" /></label>
          <label>Preço<input class="field" type="number" min="0" step="0.01" formControlName="price" /></label><label>Status<select class="field" formControlName="active"><option [ngValue]="true">Ativo</option><option [ngValue]="false">Inativo</option></select></label>
        </div><div class="actions"><button type="button" class="btn secondary" (click)="close()">Cancelar</button><button class="btn" [disabled]="form.invalid">Salvar</button></div>
      </form></div>
    }
  `,
})
export class ProductsComponent implements OnInit {
  private api = inject(ApiService); private fb = inject(FormBuilder); private toast = inject(ToastService);
  rows = signal<Product[]>([]); show = signal(false); editing = signal<Product | null>(null);
  form = this.fb.nonNullable.group({ name: ["", Validators.required], category: [""], price: [0, [Validators.required, Validators.min(0)]], active: [true] });
  ngOnInit() { this.load(); }
  load() { this.api.page<Product>("/api/products", { size: 100 }).subscribe((page) => this.rows.set(page.content)); }
  edit(product: Product) { this.editing.set(product); this.form.patchValue(product); this.show.set(true); }
  close() { this.show.set(false); this.editing.set(null); this.form.reset({ price: 0, active: true }); }
  save() { const request = this.editing() ? this.api.put<Product>(`/api/products/${this.editing()!.id}`, this.form.getRawValue()) : this.api.post<Product>("/api/products", this.form.getRawValue()); request.subscribe(() => { this.toast.show("Produto salvo"); this.close(); this.load(); }); }
  remove(product: Product) { if (confirm(`Excluir ${product.name}?`)) this.api.delete(`/api/products/${product.id}`).subscribe(() => this.load()); }
}
