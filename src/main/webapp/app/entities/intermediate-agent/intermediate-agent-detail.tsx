import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './intermediate-agent.reducer';

export const IntermediateAgentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const intermediateAgentEntity = useAppSelector(state => state.intermediateAgent.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="intermediateAgentDetailsHeading">
          <Translate contentKey="hackaton3App.intermediateAgent.detail.title">IntermediateAgent</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{intermediateAgentEntity.id}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="hackaton3App.intermediateAgent.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{intermediateAgentEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="hackaton3App.intermediateAgent.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{intermediateAgentEntity.lastName}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="hackaton3App.intermediateAgent.email">Email</Translate>
            </span>
          </dt>
          <dd>{intermediateAgentEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="hackaton3App.intermediateAgent.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{intermediateAgentEntity.phone}</dd>
          <dt>
            <span id="addressLine1">
              <Translate contentKey="hackaton3App.intermediateAgent.addressLine1">Address Line 1</Translate>
            </span>
          </dt>
          <dd>{intermediateAgentEntity.addressLine1}</dd>
          <dt>
            <span id="addressLine2">
              <Translate contentKey="hackaton3App.intermediateAgent.addressLine2">Address Line 2</Translate>
            </span>
          </dt>
          <dd>{intermediateAgentEntity.addressLine2}</dd>
          <dt>
            <span id="city">
              <Translate contentKey="hackaton3App.intermediateAgent.city">City</Translate>
            </span>
          </dt>
          <dd>{intermediateAgentEntity.city}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="hackaton3App.intermediateAgent.status">Status</Translate>
            </span>
          </dt>
          <dd>{intermediateAgentEntity.status}</dd>
          <dt>
            <span id="commissionRate">
              <Translate contentKey="hackaton3App.intermediateAgent.commissionRate">Commission Rate</Translate>
            </span>
          </dt>
          <dd>{intermediateAgentEntity.commissionRate}</dd>
          <dt>
            <Translate contentKey="hackaton3App.intermediateAgent.user">User</Translate>
          </dt>
          <dd>{intermediateAgentEntity.user ? intermediateAgentEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="hackaton3App.intermediateAgent.store">Store</Translate>
          </dt>
          <dd>{intermediateAgentEntity.store ? intermediateAgentEntity.store.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/intermediate-agent" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/intermediate-agent/${intermediateAgentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default IntermediateAgentDetail;
