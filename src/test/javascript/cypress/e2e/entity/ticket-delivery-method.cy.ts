import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('TicketDeliveryMethod e2e test', () => {
  const ticketDeliveryMethodPageUrl = '/ticket-delivery-method';
  const ticketDeliveryMethodPageUrlPattern = new RegExp('/ticket-delivery-method(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const ticketDeliveryMethodSample = { name: 'Borders', description: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=' };

  let ticketDeliveryMethod;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/ticket-delivery-methods+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/ticket-delivery-methods').as('postEntityRequest');
    cy.intercept('DELETE', '/api/ticket-delivery-methods/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (ticketDeliveryMethod) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ticket-delivery-methods/${ticketDeliveryMethod.id}`,
      }).then(() => {
        ticketDeliveryMethod = undefined;
      });
    }
  });

  it('TicketDeliveryMethods menu should load TicketDeliveryMethods page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ticket-delivery-method');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TicketDeliveryMethod').should('exist');
    cy.url().should('match', ticketDeliveryMethodPageUrlPattern);
  });

  describe('TicketDeliveryMethod page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ticketDeliveryMethodPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TicketDeliveryMethod page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/ticket-delivery-method/new$'));
        cy.getEntityCreateUpdateHeading('TicketDeliveryMethod');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketDeliveryMethodPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/ticket-delivery-methods',
          body: ticketDeliveryMethodSample,
        }).then(({ body }) => {
          ticketDeliveryMethod = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/ticket-delivery-methods+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/ticket-delivery-methods?page=0&size=20>; rel="last",<http://localhost/api/ticket-delivery-methods?page=0&size=20>; rel="first"',
              },
              body: [ticketDeliveryMethod],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(ticketDeliveryMethodPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TicketDeliveryMethod page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('ticketDeliveryMethod');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketDeliveryMethodPageUrlPattern);
      });

      it('edit button click should load edit TicketDeliveryMethod page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TicketDeliveryMethod');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketDeliveryMethodPageUrlPattern);
      });

      it('edit button click should load edit TicketDeliveryMethod page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TicketDeliveryMethod');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketDeliveryMethodPageUrlPattern);
      });

      it('last delete button click should delete instance of TicketDeliveryMethod', () => {
        cy.intercept('GET', '/api/ticket-delivery-methods/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('ticketDeliveryMethod').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketDeliveryMethodPageUrlPattern);

        ticketDeliveryMethod = undefined;
      });
    });
  });

  describe('new TicketDeliveryMethod page', () => {
    beforeEach(() => {
      cy.visit(`${ticketDeliveryMethodPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TicketDeliveryMethod');
    });

    it('should create an instance of TicketDeliveryMethod', () => {
      cy.get(`[data-cy="name"]`).type('Unbranded Ruble Fantastic').should('have.value', 'Unbranded Ruble Fantastic');

      cy.get(`[data-cy="description"]`)
        .type('../fake-data/blob/hipster.txt')
        .invoke('val')
        .should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        ticketDeliveryMethod = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', ticketDeliveryMethodPageUrlPattern);
    });
  });
});
