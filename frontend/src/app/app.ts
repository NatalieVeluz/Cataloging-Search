import { Component } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { NgIf } from '@angular/common';
import { Heading } from './heading/heading';
import { NavigationComponent } from './layout/navigation/navigation.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Heading, NavigationComponent, NgIf],
  templateUrl: './app.html'
})
export class AppComponent {

  showLayout = true;

  constructor(private router: Router) {

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {

        if (event.url === '/' || event.url.includes('login')) {
          this.showLayout = false;
        } else {
          this.showLayout = true;
        }

      });

  }

}
