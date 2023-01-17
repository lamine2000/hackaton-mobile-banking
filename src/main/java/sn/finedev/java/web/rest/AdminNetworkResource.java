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
import sn.finedev.java.repository.AdminNetworkRepository;
import sn.finedev.java.service.AdminNetworkQueryService;
import sn.finedev.java.service.AdminNetworkService;
import sn.finedev.java.service.criteria.AdminNetworkCriteria;
import sn.finedev.java.service.dto.AdminNetworkDTO;
import sn.finedev.java.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.finedev.java.domain.AdminNetwork}.
 */
@RestController
@RequestMapping("/api")
public class AdminNetworkResource {

    private final Logger log = LoggerFactory.getLogger(AdminNetworkResource.class);

    private static final String ENTITY_NAME = "adminNetwork";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AdminNetworkService adminNetworkService;

    private final AdminNetworkRepository adminNetworkRepository;

    private final AdminNetworkQueryService adminNetworkQueryService;

    public AdminNetworkResource(
        AdminNetworkService adminNetworkService,
        AdminNetworkRepository adminNetworkRepository,
        AdminNetworkQueryService adminNetworkQueryService
    ) {
        this.adminNetworkService = adminNetworkService;
        this.adminNetworkRepository = adminNetworkRepository;
        this.adminNetworkQueryService = adminNetworkQueryService;
    }

    /**
     * {@code POST  /admin-networks} : Create a new adminNetwork.
     *
     * @param adminNetworkDTO the adminNetworkDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new adminNetworkDTO, or with status {@code 400 (Bad Request)} if the adminNetwork has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/admin-networks")
    public ResponseEntity<AdminNetworkDTO> createAdminNetwork(@Valid @RequestBody AdminNetworkDTO adminNetworkDTO)
        throws URISyntaxException {
        log.debug("REST request to save AdminNetwork : {}", adminNetworkDTO);
        if (adminNetworkDTO.getId() != null) {
            throw new BadRequestAlertException("A new adminNetwork cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AdminNetworkDTO result = adminNetworkService.save(adminNetworkDTO);
        return ResponseEntity
            .created(new URI("/api/admin-networks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /admin-networks/:id} : Updates an existing adminNetwork.
     *
     * @param id the id of the adminNetworkDTO to save.
     * @param adminNetworkDTO the adminNetworkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated adminNetworkDTO,
     * or with status {@code 400 (Bad Request)} if the adminNetworkDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the adminNetworkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/admin-networks/{id}")
    public ResponseEntity<AdminNetworkDTO> updateAdminNetwork(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AdminNetworkDTO adminNetworkDTO
    ) throws URISyntaxException {
        log.debug("REST request to update AdminNetwork : {}, {}", id, adminNetworkDTO);
        if (adminNetworkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, adminNetworkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!adminNetworkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AdminNetworkDTO result = adminNetworkService.update(adminNetworkDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, adminNetworkDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /admin-networks/:id} : Partial updates given fields of an existing adminNetwork, field will ignore if it is null
     *
     * @param id the id of the adminNetworkDTO to save.
     * @param adminNetworkDTO the adminNetworkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated adminNetworkDTO,
     * or with status {@code 400 (Bad Request)} if the adminNetworkDTO is not valid,
     * or with status {@code 404 (Not Found)} if the adminNetworkDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the adminNetworkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/admin-networks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AdminNetworkDTO> partialUpdateAdminNetwork(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AdminNetworkDTO adminNetworkDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AdminNetwork partially : {}, {}", id, adminNetworkDTO);
        if (adminNetworkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, adminNetworkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!adminNetworkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AdminNetworkDTO> result = adminNetworkService.partialUpdate(adminNetworkDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, adminNetworkDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /admin-networks} : get all the adminNetworks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of adminNetworks in body.
     */
    @GetMapping("/admin-networks")
    public ResponseEntity<List<AdminNetworkDTO>> getAllAdminNetworks(
        AdminNetworkCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get AdminNetworks by criteria: {}", criteria);
        Page<AdminNetworkDTO> page = adminNetworkQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /admin-networks/count} : count all the adminNetworks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/admin-networks/count")
    public ResponseEntity<Long> countAdminNetworks(AdminNetworkCriteria criteria) {
        log.debug("REST request to count AdminNetworks by criteria: {}", criteria);
        return ResponseEntity.ok().body(adminNetworkQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /admin-networks/:id} : get the "id" adminNetwork.
     *
     * @param id the id of the adminNetworkDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the adminNetworkDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/admin-networks/{id}")
    public ResponseEntity<AdminNetworkDTO> getAdminNetwork(@PathVariable Long id) {
        log.debug("REST request to get AdminNetwork : {}", id);
        Optional<AdminNetworkDTO> adminNetworkDTO = adminNetworkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(adminNetworkDTO);
    }

    /**
     * {@code DELETE  /admin-networks/:id} : delete the "id" adminNetwork.
     *
     * @param id the id of the adminNetworkDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/admin-networks/{id}")
    public ResponseEntity<Void> deleteAdminNetwork(@PathVariable Long id) {
        log.debug("REST request to delete AdminNetwork : {}", id);
        adminNetworkService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/admin-networks?query=:query} : search for the adminNetwork corresponding
     * to the query.
     *
     * @param query the query of the adminNetwork search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/admin-networks")
    public ResponseEntity<List<AdminNetworkDTO>> searchAdminNetworks(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of AdminNetworks for query {}", query);
        Page<AdminNetworkDTO> page = adminNetworkService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
