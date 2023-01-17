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

describe('Ticket e2e test', () => {
  const ticketPageUrl = '/ticket';
  const ticketPageUrlPattern = new RegExp('/ticket(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const ticketSample = {
    data: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=',
    dataContentType: 'unknown',
    pricePerUnit: 96770,
    status: 'EXPIRED',
  };

  let ticket;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/tickets+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/tickets').as('postEntityRequest');
    cy.intercept('DELETE', '/api/tickets/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (ticket) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tickets/${ticket.id}`,
      }).then(() => {
        ticket = undefined;
      });
    }
  });

  it('Tickets menu should load Tickets page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ticket');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Ticket').should('exist');
    cy.url().should('match', ticketPageUrlPattern);
  });

  describe('Ticket page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ticketPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Ticket page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/ticket/new$'));
        cy.getEntityCreateUpdateHeading('Ticket');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/tickets',
          body: ticketSample,
        }).then(({ body }) => {
          ticket = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/tickets+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/tickets?page=0&size=20>; rel="last",<http://localhost/api/tickets?page=0&size=20>; rel="first"',
              },
              body: [ticket],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(ticketPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Ticket page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('ticket');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPageUrlPattern);
      });

      it('edit button click should load edit Ticket page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Ticket');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPageUrlPattern);
      });

      it('edit button click should load edit Ticket page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Ticket');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPageUrlPattern);
      });

      it('last delete button click should delete instance of Ticket', () => {
        cy.intercept('GET', '/api/tickets/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('ticket').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPageUrlPattern);

        ticket = undefined;
      });
    });
  });

  describe('new Ticket page', () => {
    beforeEach(() => {
      cy.visit(`${ticketPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Ticket');
    });

    it('should create an instance of Ticket', () => {
      cy.get(`[data-cy="code"]`).type('Stand-alone Metal Bedfordshire').should('have.value', 'Stand-alone Metal Bedfordshire');

      cy.setFieldImageAsBytesOfEntity('data', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="pricePerUnit"]`).type('20918').should('have.value', '20918');

      cy.get(`[data-cy="finalAgentCommission"]`).type('97589').should('have.value', '97589');

      cy.get(`[data-cy="status"]`).select('EXPIRED');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        ticket = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', ticketPageUrlPattern);
    });
  });
});
