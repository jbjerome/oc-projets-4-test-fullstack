const TEACHER = {
  id: 1,
  firstName: 'Margot',
  lastName: 'Delahaye',
  createdAt: '2025-01-01T00:00:00',
  updatedAt: '2025-01-01T00:00:00',
};

const sessionWith = (users: number[]) => ({
  id: 1,
  name: 'Yoga matin',
  description: 'Séance du matin',
  date: '2025-01-01T00:00:00.000+00:00',
  teacher_id: 1,
  users,
  createdAt: '2025-01-01T00:00:00',
  updatedAt: '2025-01-01T00:00:00',
});

describe('Sessions - informations / détail', () => {
  it("affiche les informations et le bouton Delete pour un admin, puis supprime", () => {
    cy.intercept('GET', '/api/session', { body: [sessionWith([])] }).as('sessions');
    cy.loginAs(true);
    cy.wait('@sessions');

    cy.intercept('GET', '/api/session/1', { body: sessionWith([]) }).as('detail');
    cy.intercept('GET', '/api/teacher/1', { body: TEACHER }).as('teacher');
    cy.get('[data-testid=session-detail]').click();
    cy.wait(['@detail', '@teacher']);

    cy.url().should('include', '/sessions/detail/1');
    cy.get('[data-testid=detail-name]').should('contain.text', 'Yoga Matin');
    cy.get('[data-testid=detail-description]').should('contain.text', 'Séance du matin');
    cy.get('[data-testid=detail-attendees]').should('contain.text', '0 attendees');
    cy.get('[data-testid=detail-delete]').should('be.visible');

    cy.intercept('DELETE', '/api/session/1', { statusCode: 200, body: {} }).as('delete');
    cy.get('[data-testid=detail-delete]').click();
    cy.wait('@delete');
    cy.url().should('include', '/sessions');
  });

  it("permet à un non-admin de participer puis de se désinscrire", () => {
    cy.intercept('GET', '/api/session', { body: [sessionWith([])] }).as('sessions');
    cy.loginAs(false);
    cy.wait('@sessions');

    cy.intercept('GET', '/api/session/1', { body: sessionWith([]) }).as('detail');
    cy.intercept('GET', '/api/teacher/1', { body: TEACHER }).as('teacher');
    cy.get('[data-testid=session-detail]').click();
    cy.wait(['@detail', '@teacher']);

    cy.get('[data-testid=detail-delete]').should('not.exist');
    cy.get('[data-testid=detail-participate]').should('be.visible');

    // Participation : l'utilisateur (id 1) est désormais inscrit.
    cy.intercept('POST', '/api/session/1/participate/1', { statusCode: 200, body: {} }).as('participate');
    cy.intercept('GET', '/api/session/1', { body: sessionWith([1]) }).as('detailAfter');
    cy.get('[data-testid=detail-participate]').click();
    cy.wait('@participate');
    cy.get('[data-testid=detail-unparticipate]').should('be.visible');

    // Désinscription : retour à l'état initial.
    cy.intercept('DELETE', '/api/session/1/participate/1', { statusCode: 200, body: {} }).as('unparticipate');
    cy.intercept('GET', '/api/session/1', { body: sessionWith([]) }).as('detailReset');
    cy.get('[data-testid=detail-unparticipate]').click();
    cy.wait('@unparticipate');
    cy.get('[data-testid=detail-participate]').should('be.visible');
  });
});
