const SESSIONS = [
  {
    id: 1,
    name: 'Yoga matin',
    description: 'Séance du matin',
    date: '2025-01-01T00:00:00.000+00:00',
    teacher_id: 1,
    users: [],
    createdAt: '2025-01-01T00:00:00',
    updatedAt: '2025-01-01T00:00:00',
  },
];

describe('Sessions - liste', () => {
  it('affiche la liste et les boutons Create/Edit pour un admin', () => {
    cy.intercept('GET', '/api/session', { body: SESSIONS }).as('sessions');
    cy.loginAs(true);
    cy.wait('@sessions');

    cy.get('[data-testid=session-item]').should('have.length', 1);
    cy.get('[data-testid=session-name]').should('contain.text', 'Yoga matin');
    cy.get('[data-testid=session-create]').should('be.visible');
    cy.get('[data-testid=session-detail]').should('be.visible');
    cy.get('[data-testid=session-edit]').should('be.visible');
  });

  it('masque les boutons Create/Edit pour un utilisateur non admin', () => {
    cy.intercept('GET', '/api/session', { body: SESSIONS }).as('sessions');
    cy.loginAs(false);
    cy.wait('@sessions');

    cy.get('[data-testid=session-item]').should('have.length', 1);
    cy.get('[data-testid=session-detail]').should('be.visible');
    cy.get('[data-testid=session-create]').should('not.exist');
    cy.get('[data-testid=session-edit]').should('not.exist');
  });
});
