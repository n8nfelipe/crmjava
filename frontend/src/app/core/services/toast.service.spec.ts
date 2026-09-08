import { TestBed, fakeAsync, tick } from "@angular/core/testing";
import { ToastService } from "./toast.service";
describe("ToastService", () => {
  let service: ToastService;
  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ToastService);
  });
  it("adds and automatically removes a toast", fakeAsync(() => {
    service.show("Salvo");
    expect(service.items().length).toBe(1);
    tick(3501);
    expect(service.items().length).toBe(0);
  }));
});
