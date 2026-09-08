import { Injectable, signal } from "@angular/core";
@Injectable({ providedIn: "root" })
export class ToastService {
  readonly items = signal<
    { id: number; message: string; kind: "ok" | "error" }[]
  >([]);
  show(message: string, kind: "ok" | "error" = "ok") {
    const id = Date.now();
    this.items.update((x) => [...x, { id, message, kind }]);
    setTimeout(
      () => this.items.update((x) => x.filter((t) => t.id !== id)),
      3500,
    );
  }
}
