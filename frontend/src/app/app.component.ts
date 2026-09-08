import { Component, inject } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { ToastService } from "./core/services/toast.service";
@Component({
  selector: "app-root",
  standalone: true,
  imports: [RouterOutlet],
  template: `<router-outlet />
    <div class="toasts">
      @for (t of toast.items(); track t.id) {
        <div class="toast" [class.error]="t.kind === 'error'">
          {{ t.message }}
        </div>
      }
    </div>`,
})
export class AppComponent {
  protected toast = inject(ToastService);
}
