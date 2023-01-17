package sn.finedev.java.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.finedev.java.repository.NotificationSettingsRepository;
import sn.finedev.java.service.NotificationSettingsQueryService;
import sn.finedev.java.service.NotificationSettingsService;
import sn.finedev.java.service.criteria.NotificationSettingsCriteria;
import sn.finedev.java.service.dto.NotificationSettingsDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.NotificationSettings}.
 */
@RestController
@RequestMapping("/api")
public class NotificationSettingsResource {

    private final Logger log = LoggerFactory.getLogger(NotificationSettingsResource.class);

    private static final String ENTITY_NAME = "notificationSettings";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationSettingsService notificationSettingsService;

    private final NotificationSettingsRepository notificationSettingsRepository;

    private final NotificationSettingsQueryService notificationSettingsQueryService;

    public NotificationSettingsResource(
        NotificationSettingsService notificationSettingsService,
        NotificationSettingsRepository notificationSettingsRepository,
        NotificationSettingsQueryService notificationSettingsQueryService
    ) {
        this.notificationSettingsService = notificationSettingsService;
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.notificationSettingsQueryService = notificationSettingsQueryService;
    }

    /**
     * {@code POST  /notification-settings} : Create a new notificationSettings.
     *
     * @param notificationSettingsDTO the notificationSettingsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationSettingsDTO, or with status {@code 400 (Bad Request)} if the notificationSettings has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notification-settings")
    public ResponseEntity<NotificationSettingsDTO> createNotificationSettings(
        @Valid @RequestBody NotificationSettingsDTO notificationSettingsDTO
    ) throws URISyntaxException {
        log.debug("REST request to save NotificationSettings : {}", notificationSettingsDTO);
        if (notificationSettingsDTO.getId() != null) {
            throw new BadRequestAlertException("A new notificationSettings cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NotificationSettingsDTO result = notificationSettingsService.save(notificationSettingsDTO);
        return ResponseEntity
            .created(new URI("/api/notification-settings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /notification-settings/:id} : Updates an existing notificationSettings.
     *
     * @param id the id of the notificationSettingsDTO to save.
     * @param notificationSettingsDTO the notificationSettingsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationSettingsDTO,
     * or with status {@code 400 (Bad Request)} if the notificationSettingsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationSettingsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notification-settings/{id}")
    public ResponseEntity<NotificationSettingsDTO> updateNotificationSettings(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NotificationSettingsDTO notificationSettingsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update NotificationSettings : {}, {}", id, notificationSettingsDTO);
        if (notificationSettingsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationSettingsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationSettingsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        NotificationSettingsDTO result = notificationSettingsService.update(notificationSettingsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationSettingsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /notification-settings/:id} : Partial updates given fields of an existing notificationSettings, field will ignore if it is null
     *
     * @param id the id of the notificationSettingsDTO to save.
     * @param notificationSettingsDTO the notificationSettingsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationSettingsDTO,
     * or with status {@code 400 (Bad Request)} if the notificationSettingsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notificationSettingsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notificationSettingsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/notification-settings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NotificationSettingsDTO> partialUpdateNotificationSettings(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NotificationSettingsDTO notificationSettingsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update NotificationSettings partially : {}, {}", id, notificationSettingsDTO);
        if (notificationSettingsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationSettingsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationSettingsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NotificationSettingsDTO> result = notificationSettingsService.partialUpdate(notificationSettingsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationSettingsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /notification-settings} : get all the notificationSettings.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notificationSettings in body.
     */
    @GetMapping("/notification-settings")
    public ResponseEntity<List<NotificationSettingsDTO>> getAllNotificationSettings(
        NotificationSettingsCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get NotificationSettings by criteria: {}", criteria);
        Page<NotificationSettingsDTO> page = notificationSettingsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notification-settings/count} : count all the notificationSettings.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/notification-settings/count")
    public ResponseEntity<Long> countNotificationSettings(NotificationSettingsCriteria criteria) {
        log.debug("REST request to count NotificationSettings by criteria: {}", criteria);
        return ResponseEntity.ok().body(notificationSettingsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /notification-settings/:id} : get the "id" notificationSettings.
     *
     * @param id the id of the notificationSettingsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationSettingsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notification-settings/{id}")
    public ResponseEntity<NotificationSettingsDTO> getNotificationSettings(@PathVariable Long id) {
        log.debug("REST request to get NotificationSettings : {}", id);
        Optional<NotificationSettingsDTO> notificationSettingsDTO = notificationSettingsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificationSettingsDTO);
    }

    /**
     * {@code DELETE  /notification-settings/:id} : delete the "id" notificationSettings.
     *
     * @param id the id of the notificationSettingsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notification-settings/{id}")
    public ResponseEntity<Void> deleteNotificationSettings(@PathVariable Long id) {
        log.debug("REST request to delete NotificationSettings : {}", id);
        notificationSettingsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/notification-settings?query=:query} : search for the notificationSettings corresponding
     * to the query.
     *
     * @param query the query of the notificationSettings search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/notification-settings")
    public ResponseEntity<List<NotificationSettingsDTO>> searchNotificationSettings(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of NotificationSettings for query {}", query);
        Page<NotificationSettingsDTO> page = notificationSettingsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
