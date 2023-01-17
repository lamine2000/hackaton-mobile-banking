import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './final-agent.reducer';

export const FinalAgentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const finalAgentEntity = useAppSelector(state => state.finalAgent.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="finalAgentDetailsHeading">
          <Translate contentKey="hackaton3App.finalAgent.detail.title">FinalAgent</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{finalAgentEntity.id}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="hackaton3App.finalAgent.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{finalAgentEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="hackaton3App.finalAgent.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{finalAgentEntity.lastName}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="hackaton3App.finalAgent.email">Email</Translate>
            </span>
          </dt>
          <dd>{finalAgentEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="hackaton3App.finalAgent.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{finalAgentEntity.phone}</dd>
          <dt>
            <span id="addressLine1">
              <Translate contentKey="hackaton3App.finalAgent.addressLine1">Address Line 1</Translate>
            </span>
          </dt>
          <dd>{finalAgentEntity.addressLine1}</dd>
          <dt>
            <span id="addressLine2">
              <Translate contentKey="hackaton3App.finalAgent.addressLine2">Address Line 2</Translate>
            </span>
          </dt>
          <dd>{finalAgentEntity.addressLine2}</dd>
          <dt>
            <span id="city">
              <Translate contentKey="hackaton3App.finalAgent.city">City</Translate>
            </span>
          </dt>
          <dd>{finalAgentEntity.city}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="hackaton3App.finalAgent.status">Status</Translate>
            </span>
          </dt>
          <dd>{finalAgentEntity.status}</dd>
          <dt>
            <span id="commissionRate">
              <Translate contentKey="hackaton3App.finalAgent.commissionRate">Commission Rate</Translate>
            </span>
          </dt>
          <dd>{finalAgentEntity.commissionRate}</dd>
          <dt>
            <Translate contentKey="hackaton3App.finalAgent.user">User</Translate>
          </dt>
          <dd>{finalAgentEntity.user ? finalAgentEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="hackaton3App.finalAgent.store">Store</Translate>
          </dt>
          <dd>{finalAgentEntity.store ? finalAgentEntity.store.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/final-agent" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/final-agent/${finalAgentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FinalAgentDetail;
