describe('Login - parcours de connexion', () => {
  it('connecte un utilisateur avec des identifiants valides', () => {
    cy.intercept('GET', '/api/session', { body: [] }).as('sessions');
    cy.loginAs(true);
    cy.url().should('include', '/sessions');
  });

  it("affiche une erreur en cas de mauvais login / mot de passe", () => {
    cy.intercept('POST', '/api/auth/login', { statusCode: 401, body: {} }).as('login');

    cy.visit('/login');
    cy.get('[data-testid=login-email]').type('yoga@studio.com');
    cy.get('[data-testid=login-password]').type('wrong-password');
    cy.get('[data-testid=login-submit]').click();

    cy.wait('@login');
    cy.get('[data-testid=login-error]').should('be.visible');
    cy.url().should('include', '/login');
  });

  it("désactive le bouton tant qu'un champ obligatoire est vide", () => {
    cy.visit('/login');
    cy.get('[data-testid=login-submit]').should('be.disabled');

    cy.get('[data-testid=login-email]').type('yoga@studio.com');
    cy.get('[data-testid=login-submit]').should('be.disabled');

    cy.get('[data-testid=login-password]').type('test!1234');
    cy.get('[data-testid=login-submit]').should('not.be.disabled');
  });
});
