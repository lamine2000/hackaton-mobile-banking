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

describe('IntermediateAgent e2e test', () => {
  const intermediateAgentPageUrl = '/intermediate-agent';
  const intermediateAgentPageUrlPattern = new RegExp('/intermediate-agent(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const intermediateAgentSample = {
    firstName: 'Brieuc',
    lastName: 'Gauthier',
    email: 'Astart34@yahoo.fr',
    phone: '+33 143757518',
    addressLine1: 'cross-platform',
    city: 'Mauriceview',
    status: 'INACTIVE',
    commissionRate: 85372,
  };

  let intermediateAgent;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/intermediate-agents+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/intermediate-agents').as('postEntityRequest');
    cy.intercept('DELETE', '/api/intermediate-agents/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (intermediateAgent) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/intermediate-agents/${intermediateAgent.id}`,
      }).then(() => {
        intermediateAgent = undefined;
      });
    }
  });

  it('IntermediateAgents menu should load IntermediateAgents page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('intermediate-agent');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('IntermediateAgent').should('exist');
    cy.url().should('match', intermediateAgentPageUrlPattern);
  });

  describe('IntermediateAgent page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(intermediateAgentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create IntermediateAgent page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/intermediate-agent/new$'));
        cy.getEntityCreateUpdateHeading('IntermediateAgent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', intermediateAgentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/intermediate-agents',
          body: intermediateAgentSample,
        }).then(({ body }) => {
          intermediateAgent = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/intermediate-agents+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/intermediate-agents?page=0&size=20>; rel="last",<http://localhost/api/intermediate-agents?page=0&size=20>; rel="first"',
              },
              body: [intermediateAgent],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(intermediateAgentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details IntermediateAgent page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('intermediateAgent');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', intermediateAgentPageUrlPattern);
      });

      it('edit button click should load edit IntermediateAgent page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('IntermediateAgent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', intermediateAgentPageUrlPattern);
      });

      it('edit button click should load edit IntermediateAgent page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('IntermediateAgent');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', intermediateAgentPageUrlPattern);
      });

      it('last delete button click should delete instance of IntermediateAgent', () => {
        cy.intercept('GET', '/api/intermediate-agents/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('intermediateAgent').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', intermediateAgentPageUrlPattern);

        intermediateAgent = undefined;
      });
    });
  });

  describe('new IntermediateAgent page', () => {
    beforeEach(() => {
      cy.visit(`${intermediateAgentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('IntermediateAgent');
    });

    it('should create an instance of IntermediateAgent', () => {
      cy.get(`[data-cy="firstName"]`).type('Florie').should('have.value', 'Florie');

      cy.get(`[data-cy="lastName"]`).type('Gauthier').should('have.value', 'Gauthier');

      cy.get(`[data-cy="email"]`).type('Hlier_Garcia32@hotmail.fr').should('have.value', 'Hlier_Garcia32@hotmail.fr');

      cy.get(`[data-cy="phone"]`).type('0487225737').should('have.value', '0487225737');

      cy.get(`[data-cy="addressLine1"]`).type('la').should('have.value', 'la');

      cy.get(`[data-cy="addressLine2"]`).type('calculate Guyana').should('have.value', 'calculate Guyana');

      cy.get(`[data-cy="city"]`).type('East Anicéebury').should('have.value', 'East Anicéebury');

      cy.get(`[data-cy="status"]`).select('PENDING');

      cy.get(`[data-cy="commissionRate"]`).type('72022').should('have.value', '72022');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        intermediateAgent = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', intermediateAgentPageUrlPattern);
    });
  });
});
