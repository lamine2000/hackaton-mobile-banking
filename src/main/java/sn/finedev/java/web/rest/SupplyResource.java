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
import sn.finedev.java.repository.SupplyRepository;
import sn.finedev.java.service.SupplyQueryService;
import sn.finedev.java.service.SupplyService;
import sn.finedev.java.service.criteria.SupplyCriteria;
import sn.finedev.java.service.dto.SupplyDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.Supply}.
 */
@RestController
@RequestMapping("/api")
public class SupplyResource {

    private final Logger log = LoggerFactory.getLogger(SupplyResource.class);

    private static final String ENTITY_NAME = "supply";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SupplyService supplyService;

    private final SupplyRepository supplyRepository;

    private final SupplyQueryService supplyQueryService;

    public SupplyResource(SupplyService supplyService, SupplyRepository supplyRepository, SupplyQueryService supplyQueryService) {
        this.supplyService = supplyService;
        this.supplyRepository = supplyRepository;
        this.supplyQueryService = supplyQueryService;
    }

    /**
     * {@code POST  /supplies} : Create a new supply.
     *
     * @param supplyDTO the supplyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new supplyDTO, or with status {@code 400 (Bad Request)} if the supply has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/supplies")
    public ResponseEntity<SupplyDTO> createSupply(@Valid @RequestBody SupplyDTO supplyDTO) throws URISyntaxException {
        log.debug("REST request to save Supply : {}", supplyDTO);
        if (supplyDTO.getId() != null) {
            throw new BadRequestAlertException("A new supply cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SupplyDTO result = supplyService.save(supplyDTO);
        return ResponseEntity
            .created(new URI("/api/supplies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /supplies/:id} : Updates an existing supply.
     *
     * @param id the id of the supplyDTO to save.
     * @param supplyDTO the supplyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supplyDTO,
     * or with status {@code 400 (Bad Request)} if the supplyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the supplyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/supplies/{id}")
    public ResponseEntity<SupplyDTO> updateSupply(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SupplyDTO supplyDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Supply : {}, {}", id, supplyDTO);
        if (supplyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supplyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supplyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SupplyDTO result = supplyService.update(supplyDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supplyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /supplies/:id} : Partial updates given fields of an existing supply, field will ignore if it is null
     *
     * @param id the id of the supplyDTO to save.
     * @param supplyDTO the supplyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supplyDTO,
     * or with status {@code 400 (Bad Request)} if the supplyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the supplyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the supplyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/supplies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SupplyDTO> partialUpdateSupply(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SupplyDTO supplyDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Supply partially : {}, {}", id, supplyDTO);
        if (supplyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supplyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supplyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SupplyDTO> result = supplyService.partialUpdate(supplyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, supplyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /supplies} : get all the supplies.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of supplies in body.
     */
    @GetMapping("/supplies")
    public ResponseEntity<List<SupplyDTO>> getAllSupplies(
        SupplyCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Supplies by criteria: {}", criteria);
        Page<SupplyDTO> page = supplyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /supplies/count} : count all the supplies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/supplies/count")
    public ResponseEntity<Long> countSupplies(SupplyCriteria criteria) {
        log.debug("REST request to count Supplies by criteria: {}", criteria);
        return ResponseEntity.ok().body(supplyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /supplies/:id} : get the "id" supply.
     *
     * @param id the id of the supplyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the supplyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/supplies/{id}")
    public ResponseEntity<SupplyDTO> getSupply(@PathVariable Long id) {
        log.debug("REST request to get Supply : {}", id);
        Optional<SupplyDTO> supplyDTO = supplyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(supplyDTO);
    }

    /**
     * {@code DELETE  /supplies/:id} : delete the "id" supply.
     *
     * @param id the id of the supplyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/supplies/{id}")
    public ResponseEntity<Void> deleteSupply(@PathVariable Long id) {
        log.debug("REST request to delete Supply : {}", id);
        supplyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/supplies?query=:query} : search for the supply corresponding
     * to the query.
     *
     * @param query the query of the supply search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/supplies")
    public ResponseEntity<List<SupplyDTO>> searchSupplies(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Supplies for query {}", query);
        Page<SupplyDTO> page = supplyService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
