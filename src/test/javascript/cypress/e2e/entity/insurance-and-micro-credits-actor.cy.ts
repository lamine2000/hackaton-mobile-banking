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

describe('InsuranceAndMicroCreditsActor e2e test', () => {
  const insuranceAndMicroCreditsActorPageUrl = '/insurance-and-micro-credits-actor';
  const insuranceAndMicroCreditsActorPageUrlPattern = new RegExp('/insurance-and-micro-credits-actor(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const insuranceAndMicroCreditsActorSample = {
    logo: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=',
    logoContentType: 'unknown',
    name: 'maximized wireless Concrete',
  };

  let insuranceAndMicroCreditsActor;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/insurance-and-micro-credits-actors+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/insurance-and-micro-credits-actors').as('postEntityRequest');
    cy.intercept('DELETE', '/api/insurance-and-micro-credits-actors/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (insuranceAndMicroCreditsActor) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/insurance-and-micro-credits-actors/${insuranceAndMicroCreditsActor.id}`,
      }).then(() => {
        insuranceAndMicroCreditsActor = undefined;
      });
    }
  });

  it('InsuranceAndMicroCreditsActors menu should load InsuranceAndMicroCreditsActors page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('insurance-and-micro-credits-actor');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('InsuranceAndMicroCreditsActor').should('exist');
    cy.url().should('match', insuranceAndMicroCreditsActorPageUrlPattern);
  });

  describe('InsuranceAndMicroCreditsActor page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(insuranceAndMicroCreditsActorPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create InsuranceAndMicroCreditsActor page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/insurance-and-micro-credits-actor/new$'));
        cy.getEntityCreateUpdateHeading('InsuranceAndMicroCreditsActor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', insuranceAndMicroCreditsActorPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/insurance-and-micro-credits-actors',
          body: insuranceAndMicroCreditsActorSample,
        }).then(({ body }) => {
          insuranceAndMicroCreditsActor = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/insurance-and-micro-credits-actors+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/insurance-and-micro-credits-actors?page=0&size=20>; rel="last",<http://localhost/api/insurance-and-micro-credits-actors?page=0&size=20>; rel="first"',
              },
              body: [insuranceAndMicroCreditsActor],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(insuranceAndMicroCreditsActorPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details InsuranceAndMicroCreditsActor page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('insuranceAndMicroCreditsActor');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', insuranceAndMicroCreditsActorPageUrlPattern);
      });

      it('edit button click should load edit InsuranceAndMicroCreditsActor page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InsuranceAndMicroCreditsActor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', insuranceAndMicroCreditsActorPageUrlPattern);
      });

      it('edit button click should load edit InsuranceAndMicroCreditsActor page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InsuranceAndMicroCreditsActor');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', insuranceAndMicroCreditsActorPageUrlPattern);
      });

      it('last delete button click should delete instance of InsuranceAndMicroCreditsActor', () => {
        cy.intercept('GET', '/api/insurance-and-micro-credits-actors/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('insuranceAndMicroCreditsActor').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', insuranceAndMicroCreditsActorPageUrlPattern);

        insuranceAndMicroCreditsActor = undefined;
      });
    });
  });

  describe('new InsuranceAndMicroCreditsActor page', () => {
    beforeEach(() => {
      cy.visit(`${insuranceAndMicroCreditsActorPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('InsuranceAndMicroCreditsActor');
    });

    it('should create an instance of InsuranceAndMicroCreditsActor', () => {
      cy.setFieldImageAsBytesOfEntity('logo', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="name"]`).type('demand-driven Computers').should('have.value', 'demand-driven Computers');

      cy.get(`[data-cy="acronym"]`).type('Sausages Berkshire').should('have.value', 'Sausages Berkshire');

      cy.get(`[data-cy="description"]`)
        .type('../fake-data/blob/hipster.txt')
        .invoke('val')
        .should('match', new RegExp('../fake-data/blob/hipster.txt'));

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        insuranceAndMicroCreditsActor = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', insuranceAndMicroCreditsActorPageUrlPattern);
    });
  });
});
