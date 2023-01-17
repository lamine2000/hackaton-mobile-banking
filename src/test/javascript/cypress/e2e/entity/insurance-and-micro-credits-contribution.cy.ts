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

describe('InsuranceAndMicroCreditsContribution e2e test', () => {
  const insuranceAndMicroCreditsContributionPageUrl = '/insurance-and-micro-credits-contribution';
  const insuranceAndMicroCreditsContributionPageUrlPattern = new RegExp('/insurance-and-micro-credits-contribution(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const insuranceAndMicroCreditsContributionSample = { code: 'directional Stagiaire Leu' };

  let insuranceAndMicroCreditsContribution;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/insurance-and-micro-credits-contributions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/insurance-and-micro-credits-contributions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/insurance-and-micro-credits-contributions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (insuranceAndMicroCreditsContribution) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/insurance-and-micro-credits-contributions/${insuranceAndMicroCreditsContribution.id}`,
      }).then(() => {
        insuranceAndMicroCreditsContribution = undefined;
      });
    }
  });

  it('InsuranceAndMicroCreditsContributions menu should load InsuranceAndMicroCreditsContributions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('insurance-and-micro-credits-contribution');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('InsuranceAndMicroCreditsContribution').should('exist');
    cy.url().should('match', insuranceAndMicroCreditsContributionPageUrlPattern);
  });

  describe('InsuranceAndMicroCreditsContribution page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(insuranceAndMicroCreditsContributionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create InsuranceAndMicroCreditsContribution page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/insurance-and-micro-credits-contribution/new$'));
        cy.getEntityCreateUpdateHeading('InsuranceAndMicroCreditsContribution');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', insuranceAndMicroCreditsContributionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/insurance-and-micro-credits-contributions',
          body: insuranceAndMicroCreditsContributionSample,
        }).then(({ body }) => {
          insuranceAndMicroCreditsContribution = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/insurance-and-micro-credits-contributions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/insurance-and-micro-credits-contributions?page=0&size=20>; rel="last",<http://localhost/api/insurance-and-micro-credits-contributions?page=0&size=20>; rel="first"',
              },
              body: [insuranceAndMicroCreditsContribution],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(insuranceAndMicroCreditsContributionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details InsuranceAndMicroCreditsContribution page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('insuranceAndMicroCreditsContribution');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', insuranceAndMicroCreditsContributionPageUrlPattern);
      });

      it('edit button click should load edit InsuranceAndMicroCreditsContribution page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InsuranceAndMicroCreditsContribution');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', insuranceAndMicroCreditsContributionPageUrlPattern);
      });

      it('edit button click should load edit InsuranceAndMicroCreditsContribution page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('InsuranceAndMicroCreditsContribution');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', insuranceAndMicroCreditsContributionPageUrlPattern);
      });

      it('last delete button click should delete instance of InsuranceAndMicroCreditsContribution', () => {
        cy.intercept('GET', '/api/insurance-and-micro-credits-contributions/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('insuranceAndMicroCreditsContribution').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', insuranceAndMicroCreditsContributionPageUrlPattern);

        insuranceAndMicroCreditsContribution = undefined;
      });
    });
  });

  describe('new InsuranceAndMicroCreditsContribution page', () => {
    beforeEach(() => {
      cy.visit(`${insuranceAndMicroCreditsContributionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('InsuranceAndMicroCreditsContribution');
    });

    it('should create an instance of InsuranceAndMicroCreditsContribution', () => {
      cy.get(`[data-cy="code"]`).type('Open-source').should('have.value', 'Open-source');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        insuranceAndMicroCreditsContribution = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', insuranceAndMicroCreditsContributionPageUrlPattern);
    });
  });
});
