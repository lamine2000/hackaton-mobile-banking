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
import sn.finedev.java.domain.NotificationSettings;
import sn.finedev.java.repository.NotificationSettingsRepository;
import sn.finedev.java.repository.search.NotificationSettingsSearchRepository;
import sn.finedev.java.service.criteria.NotificationSettingsCriteria;
import sn.finedev.java.service.dto.NotificationSettingsDTO;
import sn.finedev.java.service.mapper.NotificationSettingsMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link NotificationSettings} entities in the database.
 * The main input is a {@link NotificationSettingsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link NotificationSettingsDTO} or a {@link Page} of {@link NotificationSettingsDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NotificationSettingsQueryService extends QueryService<NotificationSettings> {

    private final Logger log = LoggerFactory.getLogger(NotificationSettingsQueryService.class);

    private final NotificationSettingsRepository notificationSettingsRepository;

    private final NotificationSettingsMapper notificationSettingsMapper;

    private final NotificationSettingsSearchRepository notificationSettingsSearchRepository;

    public NotificationSettingsQueryService(
        NotificationSettingsRepository notificationSettingsRepository,
        NotificationSettingsMapper notificationSettingsMapper,
        NotificationSettingsSearchRepository notificationSettingsSearchRepository
    ) {
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.notificationSettingsMapper = notificationSettingsMapper;
        this.notificationSettingsSearchRepository = notificationSettingsSearchRepository;
    }

    /**
     * Return a {@link List} of {@link NotificationSettingsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<NotificationSettingsDTO> findByCriteria(NotificationSettingsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<NotificationSettings> specification = createSpecification(criteria);
        return notificationSettingsMapper.toDto(notificationSettingsRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link NotificationSettingsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<NotificationSettingsDTO> findByCriteria(NotificationSettingsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<NotificationSettings> specification = createSpecification(criteria);
        return notificationSettingsRepository.findAll(specification, page).map(notificationSettingsMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(NotificationSettingsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<NotificationSettings> specification = createSpecification(criteria);
        return notificationSettingsRepository.count(specification);
    }

    /**
     * Function to convert {@link NotificationSettingsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<NotificationSettings> createSpecification(NotificationSettingsCriteria criteria) {
        Specification<NotificationSettings> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), NotificationSettings_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), NotificationSettings_.name));
            }
            if (criteria.getValue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getValue(), NotificationSettings_.value));
            }
        }
        return specification;
    }
}
