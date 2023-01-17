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
import sn.finedev.java.repository.TownRepository;
import sn.finedev.java.service.TownQueryService;
import sn.finedev.java.service.TownService;
import sn.finedev.java.service.criteria.TownCriteria;
import sn.finedev.java.service.dto.TownDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.Town}.
 */
@RestController
@RequestMapping("/api")
public class TownResource {

    private final Logger log = LoggerFactory.getLogger(TownResource.class);

    private static final String ENTITY_NAME = "town";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TownService townService;

    private final TownRepository townRepository;

    private final TownQueryService townQueryService;

    public TownResource(TownService townService, TownRepository townRepository, TownQueryService townQueryService) {
        this.townService = townService;
        this.townRepository = townRepository;
        this.townQueryService = townQueryService;
    }

    /**
     * {@code POST  /towns} : Create a new town.
     *
     * @param townDTO the townDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new townDTO, or with status {@code 400 (Bad Request)} if the town has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/towns")
    public ResponseEntity<TownDTO> createTown(@Valid @RequestBody TownDTO townDTO) throws URISyntaxException {
        log.debug("REST request to save Town : {}", townDTO);
        if (townDTO.getId() != null) {
            throw new BadRequestAlertException("A new town cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TownDTO result = townService.save(townDTO);
        return ResponseEntity
            .created(new URI("/api/towns/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /towns/:id} : Updates an existing town.
     *
     * @param id the id of the townDTO to save.
     * @param townDTO the townDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated townDTO,
     * or with status {@code 400 (Bad Request)} if the townDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the townDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/towns/{id}")
    public ResponseEntity<TownDTO> updateTown(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TownDTO townDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Town : {}, {}", id, townDTO);
        if (townDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, townDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!townRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TownDTO result = townService.update(townDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, townDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /towns/:id} : Partial updates given fields of an existing town, field will ignore if it is null
     *
     * @param id the id of the townDTO to save.
     * @param townDTO the townDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated townDTO,
     * or with status {@code 400 (Bad Request)} if the townDTO is not valid,
     * or with status {@code 404 (Not Found)} if the townDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the townDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/towns/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TownDTO> partialUpdateTown(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TownDTO townDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Town partially : {}, {}", id, townDTO);
        if (townDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, townDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!townRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TownDTO> result = townService.partialUpdate(townDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, townDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /towns} : get all the towns.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of towns in body.
     */
    @GetMapping("/towns")
    public ResponseEntity<List<TownDTO>> getAllTowns(
        TownCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Towns by criteria: {}", criteria);
        Page<TownDTO> page = townQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /towns/count} : count all the towns.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/towns/count")
    public ResponseEntity<Long> countTowns(TownCriteria criteria) {
        log.debug("REST request to count Towns by criteria: {}", criteria);
        return ResponseEntity.ok().body(townQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /towns/:id} : get the "id" town.
     *
     * @param id the id of the townDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the townDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/towns/{id}")
    public ResponseEntity<TownDTO> getTown(@PathVariable Long id) {
        log.debug("REST request to get Town : {}", id);
        Optional<TownDTO> townDTO = townService.findOne(id);
        return ResponseUtil.wrapOrNotFound(townDTO);
    }

    /**
     * {@code DELETE  /towns/:id} : delete the "id" town.
     *
     * @param id the id of the townDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/towns/{id}")
    public ResponseEntity<Void> deleteTown(@PathVariable Long id) {
        log.debug("REST request to delete Town : {}", id);
        townService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/towns?query=:query} : search for the town corresponding
     * to the query.
     *
     * @param query the query of the town search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/towns")
    public ResponseEntity<List<TownDTO>> searchTowns(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Towns for query {}", query);
        Page<TownDTO> page = townService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
