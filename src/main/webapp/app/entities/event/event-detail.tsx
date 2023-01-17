import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './event.reducer';

export const EventDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const eventEntity = useAppSelector(state => state.event.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="eventDetailsHeading">
          <Translate contentKey="hackaton3App.event.detail.title">Event</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{eventEntity.id}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="hackaton3App.event.date">Date</Translate>
            </span>
          </dt>
          <dd>{eventEntity.date ? <TextFormat value={eventEntity.date} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="hackaton3App.event.title">Title</Translate>
            </span>
          </dt>
          <dd>{eventEntity.title}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="hackaton3App.event.description">Description</Translate>
            </span>
          </dt>
          <dd>{eventEntity.description}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="hackaton3App.event.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{eventEntity.createdAt ? <TextFormat value={eventEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="createdBy">
              <Translate contentKey="hackaton3App.event.createdBy">Created By</Translate>
            </span>
          </dt>
          <dd>{eventEntity.createdBy}</dd>
          <dt>
            <span id="organizer">
              <Translate contentKey="hackaton3App.event.organizer">Organizer</Translate>
            </span>
          </dt>
          <dd>{eventEntity.organizer}</dd>
          <dt>
            <span id="expireAt">
              <Translate contentKey="hackaton3App.event.expireAt">Expire At</Translate>
            </span>
          </dt>
          <dd>{eventEntity.expireAt ? <TextFormat value={eventEntity.expireAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="hackaton3App.event.status">Status</Translate>
            </span>
          </dt>
          <dd>{eventEntity.status}</dd>
        </dl>
        <Button tag={Link} to="/event" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/event/${eventEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default EventDetail;
