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
import sn.finedev.java.repository.FunctionalityCategoryRepository;
import sn.finedev.java.service.FunctionalityCategoryQueryService;
import sn.finedev.java.service.FunctionalityCategoryService;
import sn.finedev.java.service.criteria.FunctionalityCategoryCriteria;
import sn.finedev.java.service.dto.FunctionalityCategoryDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.FunctionalityCategory}.
 */
@RestController
@RequestMapping("/api")
public class FunctionalityCategoryResource {

    private final Logger log = LoggerFactory.getLogger(FunctionalityCategoryResource.class);

    private static final String ENTITY_NAME = "functionalityCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FunctionalityCategoryService functionalityCategoryService;

    private final FunctionalityCategoryRepository functionalityCategoryRepository;

    private final FunctionalityCategoryQueryService functionalityCategoryQueryService;

    public FunctionalityCategoryResource(
        FunctionalityCategoryService functionalityCategoryService,
        FunctionalityCategoryRepository functionalityCategoryRepository,
        FunctionalityCategoryQueryService functionalityCategoryQueryService
    ) {
        this.functionalityCategoryService = functionalityCategoryService;
        this.functionalityCategoryRepository = functionalityCategoryRepository;
        this.functionalityCategoryQueryService = functionalityCategoryQueryService;
    }

    /**
     * {@code POST  /functionality-categories} : Create a new functionalityCategory.
     *
     * @param functionalityCategoryDTO the functionalityCategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new functionalityCategoryDTO, or with status {@code 400 (Bad Request)} if the functionalityCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/functionality-categories")
    public ResponseEntity<FunctionalityCategoryDTO> createFunctionalityCategory(
        @Valid @RequestBody FunctionalityCategoryDTO functionalityCategoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to save FunctionalityCategory : {}", functionalityCategoryDTO);
        if (functionalityCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new functionalityCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FunctionalityCategoryDTO result = functionalityCategoryService.save(functionalityCategoryDTO);
        return ResponseEntity
            .created(new URI("/api/functionality-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /functionality-categories/:id} : Updates an existing functionalityCategory.
     *
     * @param id the id of the functionalityCategoryDTO to save.
     * @param functionalityCategoryDTO the functionalityCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated functionalityCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the functionalityCategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the functionalityCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/functionality-categories/{id}")
    public ResponseEntity<FunctionalityCategoryDTO> updateFunctionalityCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FunctionalityCategoryDTO functionalityCategoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update FunctionalityCategory : {}, {}", id, functionalityCategoryDTO);
        if (functionalityCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, functionalityCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!functionalityCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FunctionalityCategoryDTO result = functionalityCategoryService.update(functionalityCategoryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, functionalityCategoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /functionality-categories/:id} : Partial updates given fields of an existing functionalityCategory, field will ignore if it is null
     *
     * @param id the id of the functionalityCategoryDTO to save.
     * @param functionalityCategoryDTO the functionalityCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated functionalityCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the functionalityCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the functionalityCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the functionalityCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/functionality-categories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FunctionalityCategoryDTO> partialUpdateFunctionalityCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FunctionalityCategoryDTO functionalityCategoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update FunctionalityCategory partially : {}, {}", id, functionalityCategoryDTO);
        if (functionalityCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, functionalityCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!functionalityCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FunctionalityCategoryDTO> result = functionalityCategoryService.partialUpdate(functionalityCategoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, functionalityCategoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /functionality-categories} : get all the functionalityCategories.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of functionalityCategories in body.
     */
    @GetMapping("/functionality-categories")
    public ResponseEntity<List<FunctionalityCategoryDTO>> getAllFunctionalityCategories(
        FunctionalityCategoryCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get FunctionalityCategories by criteria: {}", criteria);
        Page<FunctionalityCategoryDTO> page = functionalityCategoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /functionality-categories/count} : count all the functionalityCategories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/functionality-categories/count")
    public ResponseEntity<Long> countFunctionalityCategories(FunctionalityCategoryCriteria criteria) {
        log.debug("REST request to count FunctionalityCategories by criteria: {}", criteria);
        return ResponseEntity.ok().body(functionalityCategoryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /functionality-categories/:id} : get the "id" functionalityCategory.
     *
     * @param id the id of the functionalityCategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the functionalityCategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/functionality-categories/{id}")
    public ResponseEntity<FunctionalityCategoryDTO> getFunctionalityCategory(@PathVariable Long id) {
        log.debug("REST request to get FunctionalityCategory : {}", id);
        Optional<FunctionalityCategoryDTO> functionalityCategoryDTO = functionalityCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(functionalityCategoryDTO);
    }

    /**
     * {@code DELETE  /functionality-categories/:id} : delete the "id" functionalityCategory.
     *
     * @param id the id of the functionalityCategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/functionality-categories/{id}")
    public ResponseEntity<Void> deleteFunctionalityCategory(@PathVariable Long id) {
        log.debug("REST request to delete FunctionalityCategory : {}", id);
        functionalityCategoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/functionality-categories?query=:query} : search for the functionalityCategory corresponding
     * to the query.
     *
     * @param query the query of the functionalityCategory search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/functionality-categories")
    public ResponseEntity<List<FunctionalityCategoryDTO>> searchFunctionalityCategories(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of FunctionalityCategories for query {}", query);
        Page<FunctionalityCategoryDTO> page = functionalityCategoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
