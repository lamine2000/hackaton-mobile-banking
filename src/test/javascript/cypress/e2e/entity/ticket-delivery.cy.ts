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

describe('TicketDelivery e2e test', () => {
  const ticketDeliveryPageUrl = '/ticket-delivery';
  const ticketDeliveryPageUrlPattern = new RegExp('/ticket-delivery(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const ticketDeliverySample = { boughtAt: '2023-01-16T03:34:28.825Z', quantity: 6355 };

  let ticketDelivery;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/ticket-deliveries+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/ticket-deliveries').as('postEntityRequest');
    cy.intercept('DELETE', '/api/ticket-deliveries/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (ticketDelivery) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ticket-deliveries/${ticketDelivery.id}`,
      }).then(() => {
        ticketDelivery = undefined;
      });
    }
  });

  it('TicketDeliveries menu should load TicketDeliveries page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ticket-delivery');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TicketDelivery').should('exist');
    cy.url().should('match', ticketDeliveryPageUrlPattern);
  });

  describe('TicketDelivery page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ticketDeliveryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TicketDelivery page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/ticket-delivery/new$'));
        cy.getEntityCreateUpdateHeading('TicketDelivery');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketDeliveryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/ticket-deliveries',
          body: ticketDeliverySample,
        }).then(({ body }) => {
          ticketDelivery = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/ticket-deliveries+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/ticket-deliveries?page=0&size=20>; rel="last",<http://localhost/api/ticket-deliveries?page=0&size=20>; rel="first"',
              },
              body: [ticketDelivery],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(ticketDeliveryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TicketDelivery page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('ticketDelivery');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketDeliveryPageUrlPattern);
      });

      it('edit button click should load edit TicketDelivery page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TicketDelivery');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketDeliveryPageUrlPattern);
      });

      it('edit button click should load edit TicketDelivery page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TicketDelivery');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketDeliveryPageUrlPattern);
      });

      it('last delete button click should delete instance of TicketDelivery', () => {
        cy.intercept('GET', '/api/ticket-deliveries/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('ticketDelivery').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketDeliveryPageUrlPattern);

        ticketDelivery = undefined;
      });
    });
  });

  describe('new TicketDelivery page', () => {
    beforeEach(() => {
      cy.visit(`${ticketDeliveryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TicketDelivery');
    });

    it('should create an instance of TicketDelivery', () => {
      cy.get(`[data-cy="boughtAt"]`).type('2023-01-16T13:36').blur().should('have.value', '2023-01-16T13:36');

      cy.get(`[data-cy="boughtBy"]`).type('Specialiste').should('have.value', 'Specialiste');

      cy.get(`[data-cy="quantity"]`).type('35802').should('have.value', '35802');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        ticketDelivery = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', ticketDeliveryPageUrlPattern);
    });
  });
});
