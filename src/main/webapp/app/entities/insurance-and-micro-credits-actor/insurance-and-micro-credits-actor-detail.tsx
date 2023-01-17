import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './insurance-and-micro-credits-actor.reducer';

export const InsuranceAndMicroCreditsActorDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const insuranceAndMicroCreditsActorEntity = useAppSelector(state => state.insuranceAndMicroCreditsActor.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="insuranceAndMicroCreditsActorDetailsHeading">
          <Translate contentKey="hackaton3App.insuranceAndMicroCreditsActor.detail.title">InsuranceAndMicroCreditsActor</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{insuranceAndMicroCreditsActorEntity.id}</dd>
          <dt>
            <span id="logo">
              <Translate contentKey="hackaton3App.insuranceAndMicroCreditsActor.logo">Logo</Translate>
            </span>
          </dt>
          <dd>
            {insuranceAndMicroCreditsActorEntity.logo ? (
              <div>
                {insuranceAndMicroCreditsActorEntity.logoContentType ? (
                  <a onClick={openFile(insuranceAndMicroCreditsActorEntity.logoContentType, insuranceAndMicroCreditsActorEntity.logo)}>
                    <img
                      src={`data:${insuranceAndMicroCreditsActorEntity.logoContentType};base64,${insuranceAndMicroCreditsActorEntity.logo}`}
                      style={{ maxHeight: '30px' }}
                    />
                  </a>
                ) : null}
                <span>
                  {insuranceAndMicroCreditsActorEntity.logoContentType}, {byteSize(insuranceAndMicroCreditsActorEntity.logo)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="name">
              <Translate contentKey="hackaton3App.insuranceAndMicroCreditsActor.name">Name</Translate>
            </span>
          </dt>
          <dd>{insuranceAndMicroCreditsActorEntity.name}</dd>
          <dt>
            <span id="acronym">
              <Translate contentKey="hackaton3App.insuranceAndMicroCreditsActor.acronym">Acronym</Translate>
            </span>
          </dt>
          <dd>{insuranceAndMicroCreditsActorEntity.acronym}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="hackaton3App.insuranceAndMicroCreditsActor.description">Description</Translate>
            </span>
          </dt>
          <dd>{insuranceAndMicroCreditsActorEntity.description}</dd>
        </dl>
        <Button tag={Link} to="/insurance-and-micro-credits-actor" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/insurance-and-micro-credits-actor/${insuranceAndMicroCreditsActorEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default InsuranceAndMicroCreditsActorDetail;
