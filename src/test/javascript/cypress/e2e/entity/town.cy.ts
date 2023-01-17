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

describe('Town e2e test', () => {
  const townPageUrl = '/town';
  const townPageUrlPattern = new RegExp('/town(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const townSample = { name: 'b synthesize reintermediate', code: 'Chair Synchronised' };

  let town;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/towns+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/towns').as('postEntityRequest');
    cy.intercept('DELETE', '/api/towns/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (town) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/towns/${town.id}`,
      }).then(() => {
        town = undefined;
      });
    }
  });

  it('Towns menu should load Towns page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('town');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Town').should('exist');
    cy.url().should('match', townPageUrlPattern);
  });

  describe('Town page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(townPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Town page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/town/new$'));
        cy.getEntityCreateUpdateHeading('Town');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', townPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/towns',
          body: townSample,
        }).then(({ body }) => {
          town = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/towns+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/towns?page=0&size=20>; rel="last",<http://localhost/api/towns?page=0&size=20>; rel="first"',
              },
              body: [town],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(townPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Town page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('town');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', townPageUrlPattern);
      });

      it('edit button click should load edit Town page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Town');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', townPageUrlPattern);
      });

      it('edit button click should load edit Town page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Town');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', townPageUrlPattern);
      });

      it('last delete button click should delete instance of Town', () => {
        cy.intercept('GET', '/api/towns/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('town').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', townPageUrlPattern);

        town = undefined;
      });
    });
  });

  describe('new Town page', () => {
    beforeEach(() => {
      cy.visit(`${townPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Town');
    });

    it('should create an instance of Town', () => {
      cy.get(`[data-cy="name"]`).type('Card').should('have.value', 'Card');

      cy.get(`[data-cy="code"]`).type('optical engine').should('have.value', 'optical engine');

      cy.get(`[data-cy="createdAt"]`).type('2023-01-16T22:51').blur().should('have.value', '2023-01-16T22:51');

      cy.get(`[data-cy="createdBy"]`).type('Card Salomon mindshare').should('have.value', 'Card Salomon mindshare');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        town = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', townPageUrlPattern);
    });
  });
});
