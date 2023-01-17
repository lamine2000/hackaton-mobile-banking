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
import sn.finedev.java.repository.FunctionalityRepository;
import sn.finedev.java.service.FunctionalityQueryService;
import sn.finedev.java.service.FunctionalityService;
import sn.finedev.java.service.criteria.FunctionalityCriteria;
import sn.finedev.java.service.dto.FunctionalityDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.Functionality}.
 */
@RestController
@RequestMapping("/api")
public class FunctionalityResource {

    private final Logger log = LoggerFactory.getLogger(FunctionalityResource.class);

    private static final String ENTITY_NAME = "functionality";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FunctionalityService functionalityService;

    private final FunctionalityRepository functionalityRepository;

    private final FunctionalityQueryService functionalityQueryService;

    public FunctionalityResource(
        FunctionalityService functionalityService,
        FunctionalityRepository functionalityRepository,
        FunctionalityQueryService functionalityQueryService
    ) {
        this.functionalityService = functionalityService;
        this.functionalityRepository = functionalityRepository;
        this.functionalityQueryService = functionalityQueryService;
    }

    /**
     * {@code POST  /functionalities} : Create a new functionality.
     *
     * @param functionalityDTO the functionalityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new functionalityDTO, or with status {@code 400 (Bad Request)} if the functionality has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/functionalities")
    public ResponseEntity<FunctionalityDTO> createFunctionality(@Valid @RequestBody FunctionalityDTO functionalityDTO)
        throws URISyntaxException {
        log.debug("REST request to save Functionality : {}", functionalityDTO);
        if (functionalityDTO.getId() != null) {
            throw new BadRequestAlertException("A new functionality cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FunctionalityDTO result = functionalityService.save(functionalityDTO);
        return ResponseEntity
            .created(new URI("/api/functionalities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /functionalities/:id} : Updates an existing functionality.
     *
     * @param id the id of the functionalityDTO to save.
     * @param functionalityDTO the functionalityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated functionalityDTO,
     * or with status {@code 400 (Bad Request)} if the functionalityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the functionalityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/functionalities/{id}")
    public ResponseEntity<FunctionalityDTO> updateFunctionality(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FunctionalityDTO functionalityDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Functionality : {}, {}", id, functionalityDTO);
        if (functionalityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, functionalityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!functionalityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FunctionalityDTO result = functionalityService.update(functionalityDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, functionalityDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /functionalities/:id} : Partial updates given fields of an existing functionality, field will ignore if it is null
     *
     * @param id the id of the functionalityDTO to save.
     * @param functionalityDTO the functionalityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated functionalityDTO,
     * or with status {@code 400 (Bad Request)} if the functionalityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the functionalityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the functionalityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/functionalities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FunctionalityDTO> partialUpdateFunctionality(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FunctionalityDTO functionalityDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Functionality partially : {}, {}", id, functionalityDTO);
        if (functionalityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, functionalityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!functionalityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FunctionalityDTO> result = functionalityService.partialUpdate(functionalityDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, functionalityDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /functionalities} : get all the functionalities.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of functionalities in body.
     */
    @GetMapping("/functionalities")
    public ResponseEntity<List<FunctionalityDTO>> getAllFunctionalities(
        FunctionalityCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Functionalities by criteria: {}", criteria);
        Page<FunctionalityDTO> page = functionalityQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /functionalities/count} : count all the functionalities.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/functionalities/count")
    public ResponseEntity<Long> countFunctionalities(FunctionalityCriteria criteria) {
        log.debug("REST request to count Functionalities by criteria: {}", criteria);
        return ResponseEntity.ok().body(functionalityQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /functionalities/:id} : get the "id" functionality.
     *
     * @param id the id of the functionalityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the functionalityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/functionalities/{id}")
    public ResponseEntity<FunctionalityDTO> getFunctionality(@PathVariable Long id) {
        log.debug("REST request to get Functionality : {}", id);
        Optional<FunctionalityDTO> functionalityDTO = functionalityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(functionalityDTO);
    }

    /**
     * {@code DELETE  /functionalities/:id} : delete the "id" functionality.
     *
     * @param id the id of the functionalityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/functionalities/{id}")
    public ResponseEntity<Void> deleteFunctionality(@PathVariable Long id) {
        log.debug("REST request to delete Functionality : {}", id);
        functionalityService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/functionalities?query=:query} : search for the functionality corresponding
     * to the query.
     *
     * @param query the query of the functionality search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/functionalities")
    public ResponseEntity<List<FunctionalityDTO>> searchFunctionalities(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Functionalities for query {}", query);
        Page<FunctionalityDTO> page = functionalityService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
