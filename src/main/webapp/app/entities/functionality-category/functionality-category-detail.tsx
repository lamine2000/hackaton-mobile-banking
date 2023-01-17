import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './functionality-category.reducer';

export const FunctionalityCategoryDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const functionalityCategoryEntity = useAppSelector(state => state.functionalityCategory.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="functionalityCategoryDetailsHeading">
          <Translate contentKey="hackaton3App.functionalityCategory.detail.title">FunctionalityCategory</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{functionalityCategoryEntity.id}</dd>
          <dt>
            <span id="logo">
              <Translate contentKey="hackaton3App.functionalityCategory.logo">Logo</Translate>
            </span>
          </dt>
          <dd>
            {functionalityCategoryEntity.logo ? (
              <div>
                {functionalityCategoryEntity.logoContentType ? (
                  <a onClick={openFile(functionalityCategoryEntity.logoContentType, functionalityCategoryEntity.logo)}>
                    <img
                      src={`data:${functionalityCategoryEntity.logoContentType};base64,${functionalityCategoryEntity.logo}`}
                      style={{ maxHeight: '30px' }}
                    />
                  </a>
                ) : null}
                <span>
                  {functionalityCategoryEntity.logoContentType}, {byteSize(functionalityCategoryEntity.logo)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="hackaton3App.functionalityCategory.status">Status</Translate>
            </span>
          </dt>
          <dd>{functionalityCategoryEntity.status}</dd>
        </dl>
        <Button tag={Link} to="/functionality-category" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/functionality-category/${functionalityCategoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FunctionalityCategoryDetail;
