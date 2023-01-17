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
import sn.finedev.java.domain.Ticket;
import sn.finedev.java.repository.TicketRepository;
import sn.finedev.java.repository.search.TicketSearchRepository;
import sn.finedev.java.service.criteria.TicketCriteria;
import sn.finedev.java.service.dto.TicketDTO;
import sn.finedev.java.service.mapper.TicketMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Ticket} entities in the database.
 * The main input is a {@link TicketCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TicketDTO} or a {@link Page} of {@link TicketDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TicketQueryService extends QueryService<Ticket> {

    private final Logger log = LoggerFactory.getLogger(TicketQueryService.class);

    private final TicketRepository ticketRepository;

    private final TicketMapper ticketMapper;

    private final TicketSearchRepository ticketSearchRepository;

    public TicketQueryService(TicketRepository ticketRepository, TicketMapper ticketMapper, TicketSearchRepository ticketSearchRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.ticketSearchRepository = ticketSearchRepository;
    }

    /**
     * Return a {@link List} of {@link TicketDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TicketDTO> findByCriteria(TicketCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Ticket> specification = createSpecification(criteria);
        return ticketMapper.toDto(ticketRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TicketDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TicketDTO> findByCriteria(TicketCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Ticket> specification = createSpecification(criteria);
        return ticketRepository.findAll(specification, page).map(ticketMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TicketCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Ticket> specification = createSpecification(criteria);
        return ticketRepository.count(specification);
    }

    /**
     * Function to convert {@link TicketCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Ticket> createSpecification(TicketCriteria criteria) {
        Specification<Ticket> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Ticket_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Ticket_.code));
            }
            if (criteria.getPricePerUnit() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPricePerUnit(), Ticket_.pricePerUnit));
            }
            if (criteria.getFinalAgentCommission() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getFinalAgentCommission(), Ticket_.finalAgentCommission));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Ticket_.status));
            }
            if (criteria.getEventId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEventId(), root -> root.join(Ticket_.event, JoinType.LEFT).get(Event_.id))
                    );
            }
            if (criteria.getPaymentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPaymentId(), root -> root.join(Ticket_.payment, JoinType.LEFT).get(Payment_.id))
                    );
            }
            if (criteria.getTicketDeliveryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTicketDeliveryId(),
                            root -> root.join(Ticket_.ticketDelivery, JoinType.LEFT).get(TicketDelivery_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
