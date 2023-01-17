import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './transac.reducer';

export const TransacDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const transacEntity = useAppSelector(state => state.transac.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="transacDetailsHeading">
          <Translate contentKey="hackaton3App.transac.detail.title">Transac</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{transacEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="hackaton3App.transac.code">Code</Translate>
            </span>
          </dt>
          <dd>{transacEntity.code}</dd>
          <dt>
            <span id="createdBy">
              <Translate contentKey="hackaton3App.transac.createdBy">Created By</Translate>
            </span>
          </dt>
          <dd>{transacEntity.createdBy}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="hackaton3App.transac.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{transacEntity.createdAt ? <TextFormat value={transacEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="receiver">
              <Translate contentKey="hackaton3App.transac.receiver">Receiver</Translate>
            </span>
          </dt>
          <dd>{transacEntity.receiver}</dd>
          <dt>
            <span id="sender">
              <Translate contentKey="hackaton3App.transac.sender">Sender</Translate>
            </span>
          </dt>
          <dd>{transacEntity.sender}</dd>
          <dt>
            <span id="amount">
              <Translate contentKey="hackaton3App.transac.amount">Amount</Translate>
            </span>
          </dt>
          <dd>{transacEntity.amount}</dd>
          <dt>
            <span id="currency">
              <Translate contentKey="hackaton3App.transac.currency">Currency</Translate>
            </span>
          </dt>
          <dd>{transacEntity.currency}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="hackaton3App.transac.type">Type</Translate>
            </span>
          </dt>
          <dd>{transacEntity.type}</dd>
        </dl>
        <Button tag={Link} to="/transac" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/transac/${transacEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TransacDetail;
