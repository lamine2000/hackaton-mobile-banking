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
import sn.finedev.java.repository.SupplyRequestRepository;
import sn.finedev.java.service.SupplyRequestQueryService;
import sn.finedev.java.service.SupplyRequestService;
import sn.finedev.java.service.criteria.SupplyRequestCriteria;
import sn.finedev.java.service.dto.SupplyRequestDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.SupplyRequest}.
 */
@RestController
@RequestMapping("/api")
public class SupplyRequestResource {

    private final Logger log = LoggerFactory.getLogger(SupplyRequestResource.class);

    private static final String ENTITY_NAME = "supplyRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SupplyRequestService supplyRequestService;

    private final SupplyRequestRepository supplyRequestRepository;

    private final SupplyRequestQueryService supplyRequestQueryService;

    public SupplyRequestResource(
        SupplyRequestService supplyRequestService,
        SupplyRequestRepository supplyRequestRepository,
        SupplyRequestQueryService supplyRequestQueryService
    ) {
        this.supplyRequestService = supplyRequestService;
        this.supplyRequestRepository = supplyRequestRepository;
        this.supplyRequestQueryService = supplyRequestQueryService;
    }

    /**
     * {@code POST  /supply-requests} : Create a new supplyRequest.
     *
     * @param supplyRequestDTO the supplyRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new supplyRequestDTO, or with status {@code 400 (Bad Request)} if the supplyRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/supply-requests")
    public ResponseEntity<SupplyRequestDTO> createSupplyRequest(@Valid @RequestBody SupplyRequestDTO supplyRequestDTO)
        throws URISyntaxException {
        log.debug("REST request to save SupplyRequest : {}", supplyRequestDTO);
        if (supplyRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new supplyRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SupplyRequestDTO result = supplyRequestService.save(supplyRequestDTO);
        return ResponseEntity
            .created(new URI("/api/supply-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /supply-requests/:id} : Updates an existing supplyRequest.
     *
     * @param id the id of the supplyRequestDTO to save.
     * @param supplyRequestDTO the supplyRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supplyRequestDTO,
     * or with status {@code 400 (Bad Request)} if the supplyRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the supplyRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/supply-requests/{id}")
    public ResponseEntity<SupplyRequestDTO> updateSupplyRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SupplyRequestDTO supplyRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to update SupplyRequest : {}, {}", id, supplyRequestDTO);
        if (supplyRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supplyRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supplyRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SupplyRequestDTO result = supplyRequestService.update(supplyRequestDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supplyRequestDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /supply-requests/:id} : Partial updates given fields of an existing supplyRequest, field will ignore if it is null
     *
     * @param id the id of the supplyRequestDTO to save.
     * @param supplyRequestDTO the supplyRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supplyRequestDTO,
     * or with status {@code 400 (Bad Request)} if the supplyRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the supplyRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the supplyRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/supply-requests/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SupplyRequestDTO> partialUpdateSupplyRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SupplyRequestDTO supplyRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SupplyRequest partially : {}, {}", id, supplyRequestDTO);
        if (supplyRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supplyRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supplyRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SupplyRequestDTO> result = supplyRequestService.partialUpdate(supplyRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supplyRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /supply-requests} : get all the supplyRequests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of supplyRequests in body.
     */
    @GetMapping("/supply-requests")
    public ResponseEntity<List<SupplyRequestDTO>> getAllSupplyRequests(
        SupplyRequestCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get SupplyRequests by criteria: {}", criteria);
        Page<SupplyRequestDTO> page = supplyRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /supply-requests/count} : count all the supplyRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/supply-requests/count")
    public ResponseEntity<Long> countSupplyRequests(SupplyRequestCriteria criteria) {
        log.debug("REST request to count SupplyRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(supplyRequestQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /supply-requests/:id} : get the "id" supplyRequest.
     *
     * @param id the id of the supplyRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the supplyRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/supply-requests/{id}")
    public ResponseEntity<SupplyRequestDTO> getSupplyRequest(@PathVariable Long id) {
        log.debug("REST request to get SupplyRequest : {}", id);
        Optional<SupplyRequestDTO> supplyRequestDTO = supplyRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(supplyRequestDTO);
    }

    /**
     * {@code DELETE  /supply-requests/:id} : delete the "id" supplyRequest.
     *
     * @param id the id of the supplyRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/supply-requests/{id}")
    public ResponseEntity<Void> deleteSupplyRequest(@PathVariable Long id) {
        log.debug("REST request to delete SupplyRequest : {}", id);
        supplyRequestService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/supply-requests?query=:query} : search for the supplyRequest corresponding
     * to the query.
     *
     * @param query the query of the supplyRequest search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/supply-requests")
    public ResponseEntity<List<SupplyRequestDTO>> searchSupplyRequests(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of SupplyRequests for query {}", query);
        Page<SupplyRequestDTO> page = supplyRequestService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
