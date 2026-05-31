describe('Navigation - gardes et page 404', () => {
  it('redirige vers /login un accès non authentifié à /sessions (AuthGuard)', () => {
    cy.visit('/sessions');
    cy.url().should('include', '/login');
  });

  it('affiche la page 404 pour une route inconnue', () => {
    cy.visit('/cette-route-nexiste-pas');
    cy.url().should('include', '/404');
    cy.contains('Page not found').should('be.visible');
  });
});
