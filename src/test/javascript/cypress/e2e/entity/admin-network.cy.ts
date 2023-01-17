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

describe('AdminNetwork e2e test', () => {
  const adminNetworkPageUrl = '/admin-network';
  const adminNetworkPageUrlPattern = new RegExp('/admin-network(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const adminNetworkSample = {
    firstName: 'Antonin',
    lastName: 'Fernandez',
    email: 'Axel.Charpentier46@gmail.com',
    phone: '0254845854',
    addressLine1: 'Awesome initiatives b',
    city: 'Saint-Maur-des-Fossés',
    status: 'INACTIVE',
    commissionRate: 46078,
  };

  let adminNetwork;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/admin-networks+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/admin-networks').as('postEntityRequest');
    cy.intercept('DELETE', '/api/admin-networks/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (adminNetwork) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/admin-networks/${adminNetwork.id}`,
      }).then(() => {
        adminNetwork = undefined;
      });
    }
  });

  it('AdminNetworks menu should load AdminNetworks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('admin-network');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AdminNetwork').should('exist');
    cy.url().should('match', adminNetworkPageUrlPattern);
  });

  describe('AdminNetwork page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(adminNetworkPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AdminNetwork page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/admin-network/new$'));
        cy.getEntityCreateUpdateHeading('AdminNetwork');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', adminNetworkPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/admin-networks',
          body: adminNetworkSample,
        }).then(({ body }) => {
          adminNetwork = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/admin-networks+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/admin-networks?page=0&size=20>; rel="last",<http://localhost/api/admin-networks?page=0&size=20>; rel="first"',
              },
              body: [adminNetwork],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(adminNetworkPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details AdminNetwork page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('adminNetwork');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', adminNetworkPageUrlPattern);
      });

      it('edit button click should load edit AdminNetwork page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AdminNetwork');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', adminNetworkPageUrlPattern);
      });

      it('edit button click should load edit AdminNetwork page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AdminNetwork');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', adminNetworkPageUrlPattern);
      });

      it('last delete button click should delete instance of AdminNetwork', () => {
        cy.intercept('GET', '/api/admin-networks/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('adminNetwork').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', adminNetworkPageUrlPattern);

        adminNetwork = undefined;
      });
    });
  });

  describe('new AdminNetwork page', () => {
    beforeEach(() => {
      cy.visit(`${adminNetworkPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AdminNetwork');
    });

    it('should create an instance of AdminNetwork', () => {
      cy.get(`[data-cy="firstName"]`).type('Angadrême').should('have.value', 'Angadrême');

      cy.get(`[data-cy="lastName"]`).type('Marie').should('have.value', 'Marie');

      cy.get(`[data-cy="email"]`).type('Angadrme79@yahoo.fr').should('have.value', 'Angadrme79@yahoo.fr');

      cy.get(`[data-cy="phone"]`).type('+33 787918015').should('have.value', '+33 787918015');

      cy.get(`[data-cy="addressLine1"]`).type('mint wireless Computer').should('have.value', 'mint wireless Computer');

      cy.get(`[data-cy="addressLine2"]`).type('de Chili').should('have.value', 'de Chili');

      cy.get(`[data-cy="city"]`).type('Le Tampon').should('have.value', 'Le Tampon');

      cy.get(`[data-cy="status"]`).select('ACTIVE');

      cy.get(`[data-cy="commissionRate"]`).type('87601').should('have.value', '87601');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        adminNetwork = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', adminNetworkPageUrlPattern);
    });
  });
});
