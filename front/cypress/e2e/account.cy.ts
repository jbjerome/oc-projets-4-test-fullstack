const userWith = (admin: boolean) => ({
  id: 1,
  email: 'yoga@studio.com',
  firstName: 'Admin',
  lastName: 'User',
  admin,
  createdAt: '2025-01-01T00:00:00',
  updatedAt: '2025-01-01T00:00:00',
});

describe('Account - informations utilisateur', () => {
  it("affiche les informations de l'utilisateur admin", () => {
    cy.intercept('GET', '/api/session', { body: [] }).as('sessions');
    cy.loginAs(true);

    cy.intercept('GET', '/api/user/1', { body: userWith(true) }).as('user');
    cy.get('[data-testid=nav-account]').click();
    cy.wait('@user');

    cy.url().should('include', '/me');
    cy.get('[data-testid=me-name]').should('contain.text', 'Admin');
    cy.get('[data-testid=me-email]').should('contain.text', 'yoga@studio.com');
    cy.get('[data-testid=me-admin]').should('be.visible');
    cy.get('[data-testid=me-delete]').should('not.exist');
  });

  it("permet à un utilisateur non admin de supprimer son compte", () => {
    cy.intercept('GET', '/api/session', { body: [] }).as('sessions');
    cy.loginAs(false);

    cy.intercept('GET', '/api/user/1', { body: userWith(false) }).as('user');
    cy.get('[data-testid=nav-account]').click();
    cy.wait('@user');

    cy.get('[data-testid=me-delete]').should('be.visible');
    cy.intercept('DELETE', '/api/user/1', { statusCode: 200, body: {} }).as('delete');
    cy.get('[data-testid=me-delete]').click();

    cy.wait('@delete');
    cy.url().should('match', /\/$|\/login/);
  });
});

describe('Logout - déconnexion', () => {
  it("déconnecte l'utilisateur et le ramène au login", () => {
    cy.intercept('GET', '/api/session', { body: [] }).as('sessions');
    cy.loginAs(true);

    cy.get('[data-testid=nav-logout]').click();
    cy.url().should('include', '/login');
    cy.get('[data-testid=nav-login]').should('be.visible');
  });
});
