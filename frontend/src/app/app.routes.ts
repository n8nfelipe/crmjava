import { Routes } from "@angular/router";
import { authGuard } from "./core/guards/auth.guard";
export const routes: Routes = [
  {
    path: "login",
    loadComponent: () =>
      import("./features/auth/login.component").then((m) => m.LoginComponent),
  },
  {
    path: "reset-password",
    loadComponent: () =>
      import("./features/auth/reset-password.component").then(
        (m) => m.ResetPasswordComponent,
      ),
  },
  {
    path: "",
    canActivate: [authGuard],
    loadComponent: () =>
      import("./layouts/shell.component").then((m) => m.ShellComponent),
    children: [
      {
        path: "dashboard",
        loadComponent: () =>
          import("./features/dashboard/dashboard.component").then(
            (m) => m.DashboardComponent,
          ),
      },
      {
        path: "customers",
        loadComponent: () =>
          import("./features/customers/customers.component").then(
            (m) => m.CustomersComponent,
          ),
      },
      {
        path: "leads",
        loadComponent: () =>
          import("./features/leads/leads.component").then(
            (m) => m.LeadsComponent,
          ),
      },
      {
        path: "pipeline",
        loadComponent: () =>
          import("./features/pipeline/pipeline.component").then(
            (m) => m.PipelineComponent,
          ),
      },
      {
        path: "activities",
        loadComponent: () =>
          import("./features/activities/activities.component").then(
            (m) => m.ActivitiesComponent,
          ),
      },
      {
        path: "products",
        loadComponent: () =>
          import("./features/products/products.component").then(
            (m) => m.ProductsComponent,
          ),
      },
      {
        path: "reports",
        loadComponent: () =>
          import("./features/reports/reports.component").then(
            (m) => m.ReportsComponent,
          ),
      },
      { path: "", pathMatch: "full", redirectTo: "dashboard" },
    ],
  },
  { path: "**", redirectTo: "" },
];
