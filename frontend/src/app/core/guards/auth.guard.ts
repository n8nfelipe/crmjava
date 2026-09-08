import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { catchError, map, of } from "rxjs";
import { AuthService } from "../auth/auth.service";
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService),
    router = inject(Router);
  if (!auth.authenticated()) return router.createUrlTree(["/login"]);
  if (auth.token()) return true;
  return auth.refresh().pipe(
    map(() => true),
    catchError(() => of(router.createUrlTree(["/login"]))),
  );
};
