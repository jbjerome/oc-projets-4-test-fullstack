const TEACHERS = [
  { id: 1, firstName: 'Margot', lastName: 'Delahaye', createdAt: '2025-01-01T00:00:00', updatedAt: '2025-01-01T00:00:00' },
  { id: 2, firstName: 'Helene', lastName: 'Thiercelin', createdAt: '2025-01-01T00:00:00', updatedAt: '2025-01-01T00:00:00' },
];

const SESSION = {
  id: 1,
  name: 'Yoga matin',
  description: 'Séance du matin',
  date: '2025-01-01T00:00:00.000+00:00',
  teacher_id: 1,
  users: [],
  createdAt: '2025-01-01T00:00:00',
  updatedAt: '2025-01-01T00:00:00',
};

describe('Sessions - création', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/session', { body: [SESSION] }).as('sessions');
    cy.intercept('GET', '/api/teacher', { body: TEACHERS }).as('teachers');
    cy.loginAs(true);
    cy.wait('@sessions');
    cy.get('[data-testid=session-create]').click();
    cy.url().should('include', '/sessions/create');
    cy.wait('@teachers');
  });

  it('crée une session puis revient à la liste', () => {
    cy.intercept('POST', '/api/session', { statusCode: 200, body: SESSION }).as('create');

    cy.get('[data-testid=form-submit]').should('be.disabled');

    cy.get('[data-testid=form-name]').type('Yoga du soir');
    cy.get('[data-testid=form-date]').type('2025-02-01');
    cy.get('[data-testid=form-teacher]').click();
    cy.get('[data-testid=form-teacher-option-1]').click();
    cy.get('[data-testid=form-description]').type('Une séance relaxante en fin de journée');

    cy.get('[data-testid=form-submit]').should('not.be.disabled').click();
    cy.wait('@create');
    cy.url().should('match', /\/sessions$/);
  });

  it("garde le bouton désactivé tant qu'un champ obligatoire est vide", () => {
    cy.get('[data-testid=form-name]').type('Yoga du soir');
    cy.get('[data-testid=form-submit]').should('be.disabled');
  });
});

describe('Sessions - modification', () => {
  it('modifie une session existante', () => {
    cy.intercept('GET', '/api/session', { body: [SESSION] }).as('sessions');
    cy.intercept('GET', '/api/teacher', { body: TEACHERS }).as('teachers');
    cy.intercept('GET', '/api/session/1', { body: SESSION }).as('detail');
    cy.loginAs(true);
    cy.wait('@sessions');

    cy.get('[data-testid=session-edit]').click();
    cy.url().should('include', '/sessions/update/1');
    cy.wait(['@teachers', '@detail']);

    cy.get('[data-testid=form-name]').should('have.value', 'Yoga matin');

    cy.intercept('PUT', '/api/session/1', { statusCode: 200, body: SESSION }).as('update');
    cy.get('[data-testid=form-name]').clear();
    cy.get('[data-testid=form-name]').type('Yoga matin modifié');
    cy.get('[data-testid=form-submit]').click();

    cy.wait('@update');
    cy.url().should('match', /\/sessions$/);
  });
});
