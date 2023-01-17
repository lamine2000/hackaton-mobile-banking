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

describe('FunctionalityCategory e2e test', () => {
  const functionalityCategoryPageUrl = '/functionality-category';
  const functionalityCategoryPageUrlPattern = new RegExp('/functionality-category(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const functionalityCategorySample = {
    logo: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=',
    logoContentType: 'unknown',
    status: 'UNAVAILABLE',
  };

  let functionalityCategory;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/functionality-categories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/functionality-categories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/functionality-categories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (functionalityCategory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/functionality-categories/${functionalityCategory.id}`,
      }).then(() => {
        functionalityCategory = undefined;
      });
    }
  });

  it('FunctionalityCategories menu should load FunctionalityCategories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('functionality-category');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('FunctionalityCategory').should('exist');
    cy.url().should('match', functionalityCategoryPageUrlPattern);
  });

  describe('FunctionalityCategory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(functionalityCategoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create FunctionalityCategory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/functionality-category/new$'));
        cy.getEntityCreateUpdateHeading('FunctionalityCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', functionalityCategoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/functionality-categories',
          body: functionalityCategorySample,
        }).then(({ body }) => {
          functionalityCategory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/functionality-categories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/functionality-categories?page=0&size=20>; rel="last",<http://localhost/api/functionality-categories?page=0&size=20>; rel="first"',
              },
              body: [functionalityCategory],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(functionalityCategoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details FunctionalityCategory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('functionalityCategory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', functionalityCategoryPageUrlPattern);
      });

      it('edit button click should load edit FunctionalityCategory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FunctionalityCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', functionalityCategoryPageUrlPattern);
      });

      it('edit button click should load edit FunctionalityCategory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FunctionalityCategory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', functionalityCategoryPageUrlPattern);
      });

      it('last delete button click should delete instance of FunctionalityCategory', () => {
        cy.intercept('GET', '/api/functionality-categories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('functionalityCategory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', functionalityCategoryPageUrlPattern);

        functionalityCategory = undefined;
      });
    });
  });

  describe('new FunctionalityCategory page', () => {
    beforeEach(() => {
      cy.visit(`${functionalityCategoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('FunctionalityCategory');
    });

    it('should create an instance of FunctionalityCategory', () => {
      cy.setFieldImageAsBytesOfEntity('logo', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="status"]`).select('UNAVAILABLE');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        functionalityCategory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', functionalityCategoryPageUrlPattern);
    });
  });
});
