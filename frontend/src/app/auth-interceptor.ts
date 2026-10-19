import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const username = 'seconize-dev';
  const password = 'Roopesh@2004';

  if (!req.url.startsWith('http://localhost:8080/api/')) {
    return next(req);
  }

  const credentials = btoa(`${username}:${password}`);

  const authenticatedRequest = req.clone({
    setHeaders: {
      Authorization: `Basic ${credentials}`
    }
  });

  return next(authenticatedRequest);
};
