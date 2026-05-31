/// <reference types="cypress" />

// Commandes personnalisées Cypress.
//
// cy.loginAs(admin) : intercepte l'appel de login et réalise la connexion via
// le formulaire, puis attend la redirection vers /sessions. Le stub de
// GET /api/session doit être défini par le test AVANT l'appel à loginAs.

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      /**
       * Connecte un utilisateur (admin par défaut) en passant par le formulaire de login.
       * @param admin true pour un compte admin, false sinon.
       */
      loginAs(admin?: boolean): Chainable<void>;
    }
  }
}

Cypress.Commands.add('loginAs', (admin: boolean = true) => {
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: {
      token: 'fake-jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'yoga@studio.com',
      firstName: 'Admin',
      lastName: 'User',
      admin,
    },
  }).as('login');

  cy.visit('/login');
  cy.get('[data-testid=login-email]').type('yoga@studio.com');
  cy.get('[data-testid=login-password]').type('test!1234');
  cy.get('[data-testid=login-submit]').click();
  cy.wait('@login');
  cy.url().should('include', '/sessions');
});

export {};
