import { HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { catchError, switchMap, throwError } from "rxjs";
import { AuthService } from "./auth.service";
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService),
    token = auth.token(),
    request = token
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;
  return next(request).pipe(
    catchError((err) => {
      if (err.status !== 401 || req.url.includes("/auth/"))
        return throwError(() => err);
      return auth.refresh().pipe(
        switchMap((x) =>
          next(
            req.clone({
              setHeaders: { Authorization: `Bearer ${x.accessToken}` },
            }),
          ),
        ),
        catchError((e) => {
          auth.clear();
          return throwError(() => e);
        }),
      );
    }),
  );
};
