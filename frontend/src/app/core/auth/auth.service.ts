import { HttpClient } from "@angular/common/http";
import { computed, inject, Injectable, signal } from "@angular/core";
import { Router } from "@angular/router";
import { Observable, tap } from "rxjs";
import { TokenResponse, UserView } from "../api.types";
@Injectable({ providedIn: "root" })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private access = signal<string | null>(null);
  private current = signal<UserView | null>(this.readUser());
  readonly user = this.current.asReadonly();
  readonly authenticated = computed(() => !!this.current());
  token() {
    return this.access();
  }
  login(email: string, password: string) {
    return this.http
      .post<TokenResponse>("/api/auth/login", { email, password })
      .pipe(tap((x) => this.accept(x)));
  }
  refresh(): Observable<TokenResponse> {
    const refreshToken = sessionStorage.getItem("crm_refresh");
    return this.http
      .post<TokenResponse>("/api/auth/refresh", { refreshToken })
      .pipe(tap((x) => this.accept(x)));
  }
  logout() {
    const refreshToken = sessionStorage.getItem("crm_refresh");
    this.http
      .post("/api/auth/logout", { refreshToken })
      .subscribe({ complete: () => this.clear(), error: () => this.clear() });
  }
  clear() {
    this.access.set(null);
    this.current.set(null);
    sessionStorage.removeItem("crm_refresh");
    sessionStorage.removeItem("crm_user");
    void this.router.navigateByUrl("/login");
  }
  private accept(x: TokenResponse) {
    this.access.set(x.accessToken);
    this.current.set(x.user);
    sessionStorage.setItem("crm_refresh", x.refreshToken);
    sessionStorage.setItem("crm_user", JSON.stringify(x.user));
  }
  private readUser() {
    try {
      return JSON.parse(
        sessionStorage.getItem("crm_user") || "null",
      ) as UserView | null;
    } catch {
      return null;
    }
  }
}
