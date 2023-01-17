package sn.finedev.java.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.*; // for static metamodels
import sn.finedev.java.domain.TicketDelivery;
import sn.finedev.java.repository.TicketDeliveryRepository;
import sn.finedev.java.repository.search.TicketDeliverySearchRepository;
import sn.finedev.java.service.criteria.TicketDeliveryCriteria;
import sn.finedev.java.service.dto.TicketDeliveryDTO;
import sn.finedev.java.service.mapper.TicketDeliveryMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TicketDelivery} entities in the database.
 * The main input is a {@link TicketDeliveryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TicketDeliveryDTO} or a {@link Page} of {@link TicketDeliveryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TicketDeliveryQueryService extends QueryService<TicketDelivery> {

    private final Logger log = LoggerFactory.getLogger(TicketDeliveryQueryService.class);

    private final TicketDeliveryRepository ticketDeliveryRepository;

    private final TicketDeliveryMapper ticketDeliveryMapper;

    private final TicketDeliverySearchRepository ticketDeliverySearchRepository;

    public TicketDeliveryQueryService(
        TicketDeliveryRepository ticketDeliveryRepository,
        TicketDeliveryMapper ticketDeliveryMapper,
        TicketDeliverySearchRepository ticketDeliverySearchRepository
    ) {
        this.ticketDeliveryRepository = ticketDeliveryRepository;
        this.ticketDeliveryMapper = ticketDeliveryMapper;
        this.ticketDeliverySearchRepository = ticketDeliverySearchRepository;
    }

    /**
     * Return a {@link List} of {@link TicketDeliveryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TicketDeliveryDTO> findByCriteria(TicketDeliveryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TicketDelivery> specification = createSpecification(criteria);
        return ticketDeliveryMapper.toDto(ticketDeliveryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TicketDeliveryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TicketDeliveryDTO> findByCriteria(TicketDeliveryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TicketDelivery> specification = createSpecification(criteria);
        return ticketDeliveryRepository.findAll(specification, page).map(ticketDeliveryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TicketDeliveryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TicketDelivery> specification = createSpecification(criteria);
        return ticketDeliveryRepository.count(specification);
    }

    /**
     * Function to convert {@link TicketDeliveryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TicketDelivery> createSpecification(TicketDeliveryCriteria criteria) {
        Specification<TicketDelivery> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TicketDelivery_.id));
            }
            if (criteria.getBoughtAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBoughtAt(), TicketDelivery_.boughtAt));
            }
            if (criteria.getBoughtBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBoughtBy(), TicketDelivery_.boughtBy));
            }
            if (criteria.getQuantity() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQuantity(), TicketDelivery_.quantity));
            }
            if (criteria.getTicketDeliveryMethodId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTicketDeliveryMethodId(),
                            root -> root.join(TicketDelivery_.ticketDeliveryMethod, JoinType.LEFT).get(TicketDeliveryMethod_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
