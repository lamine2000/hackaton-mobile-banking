import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './supply.reducer';

export const SupplyDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const supplyEntity = useAppSelector(state => state.supply.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="supplyDetailsHeading">
          <Translate contentKey="hackaton3App.supply.detail.title">Supply</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{supplyEntity.id}</dd>
          <dt>
            <span id="receiver">
              <Translate contentKey="hackaton3App.supply.receiver">Receiver</Translate>
            </span>
          </dt>
          <dd>{supplyEntity.receiver}</dd>
          <dt>
            <Translate contentKey="hackaton3App.supply.supplyRequest">Supply Request</Translate>
          </dt>
          <dd>{supplyEntity.supplyRequest ? supplyEntity.supplyRequest.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/supply" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/supply/${supplyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SupplyDetail;
