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

describe('Functionality e2e test', () => {
  const functionalityPageUrl = '/functionality';
  const functionalityPageUrlPattern = new RegExp('/functionality(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const functionalitySample = { image: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=', imageContentType: 'unknown', status: 'OUT_OF_CREDITS' };

  let functionality;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/functionalities+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/functionalities').as('postEntityRequest');
    cy.intercept('DELETE', '/api/functionalities/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (functionality) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/functionalities/${functionality.id}`,
      }).then(() => {
        functionality = undefined;
      });
    }
  });

  it('Functionalities menu should load Functionalities page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('functionality');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Functionality').should('exist');
    cy.url().should('match', functionalityPageUrlPattern);
  });

  describe('Functionality page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(functionalityPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Functionality page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/functionality/new$'));
        cy.getEntityCreateUpdateHeading('Functionality');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', functionalityPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/functionalities',
          body: functionalitySample,
        }).then(({ body }) => {
          functionality = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/functionalities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/functionalities?page=0&size=20>; rel="last",<http://localhost/api/functionalities?page=0&size=20>; rel="first"',
              },
              body: [functionality],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(functionalityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Functionality page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('functionality');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', functionalityPageUrlPattern);
      });

      it('edit button click should load edit Functionality page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Functionality');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', functionalityPageUrlPattern);
      });

      it('edit button click should load edit Functionality page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Functionality');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', functionalityPageUrlPattern);
      });

      it('last delete button click should delete instance of Functionality', () => {
        cy.intercept('GET', '/api/functionalities/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('functionality').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', functionalityPageUrlPattern);

        functionality = undefined;
      });
    });
  });

  describe('new Functionality page', () => {
    beforeEach(() => {
      cy.visit(`${functionalityPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Functionality');
    });

    it('should create an instance of Functionality', () => {
      cy.setFieldImageAsBytesOfEntity('image', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="status"]`).select('OUT_OF_CREDITS');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        functionality = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', functionalityPageUrlPattern);
    });
  });
});
