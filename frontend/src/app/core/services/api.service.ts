import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { PageResponse } from "../api.types";
@Injectable({ providedIn: "root" })
export class ApiService {
  private http = inject(HttpClient);
  page<T>(
    url: string,
    params: Record<string, string | number | undefined> = {},
  ) {
    let p = new HttpParams();
    Object.entries(params).forEach(([k, v]) => {
      if (v !== undefined && v !== "") p = p.set(k, v);
    });
    return this.http.get<PageResponse<T>>(url, { params: p });
  }
  get<T>(url: string) {
    return this.http.get<T>(url);
  }
  post<T>(url: string, body: unknown) {
    return this.http.post<T>(url, body);
  }
  put<T>(url: string, body: unknown) {
    return this.http.put<T>(url, body);
  }
  patch<T>(url: string, body: unknown) {
    return this.http.patch<T>(url, body);
  }
  delete(url: string) {
    return this.http.delete<void>(url);
  }
}
