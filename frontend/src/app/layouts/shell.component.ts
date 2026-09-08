import { Component, signal } from "@angular/core";
import { RouterLink, RouterLinkActive, RouterOutlet } from "@angular/router";
import { AuthService } from "../core/auth/auth.service";
@Component({
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `<div class="shell" [class.closed]="collapsed()">
    <aside>
      <div class="brand"><b>N</b><span>Nexus CRM</span></div>
      <nav>
        @for (i of menu; track i.path) {
          <a [routerLink]="i.path" routerLinkActive="active"
            ><i>{{ i.icon }}</i
            ><span>{{ i.label }}</span></a
          >
        }
      </nav>
      <button class="logout" (click)="auth.logout()">
        <i>↪</i><span>Sair</span>
      </button>
    </aside>
    <main>
      <header>
        <button class="icon-btn" (click)="toggle()">☰</button>
        <div class="global">
          <span>⌕</span
          ><input placeholder="Buscar clientes, leads, negócios..." />
        </div>
        <button class="icon-btn" (click)="theme()">◐</button>
        <div class="avatar">
          {{ auth.user()?.name?.charAt(0) }}<span>{{ auth.user()?.name }}</span>
        </div>
      </header>
      <router-outlet />
    </main>
  </div>`,
  styles: [
    `
      .shell {
        min-height: 100vh;
        display: grid;
        grid-template-columns: 245px 1fr;
        transition: 0.2s;
      }
      .shell.closed {
        grid-template-columns: 78px 1fr;
      }
      aside {
        position: sticky;
        top: 0;
        height: 100vh;
        background: #17192c;
        color: #c7c9da;
        padding: 1.1rem;
        display: flex;
        flex-direction: column;
        overflow: hidden;
      }
      .brand {
        height: 52px;
        display: flex;
        align-items: center;
        gap: 0.8rem;
        color: white;
        font-size: 1.12rem;
        white-space: nowrap;
      }
      .brand b {
        display: grid;
        place-items: center;
        min-width: 40px;
        height: 40px;
        border-radius: 12px;
        background: var(--primary);
        font-size: 1.3rem;
      }
      nav {
        display: grid;
        gap: 0.3rem;
        margin-top: 1.3rem;
      }
      a,
      .logout {
        height: 45px;
        display: flex;
        align-items: center;
        gap: 0.85rem;
        border: 0;
        border-radius: 10px;
        padding: 0 0.75rem;
        color: inherit;
        text-decoration: none;
        background: transparent;
        white-space: nowrap;
      }
      a:hover,
      a.active {
        background: #292c47;
        color: white;
      }
      i {
        font-style: normal;
        font-size: 1.15rem;
        min-width: 22px;
        text-align: center;
      }
      .logout {
        margin-top: auto;
        font: inherit;
      }
      .closed aside {
        padding-inline: 0.65rem;
      }
      .closed .brand span,
      .closed nav span,
      .closed .logout span {
        display: none;
      }
      main {
        min-width: 0;
      }
      header {
        height: 72px;
        padding: 0 1.5rem;
        background: var(--surface);
        border-bottom: 1px solid var(--border);
        display: flex;
        align-items: center;
        gap: 1rem;
        position: sticky;
        top: 0;
        z-index: 10;
      }
      .global {
        margin: auto;
        max-width: 520px;
        flex: 1;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        background: var(--bg);
        border-radius: 10px;
        padding: 0 0.8rem;
      }
      .global input {
        width: 100%;
        border: 0;
        background: transparent;
        padding: 0.7rem;
        outline: 0;
        color: var(--text);
      }
      .avatar {
        display: flex;
        align-items: center;
        gap: 0.6rem;
        background: var(--primary);
        color: #fff;
        width: 36px;
        height: 36px;
        border-radius: 50%;
        justify-content: center;
        font-weight: 700;
      }
      .avatar span {
        color: var(--text);
        white-space: nowrap;
        font-weight: 500;
      }
      @media (max-width: 800px) {
        .shell,
        .shell.closed {
          grid-template-columns: 1fr;
        }
        aside {
          position: fixed;
          z-index: 20;
          width: 245px;
          transform: translateX(0);
          transition: 0.2s;
        }
        .closed aside {
          transform: translateX(-100%);
        }
        header {
          padding: 0 1rem;
        }
        .avatar span {
          display: none;
        }
        .global {
          display: none;
        }
      }
    `,
  ],
})
export class ShellComponent {
  collapsed = signal(innerWidth < 800);
  constructor(public auth: AuthService) {}
  toggle() {
    this.collapsed.update((value) => !value);
  }
  menu = [
    { path: "/dashboard", icon: "▦", label: "Dashboard" },
    { path: "/customers", icon: "◉", label: "Clientes" },
    { path: "/leads", icon: "◎", label: "Leads" },
    { path: "/pipeline", icon: "▥", label: "Pipeline" },
    { path: "/activities", icon: "✓", label: "Atividades" },
    { path: "/products", icon: "◇", label: "Produtos" },
    { path: "/reports", icon: "↗", label: "Relatórios" },
  ];
  theme() {
    document.documentElement.classList.toggle("dark");
    localStorage.setItem(
      "crm_theme",
      document.documentElement.classList.contains("dark") ? "dark" : "light",
    );
  }
}
