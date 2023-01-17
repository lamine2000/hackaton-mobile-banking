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

describe('SupplyRequest e2e test', () => {
  const supplyRequestPageUrl = '/supply-request';
  const supplyRequestPageUrlPattern = new RegExp('/supply-request(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const supplyRequestSample = { status: 'REJECTED' };

  let supplyRequest;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/supply-requests+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/supply-requests').as('postEntityRequest');
    cy.intercept('DELETE', '/api/supply-requests/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (supplyRequest) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/supply-requests/${supplyRequest.id}`,
      }).then(() => {
        supplyRequest = undefined;
      });
    }
  });

  it('SupplyRequests menu should load SupplyRequests page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('supply-request');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SupplyRequest').should('exist');
    cy.url().should('match', supplyRequestPageUrlPattern);
  });

  describe('SupplyRequest page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(supplyRequestPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SupplyRequest page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/supply-request/new$'));
        cy.getEntityCreateUpdateHeading('SupplyRequest');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyRequestPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/supply-requests',
          body: supplyRequestSample,
        }).then(({ body }) => {
          supplyRequest = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/supply-requests+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/supply-requests?page=0&size=20>; rel="last",<http://localhost/api/supply-requests?page=0&size=20>; rel="first"',
              },
              body: [supplyRequest],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(supplyRequestPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details SupplyRequest page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('supplyRequest');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyRequestPageUrlPattern);
      });

      it('edit button click should load edit SupplyRequest page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SupplyRequest');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyRequestPageUrlPattern);
      });

      it('edit button click should load edit SupplyRequest page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SupplyRequest');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyRequestPageUrlPattern);
      });

      it('last delete button click should delete instance of SupplyRequest', () => {
        cy.intercept('GET', '/api/supply-requests/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('supplyRequest').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', supplyRequestPageUrlPattern);

        supplyRequest = undefined;
      });
    });
  });

  describe('new SupplyRequest page', () => {
    beforeEach(() => {
      cy.visit(`${supplyRequestPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SupplyRequest');
    });

    it('should create an instance of SupplyRequest', () => {
      cy.get(`[data-cy="amount"]`).type('34154').should('have.value', '34154');

      cy.get(`[data-cy="quantity"]`).type('17050').should('have.value', '17050');

      cy.get(`[data-cy="status"]`).select('REJECTED');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        supplyRequest = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', supplyRequestPageUrlPattern);
    });
  });
});
