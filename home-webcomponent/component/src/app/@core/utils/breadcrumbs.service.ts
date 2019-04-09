import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable()
export class BreadcrumbsService {
    public items: Subject<string[]> = new Subject<string[]>();

    public setBreadcrumbs(items: string[]) {
        this.items.next(items);
    }

    public getBreadcrumbs() {
        return this.items.asObservable();
    }
}
