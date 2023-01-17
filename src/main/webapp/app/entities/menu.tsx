import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/functionality">
        <Translate contentKey="global.menu.entities.functionality" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/functionality-category">
        <Translate contentKey="global.menu.entities.functionalityCategory" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/transac">
        <Translate contentKey="global.menu.entities.transac" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/mobile-banking-actor">
        <Translate contentKey="global.menu.entities.mobileBankingActor" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/supply-request">
        <Translate contentKey="global.menu.entities.supplyRequest" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/supply">
        <Translate contentKey="global.menu.entities.supply" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/payment">
        <Translate contentKey="global.menu.entities.payment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/payment-method">
        <Translate contentKey="global.menu.entities.paymentMethod" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/event">
        <Translate contentKey="global.menu.entities.event" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/notification">
        <Translate contentKey="global.menu.entities.notification" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/notification-settings">
        <Translate contentKey="global.menu.entities.notificationSettings" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/ticket">
        <Translate contentKey="global.menu.entities.ticket" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/ticket-delivery">
        <Translate contentKey="global.menu.entities.ticketDelivery" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/ticket-delivery-method">
        <Translate contentKey="global.menu.entities.ticketDeliveryMethod" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/store">
        <Translate contentKey="global.menu.entities.store" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/insurance-and-micro-credits-actor">
        <Translate contentKey="global.menu.entities.insuranceAndMicroCreditsActor" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/insurance-and-micro-credits-contribution">
        <Translate contentKey="global.menu.entities.insuranceAndMicroCreditsContribution" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/admin-network">
        <Translate contentKey="global.menu.entities.adminNetwork" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/intermediate-agent">
        <Translate contentKey="global.menu.entities.intermediateAgent" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/final-agent">
        <Translate contentKey="global.menu.entities.finalAgent" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/country">
        <Translate contentKey="global.menu.entities.country" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/region">
        <Translate contentKey="global.menu.entities.region" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/department">
        <Translate contentKey="global.menu.entities.department" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/town">
        <Translate contentKey="global.menu.entities.town" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/zone">
        <Translate contentKey="global.menu.entities.zone" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
