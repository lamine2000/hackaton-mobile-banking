import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './mobile-banking-actor.reducer';

export const MobileBankingActorDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const mobileBankingActorEntity = useAppSelector(state => state.mobileBankingActor.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="mobileBankingActorDetailsHeading">
          <Translate contentKey="hackaton3App.mobileBankingActor.detail.title">MobileBankingActor</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{mobileBankingActorEntity.id}</dd>
          <dt>
            <span id="logo">
              <Translate contentKey="hackaton3App.mobileBankingActor.logo">Logo</Translate>
            </span>
          </dt>
          <dd>
            {mobileBankingActorEntity.logo ? (
              <div>
                {mobileBankingActorEntity.logoContentType ? (
                  <a onClick={openFile(mobileBankingActorEntity.logoContentType, mobileBankingActorEntity.logo)}>
                    <img
                      src={`data:${mobileBankingActorEntity.logoContentType};base64,${mobileBankingActorEntity.logo}`}
                      style={{ maxHeight: '30px' }}
                    />
                  </a>
                ) : null}
                <span>
                  {mobileBankingActorEntity.logoContentType}, {byteSize(mobileBankingActorEntity.logo)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="name">
              <Translate contentKey="hackaton3App.mobileBankingActor.name">Name</Translate>
            </span>
          </dt>
          <dd>{mobileBankingActorEntity.name}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="hackaton3App.mobileBankingActor.status">Status</Translate>
            </span>
          </dt>
          <dd>{mobileBankingActorEntity.status}</dd>
          <dt>
            <Translate contentKey="hackaton3App.mobileBankingActor.functionality">Functionality</Translate>
          </dt>
          <dd>
            {mobileBankingActorEntity.functionalities
              ? mobileBankingActorEntity.functionalities.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {mobileBankingActorEntity.functionalities && i === mobileBankingActorEntity.functionalities.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/mobile-banking-actor" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/mobile-banking-actor/${mobileBankingActorEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MobileBankingActorDetail;
