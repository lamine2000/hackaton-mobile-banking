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
import sn.finedev.java.domain.TicketDeliveryMethod;
import sn.finedev.java.repository.TicketDeliveryMethodRepository;
import sn.finedev.java.repository.search.TicketDeliveryMethodSearchRepository;
import sn.finedev.java.service.criteria.TicketDeliveryMethodCriteria;
import sn.finedev.java.service.dto.TicketDeliveryMethodDTO;
import sn.finedev.java.service.mapper.TicketDeliveryMethodMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TicketDeliveryMethod} entities in the database.
 * The main input is a {@link TicketDeliveryMethodCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TicketDeliveryMethodDTO} or a {@link Page} of {@link TicketDeliveryMethodDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TicketDeliveryMethodQueryService extends QueryService<TicketDeliveryMethod> {

    private final Logger log = LoggerFactory.getLogger(TicketDeliveryMethodQueryService.class);

    private final TicketDeliveryMethodRepository ticketDeliveryMethodRepository;

    private final TicketDeliveryMethodMapper ticketDeliveryMethodMapper;

    private final TicketDeliveryMethodSearchRepository ticketDeliveryMethodSearchRepository;

    public TicketDeliveryMethodQueryService(
        TicketDeliveryMethodRepository ticketDeliveryMethodRepository,
        TicketDeliveryMethodMapper ticketDeliveryMethodMapper,
        TicketDeliveryMethodSearchRepository ticketDeliveryMethodSearchRepository
    ) {
        this.ticketDeliveryMethodRepository = ticketDeliveryMethodRepository;
        this.ticketDeliveryMethodMapper = ticketDeliveryMethodMapper;
        this.ticketDeliveryMethodSearchRepository = ticketDeliveryMethodSearchRepository;
    }

    /**
     * Return a {@link List} of {@link TicketDeliveryMethodDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TicketDeliveryMethodDTO> findByCriteria(TicketDeliveryMethodCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TicketDeliveryMethod> specification = createSpecification(criteria);
        return ticketDeliveryMethodMapper.toDto(ticketDeliveryMethodRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TicketDeliveryMethodDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TicketDeliveryMethodDTO> findByCriteria(TicketDeliveryMethodCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TicketDeliveryMethod> specification = createSpecification(criteria);
        return ticketDeliveryMethodRepository.findAll(specification, page).map(ticketDeliveryMethodMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TicketDeliveryMethodCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TicketDeliveryMethod> specification = createSpecification(criteria);
        return ticketDeliveryMethodRepository.count(specification);
    }

    /**
     * Function to convert {@link TicketDeliveryMethodCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TicketDeliveryMethod> createSpecification(TicketDeliveryMethodCriteria criteria) {
        Specification<TicketDeliveryMethod> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TicketDeliveryMethod_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), TicketDeliveryMethod_.name));
            }
        }
        return specification;
    }
}
