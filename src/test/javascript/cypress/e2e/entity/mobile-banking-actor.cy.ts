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

describe('MobileBankingActor e2e test', () => {
  const mobileBankingActorPageUrl = '/mobile-banking-actor';
  const mobileBankingActorPageUrlPattern = new RegExp('/mobile-banking-actor(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mobileBankingActorSample = {
    logo: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=',
    logoContentType: 'unknown',
    name: 'b productivity',
    status: 'OUT_OF_CREDITS',
  };

  let mobileBankingActor;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mobile-banking-actors+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mobile-banking-actors').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mobile-banking-actors/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mobileBankingActor) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mobile-banking-actors/${mobileBankingActor.id}`,
      }).then(() => {
        mobileBankingActor = undefined;
      });
    }
  });

  it('MobileBankingActors menu should load MobileBankingActors page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mobile-banking-actor');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MobileBankingActor').should('exist');
    cy.url().should('match', mobileBankingActorPageUrlPattern);
  });

  describe('MobileBankingActor page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mobileBankingActorPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MobileBankingActor page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mobile-banking-actor/new$'));
        cy.getEntityCreateUpdateHeading('MobileBankingActor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', mobileBankingActorPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mobile-banking-actors',
          body: mobileBankingActorSample,
        }).then(({ body }) => {
          mobileBankingActor = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mobile-banking-actors+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mobile-banking-actors?page=0&size=20>; rel="last",<http://localhost/api/mobile-banking-actors?page=0&size=20>; rel="first"',
              },
              body: [mobileBankingActor],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(mobileBankingActorPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MobileBankingActor page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mobileBankingActor');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', mobileBankingActorPageUrlPattern);
      });

      it('edit button click should load edit MobileBankingActor page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MobileBankingActor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', mobileBankingActorPageUrlPattern);
      });

      it('edit button click should load edit MobileBankingActor page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MobileBankingActor');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', mobileBankingActorPageUrlPattern);
      });

      it('last delete button click should delete instance of MobileBankingActor', () => {
        cy.intercept('GET', '/api/mobile-banking-actors/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('mobileBankingActor').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', mobileBankingActorPageUrlPattern);

        mobileBankingActor = undefined;
      });
    });
  });

  describe('new MobileBankingActor page', () => {
    beforeEach(() => {
      cy.visit(`${mobileBankingActorPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MobileBankingActor');
    });

    it('should create an instance of MobileBankingActor', () => {
      cy.setFieldImageAsBytesOfEntity('logo', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="name"]`).type('back-end').should('have.value', 'back-end');

      cy.get(`[data-cy="status"]`).select('AVAILABLE');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        mobileBankingActor = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', mobileBankingActorPageUrlPattern);
    });
  });
});
