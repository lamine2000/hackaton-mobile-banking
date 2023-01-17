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

describe('Transac e2e test', () => {
  const transacPageUrl = '/transac';
  const transacPageUrlPattern = new RegExp('/transac(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const transacSample = {
    code: 'haptic eyeballs SCSI',
    createdBy: 'SQL Account Baby',
    createdAt: '2023-01-16T20:35:18.405Z',
    amount: 61491,
    currency: 'XOF',
    type: 'DEPOSIT',
  };

  let transac;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/transacs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/transacs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/transacs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (transac) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/transacs/${transac.id}`,
      }).then(() => {
        transac = undefined;
      });
    }
  });

  it('Transacs menu should load Transacs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('transac');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Transac').should('exist');
    cy.url().should('match', transacPageUrlPattern);
  });

  describe('Transac page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(transacPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Transac page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/transac/new$'));
        cy.getEntityCreateUpdateHeading('Transac');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', transacPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/transacs',
          body: transacSample,
        }).then(({ body }) => {
          transac = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/transacs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/transacs?page=0&size=20>; rel="last",<http://localhost/api/transacs?page=0&size=20>; rel="first"',
              },
              body: [transac],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(transacPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Transac page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('transac');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', transacPageUrlPattern);
      });

      it('edit button click should load edit Transac page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Transac');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', transacPageUrlPattern);
      });

      it('edit button click should load edit Transac page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Transac');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', transacPageUrlPattern);
      });

      it('last delete button click should delete instance of Transac', () => {
        cy.intercept('GET', '/api/transacs/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('transac').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', transacPageUrlPattern);

        transac = undefined;
      });
    });
  });

  describe('new Transac page', () => {
    beforeEach(() => {
      cy.visit(`${transacPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Transac');
    });

    it('should create an instance of Transac', () => {
      cy.get(`[data-cy="code"]`).type('Shirt').should('have.value', 'Shirt');

      cy.get(`[data-cy="createdBy"]`).type('Savings withdrawal Metal').should('have.value', 'Savings withdrawal Metal');

      cy.get(`[data-cy="createdAt"]`).type('2023-01-16T22:17').blur().should('have.value', '2023-01-16T22:17');

      cy.get(`[data-cy="receiver"]`).type('Soft payment maximize').should('have.value', 'Soft payment maximize');

      cy.get(`[data-cy="sender"]`).type('product base').should('have.value', 'product base');

      cy.get(`[data-cy="amount"]`).type('8072').should('have.value', '8072');

      cy.get(`[data-cy="currency"]`).select('XOF');

      cy.get(`[data-cy="type"]`).select('PAYMENT');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        transac = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', transacPageUrlPattern);
    });
  });
});
