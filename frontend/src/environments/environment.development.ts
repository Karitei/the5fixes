/** Development: requests are proxied to the Spring Boot API by proxy.conf.json. */
export const environment = {
  production: false,
  apiUrl: '/api',
} as const;
