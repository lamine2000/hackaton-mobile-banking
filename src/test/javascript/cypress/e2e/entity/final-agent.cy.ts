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

describe('FinalAgent e2e test', () => {
  const finalAgentPageUrl = '/final-agent';
  const finalAgentPageUrlPattern = new RegExp('/final-agent(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const finalAgentSample = {
    firstName: 'Lorrain',
    lastName: 'Picard',
    email: 'Cleste27@yahoo.fr',
    phone: '0681809951',
    addressLine1: 'payment',
    city: 'Yoannton',
    status: 'INACTIVE',
    commissionRate: 61929,
  };

  let finalAgent;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/final-agents+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/final-agents').as('postEntityRequest');
    cy.intercept('DELETE', '/api/final-agents/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (finalAgent) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/final-agents/${finalAgent.id}`,
      }).then(() => {
        finalAgent = undefined;
      });
    }
  });

  it('FinalAgents menu should load FinalAgents page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('final-agent');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('FinalAgent').should('exist');
    cy.url().should('match', finalAgentPageUrlPattern);
  });

  describe('FinalAgent page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(finalAgentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create FinalAgent page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/final-agent/new$'));
        cy.getEntityCreateUpdateHeading('FinalAgent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', finalAgentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/final-agents',
          body: finalAgentSample,
        }).then(({ body }) => {
          finalAgent = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/final-agents+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/final-agents?page=0&size=20>; rel="last",<http://localhost/api/final-agents?page=0&size=20>; rel="first"',
              },
              body: [finalAgent],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(finalAgentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details FinalAgent page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('finalAgent');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', finalAgentPageUrlPattern);
      });

      it('edit button click should load edit FinalAgent page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FinalAgent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', finalAgentPageUrlPattern);
      });

      it('edit button click should load edit FinalAgent page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FinalAgent');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', finalAgentPageUrlPattern);
      });

      it('last delete button click should delete instance of FinalAgent', () => {
        cy.intercept('GET', '/api/final-agents/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('finalAgent').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', finalAgentPageUrlPattern);

        finalAgent = undefined;
      });
    });
  });

  describe('new FinalAgent page', () => {
    beforeEach(() => {
      cy.visit(`${finalAgentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('FinalAgent');
    });

    it('should create an instance of FinalAgent', () => {
      cy.get(`[data-cy="firstName"]`).type('Bérangère').should('have.value', 'Bérangère');

      cy.get(`[data-cy="lastName"]`).type('Charles').should('have.value', 'Charles');

      cy.get(`[data-cy="email"]`).type('Ren6@hotmail.fr').should('have.value', 'Ren6@hotmail.fr');

      cy.get(`[data-cy="phone"]`).type('0628623809').should('have.value', '0628623809');

      cy.get(`[data-cy="addressLine1"]`).type('Borders Jewelery Bulgarie').should('have.value', 'Borders Jewelery Bulgarie');

      cy.get(`[data-cy="addressLine2"]`).type('Chicken driver').should('have.value', 'Chicken driver');

      cy.get(`[data-cy="city"]`).type('Mulhouse').should('have.value', 'Mulhouse');

      cy.get(`[data-cy="status"]`).select('ACTIVE');

      cy.get(`[data-cy="commissionRate"]`).type('84225').should('have.value', '84225');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        finalAgent = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', finalAgentPageUrlPattern);
    });
  });
});
