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
import sn.finedev.java.domain.MobileBankingActor;
import sn.finedev.java.repository.MobileBankingActorRepository;
import sn.finedev.java.repository.search.MobileBankingActorSearchRepository;
import sn.finedev.java.service.criteria.MobileBankingActorCriteria;
import sn.finedev.java.service.dto.MobileBankingActorDTO;
import sn.finedev.java.service.mapper.MobileBankingActorMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link MobileBankingActor} entities in the database.
 * The main input is a {@link MobileBankingActorCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MobileBankingActorDTO} or a {@link Page} of {@link MobileBankingActorDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MobileBankingActorQueryService extends QueryService<MobileBankingActor> {

    private final Logger log = LoggerFactory.getLogger(MobileBankingActorQueryService.class);

    private final MobileBankingActorRepository mobileBankingActorRepository;

    private final MobileBankingActorMapper mobileBankingActorMapper;

    private final MobileBankingActorSearchRepository mobileBankingActorSearchRepository;

    public MobileBankingActorQueryService(
        MobileBankingActorRepository mobileBankingActorRepository,
        MobileBankingActorMapper mobileBankingActorMapper,
        MobileBankingActorSearchRepository mobileBankingActorSearchRepository
    ) {
        this.mobileBankingActorRepository = mobileBankingActorRepository;
        this.mobileBankingActorMapper = mobileBankingActorMapper;
        this.mobileBankingActorSearchRepository = mobileBankingActorSearchRepository;
    }

    /**
     * Return a {@link List} of {@link MobileBankingActorDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MobileBankingActorDTO> findByCriteria(MobileBankingActorCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<MobileBankingActor> specification = createSpecification(criteria);
        return mobileBankingActorMapper.toDto(mobileBankingActorRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MobileBankingActorDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MobileBankingActorDTO> findByCriteria(MobileBankingActorCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<MobileBankingActor> specification = createSpecification(criteria);
        return mobileBankingActorRepository.findAll(specification, page).map(mobileBankingActorMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MobileBankingActorCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<MobileBankingActor> specification = createSpecification(criteria);
        return mobileBankingActorRepository.count(specification);
    }

    /**
     * Function to convert {@link MobileBankingActorCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<MobileBankingActor> createSpecification(MobileBankingActorCriteria criteria) {
        Specification<MobileBankingActor> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), MobileBankingActor_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), MobileBankingActor_.name));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), MobileBankingActor_.status));
            }
            if (criteria.getFunctionalityId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFunctionalityId(),
                            root -> root.join(MobileBankingActor_.functionalities, JoinType.LEFT).get(Functionality_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
