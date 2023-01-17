import functionality from 'app/entities/functionality/functionality.reducer';
import functionalityCategory from 'app/entities/functionality-category/functionality-category.reducer';
import transac from 'app/entities/transac/transac.reducer';
import mobileBankingActor from 'app/entities/mobile-banking-actor/mobile-banking-actor.reducer';
import supplyRequest from 'app/entities/supply-request/supply-request.reducer';
import supply from 'app/entities/supply/supply.reducer';
import payment from 'app/entities/payment/payment.reducer';
import paymentMethod from 'app/entities/payment-method/payment-method.reducer';
import event from 'app/entities/event/event.reducer';
import notification from 'app/entities/notification/notification.reducer';
import notificationSettings from 'app/entities/notification-settings/notification-settings.reducer';
import ticket from 'app/entities/ticket/ticket.reducer';
import ticketDelivery from 'app/entities/ticket-delivery/ticket-delivery.reducer';
import ticketDeliveryMethod from 'app/entities/ticket-delivery-method/ticket-delivery-method.reducer';
import store from 'app/entities/store/store.reducer';
import insuranceAndMicroCreditsActor from 'app/entities/insurance-and-micro-credits-actor/insurance-and-micro-credits-actor.reducer';
import insuranceAndMicroCreditsContribution from 'app/entities/insurance-and-micro-credits-contribution/insurance-and-micro-credits-contribution.reducer';
import adminNetwork from 'app/entities/admin-network/admin-network.reducer';
import intermediateAgent from 'app/entities/intermediate-agent/intermediate-agent.reducer';
import finalAgent from 'app/entities/final-agent/final-agent.reducer';
import country from 'app/entities/country/country.reducer';
import region from 'app/entities/region/region.reducer';
import department from 'app/entities/department/department.reducer';
import town from 'app/entities/town/town.reducer';
import zone from 'app/entities/zone/zone.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  functionality,
  functionalityCategory,
  transac,
  mobileBankingActor,
  supplyRequest,
  supply,
  payment,
  paymentMethod,
  event,
  notification,
  notificationSettings,
  ticket,
  ticketDelivery,
  ticketDeliveryMethod,
  store,
  insuranceAndMicroCreditsActor,
  insuranceAndMicroCreditsContribution,
  adminNetwork,
  intermediateAgent,
  finalAgent,
  country,
  region,
  department,
  town,
  zone,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
