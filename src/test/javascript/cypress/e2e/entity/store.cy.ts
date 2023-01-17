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

describe('Store e2e test', () => {
  const storePageUrl = '/store';
  const storePageUrlPattern = new RegExp('/store(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const storeSample = {
    code: 'parse USB Bedfordshire',
    location: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=',
    locationContentType: 'unknown',
    name: 'Dauphine c',
    phone: '0107624813',
    notificationEmail: 'c Pound',
  };

  let store;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/stores+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/stores').as('postEntityRequest');
    cy.intercept('DELETE', '/api/stores/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (store) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/stores/${store.id}`,
      }).then(() => {
        store = undefined;
      });
    }
  });

  it('Stores menu should load Stores page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('store');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Store').should('exist');
    cy.url().should('match', storePageUrlPattern);
  });

  describe('Store page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(storePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Store page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/store/new$'));
        cy.getEntityCreateUpdateHeading('Store');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', storePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/stores',
          body: storeSample,
        }).then(({ body }) => {
          store = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/stores+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/stores?page=0&size=20>; rel="last",<http://localhost/api/stores?page=0&size=20>; rel="first"',
              },
              body: [store],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(storePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Store page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('store');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', storePageUrlPattern);
      });

      it('edit button click should load edit Store page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Store');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', storePageUrlPattern);
      });

      it('edit button click should load edit Store page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Store');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', storePageUrlPattern);
      });

      it('last delete button click should delete instance of Store', () => {
        cy.intercept('GET', '/api/stores/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('store').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', storePageUrlPattern);

        store = undefined;
      });
    });
  });

  describe('new Store page', () => {
    beforeEach(() => {
      cy.visit(`${storePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Store');
    });

    it('should create an instance of Store', () => {
      cy.get(`[data-cy="code"]`).type('streamline').should('have.value', 'streamline');

      cy.setFieldImageAsBytesOfEntity('location', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="address"]`).type('Avon Account uniform').should('have.value', 'Avon Account uniform');

      cy.get(`[data-cy="name"]`).type('Robust de').should('have.value', 'Robust de');

      cy.get(`[data-cy="description"]`).type('program incubate overriding').should('have.value', 'program incubate overriding');

      cy.get(`[data-cy="currency"]`).select('USD');

      cy.get(`[data-cy="phone"]`).type('+33 405112631').should('have.value', '+33 405112631');

      cy.get(`[data-cy="notificationEmail"]`).type('parse Account Handcrafted').should('have.value', 'parse Account Handcrafted');

      cy.get(`[data-cy="status"]`).select('CLOSED');

      cy.get(`[data-cy="aboutUs"]`)
        .type('../fake-data/blob/hipster.txt')
        .invoke('val')
        .should('match', new RegExp('../fake-data/blob/hipster.txt'));

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        store = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', storePageUrlPattern);
    });
  });
});
