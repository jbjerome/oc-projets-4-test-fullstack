describe('Register - création de compte', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('crée un compte et redirige vers le login', () => {
    cy.intercept('POST', '/api/auth/register', { statusCode: 200, body: {} }).as('register');

    cy.get('[data-testid=register-firstName]').type('John');
    cy.get('[data-testid=register-lastName]').type('Doe');
    cy.get('[data-testid=register-email]').type('john.doe@test.com');
    cy.get('[data-testid=register-password]').type('test!1234');
    cy.get('[data-testid=register-submit]').click();

    cy.wait('@register');
    cy.url().should('include', '/login');
  });

  it('affiche une erreur si la création échoue', () => {
    cy.intercept('POST', '/api/auth/register', { statusCode: 400, body: {} }).as('register');

    cy.get('[data-testid=register-firstName]').type('John');
    cy.get('[data-testid=register-lastName]').type('Doe');
    cy.get('[data-testid=register-email]').type('used@test.com');
    cy.get('[data-testid=register-password]').type('test!1234');
    cy.get('[data-testid=register-submit]').click();

    cy.wait('@register');
    cy.get('[data-testid=register-error]').should('be.visible');
  });

  it("désactive le bouton tant qu'un champ obligatoire est vide", () => {
    cy.get('[data-testid=register-submit]').should('be.disabled');

    cy.get('[data-testid=register-firstName]').type('John');
    cy.get('[data-testid=register-lastName]').type('Doe');
    cy.get('[data-testid=register-email]').type('john.doe@test.com');
    cy.get('[data-testid=register-submit]').should('be.disabled');

    cy.get('[data-testid=register-password]').type('test!1234');
    cy.get('[data-testid=register-submit]').should('not.be.disabled');
  });
});
