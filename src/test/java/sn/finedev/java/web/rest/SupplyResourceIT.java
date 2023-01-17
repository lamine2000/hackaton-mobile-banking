package sn.finedev.java.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.IntegrationTest;
import sn.finedev.java.domain.Supply;
import sn.finedev.java.domain.SupplyRequest;
import sn.finedev.java.repository.SupplyRepository;
import sn.finedev.java.repository.search.SupplySearchRepository;
import sn.finedev.java.service.criteria.SupplyCriteria;
import sn.finedev.java.service.dto.SupplyDTO;
import sn.finedev.java.service.mapper.SupplyMapper;

/**
 * Integration tests for the {@link SupplyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SupplyResourceIT {

    private static final String DEFAULT_RECEIVER = "AAAAAAAAAA";
    private static final String UPDATED_RECEIVER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/supplies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/supplies";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private SupplyMapper supplyMapper;

    @Autowired
    private SupplySearchRepository supplySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSupplyMockMvc;

    private Supply supply;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Supply createEntity(EntityManager em) {
        Supply supply = new Supply().receiver(DEFAULT_RECEIVER);
        return supply;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Supply createUpdatedEntity(EntityManager em) {
        Supply supply = new Supply().receiver(UPDATED_RECEIVER);
        return supply;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        supplySearchRepository.deleteAll();
        assertThat(supplySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        supply = createEntity(em);
    }

    @Test
    @Transactional
    void createSupply() throws Exception {
        int databaseSizeBeforeCreate = supplyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplySearchRepository.findAll());
        // Create the Supply
        SupplyDTO supplyDTO = supplyMapper.toDto(supply);
        restSupplyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(supplyDTO)))
            .andExpect(status().isCreated());

        // Validate the Supply in the database
        List<Supply> supplyList = supplyRepository.findAll();
        assertThat(supplyList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Supply testSupply = supplyList.get(supplyList.size() - 1);
        assertThat(testSupply.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
    }

    @Test
    @Transactional
    void createSupplyWithExistingId() throws Exception {
        // Create the Supply with an existing ID
        supply.setId(1L);
        SupplyDTO supplyDTO = supplyMapper.toDto(supply);

        int databaseSizeBeforeCreate = supplyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSupplyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(supplyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Supply in the database
        List<Supply> supplyList = supplyRepository.findAll();
        assertThat(supplyList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReceiverIsRequired() throws Exception {
        int databaseSizeBeforeTest = supplyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplySearchRepository.findAll());
        // set the field null
        supply.setReceiver(null);

        // Create the Supply, which fails.
        SupplyDTO supplyDTO = supplyMapper.toDto(supply);

        restSupplyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(supplyDTO)))
            .andExpect(status().isBadRequest());

        List<Supply> supplyList = supplyRepository.findAll();
        assertThat(supplyList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSupplies() throws Exception {
        // Initialize the database
        supplyRepository.saveAndFlush(supply);

        // Get all the supplyList
        restSupplyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supply.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER)));
    }

    @Test
    @Transactional
    void getSupply() throws Exception {
        // Initialize the database
        supplyRepository.saveAndFlush(supply);

        // Get the supply
        restSupplyMockMvc
            .perform(get(ENTITY_API_URL_ID, supply.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(supply.getId().intValue()))
            .andExpect(jsonPath("$.receiver").value(DEFAULT_RECEIVER));
    }

    @Test
    @Transactional
    void getSuppliesByIdFiltering() throws Exception {
        // Initialize the database
        supplyRepository.saveAndFlush(supply);

        Long id = supply.getId();

        defaultSupplyShouldBeFound("id.equals=" + id);
        defaultSupplyShouldNotBeFound("id.notEquals=" + id);

        defaultSupplyShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSupplyShouldNotBeFound("id.greaterThan=" + id);

        defaultSupplyShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSupplyShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSuppliesByReceiverIsEqualToSomething() throws Exception {
        // Initialize the database
        supplyRepository.saveAndFlush(supply);

        // Get all the supplyList where receiver equals to DEFAULT_RECEIVER
        defaultSupplyShouldBeFound("receiver.equals=" + DEFAULT_RECEIVER);

        // Get all the supplyList where receiver equals to UPDATED_RECEIVER
        defaultSupplyShouldNotBeFound("receiver.equals=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllSuppliesByReceiverIsInShouldWork() throws Exception {
        // Initialize the database
        supplyRepository.saveAndFlush(supply);

        // Get all the supplyList where receiver in DEFAULT_RECEIVER or UPDATED_RECEIVER
        defaultSupplyShouldBeFound("receiver.in=" + DEFAULT_RECEIVER + "," + UPDATED_RECEIVER);

        // Get all the supplyList where receiver equals to UPDATED_RECEIVER
        defaultSupplyShouldNotBeFound("receiver.in=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllSuppliesByReceiverIsNullOrNotNull() throws Exception {
        // Initialize the database
        supplyRepository.saveAndFlush(supply);

        // Get all the supplyList where receiver is not null
        defaultSupplyShouldBeFound("receiver.specified=true");

        // Get all the supplyList where receiver is null
        defaultSupplyShouldNotBeFound("receiver.specified=false");
    }

    @Test
    @Transactional
    void getAllSuppliesByReceiverContainsSomething() throws Exception {
        // Initialize the database
        supplyRepository.saveAndFlush(supply);

        // Get all the supplyList where receiver contains DEFAULT_RECEIVER
        defaultSupplyShouldBeFound("receiver.contains=" + DEFAULT_RECEIVER);

        // Get all the supplyList where receiver contains UPDATED_RECEIVER
        defaultSupplyShouldNotBeFound("receiver.contains=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllSuppliesByReceiverNotContainsSomething() throws Exception {
        // Initialize the database
        supplyRepository.saveAndFlush(supply);

        // Get all the supplyList where receiver does not contain DEFAULT_RECEIVER
        defaultSupplyShouldNotBeFound("receiver.doesNotContain=" + DEFAULT_RECEIVER);

        // Get all the supplyList where receiver does not contain UPDATED_RECEIVER
        defaultSupplyShouldBeFound("receiver.doesNotContain=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllSuppliesBySupplyRequestIsEqualToSomething() throws Exception {
        SupplyRequest supplyRequest;
        if (TestUtil.findAll(em, SupplyRequest.class).isEmpty()) {
            supplyRepository.saveAndFlush(supply);
            supplyRequest = SupplyRequestResourceIT.createEntity(em);
        } else {
            supplyRequest = TestUtil.findAll(em, SupplyRequest.class).get(0);
        }
        em.persist(supplyRequest);
        em.flush();
        supply.setSupplyRequest(supplyRequest);
        supplyRepository.saveAndFlush(supply);
        Long supplyRequestId = supplyRequest.getId();

        // Get all the supplyList where supplyRequest equals to supplyRequestId
        defaultSupplyShouldBeFound("supplyRequestId.equals=" + supplyRequestId);

        // Get all the supplyList where supplyRequest equals to (supplyRequestId + 1)
        defaultSupplyShouldNotBeFound("supplyRequestId.equals=" + (supplyRequestId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSupplyShouldBeFound(String filter) throws Exception {
        restSupplyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supply.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER)));

        // Check, that the count call also returns 1
        restSupplyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSupplyShouldNotBeFound(String filter) throws Exception {
        restSupplyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSupplyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSupply() throws Exception {
        // Get the supply
        restSupplyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSupply() throws Exception {
        // Initialize the database
        supplyRepository.saveAndFlush(supply);

        int databaseSizeBeforeUpdate = supplyRepository.findAll().size();
        supplySearchRepository.save(supply);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplySearchRepository.findAll());

        // Update the supply
        Supply updatedSupply = supplyRepository.findById(supply.getId()).get();
        // Disconnect from session so that the updates on updatedSupply are not directly saved in db
        em.detach(updatedSupply);
        updatedSupply.receiver(UPDATED_RECEIVER);
        SupplyDTO supplyDTO = supplyMapper.toDto(updatedSupply);

        restSupplyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supplyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(supplyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Supply in the database
        List<Supply> supplyList = supplyRepository.findAll();
        assertThat(supplyList).hasSize(databaseSizeBeforeUpdate);
        Supply testSupply = supplyList.get(supplyList.size() - 1);
        assertThat(testSupply.getReceiver()).isEqualTo(UPDATED_RECEIVER);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Supply> supplySearchList = IterableUtils.toList(supplySearchRepository.findAll());
                Supply testSupplySearch = supplySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSupplySearch.getReceiver()).isEqualTo(UPDATED_RECEIVER);
            });
    }

    @Test
    @Transactional
    void putNonExistingSupply() throws Exception {
        int databaseSizeBeforeUpdate = supplyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplySearchRepository.findAll());
        supply.setId(count.incrementAndGet());

        // Create the Supply
        SupplyDTO supplyDTO = supplyMapper.toDto(supply);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supplyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(supplyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supply in the database
        List<Supply> supplyList = supplyRepository.findAll();
        assertThat(supplyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSupply() throws Exception {
        int databaseSizeBeforeUpdate = supplyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplySearchRepository.findAll());
        supply.setId(count.incrementAndGet());

        // Create the Supply
        SupplyDTO supplyDTO = supplyMapper.toDto(supply);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(supplyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supply in the database
        List<Supply> supplyList = supplyRepository.findAll();
        assertThat(supplyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSupply() throws Exception {
        int databaseSizeBeforeUpdate = supplyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplySearchRepository.findAll());
        supply.setId(count.incrementAndGet());

        // Create the Supply
        SupplyDTO supplyDTO = supplyMapper.toDto(supply);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(supplyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Supply in the database
        List<Supply> supplyList = supplyRepository.findAll();
        assertThat(supplyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSupplyWithPatch() throws Exception {
        // Initialize the database
        supplyRepository.saveAndFlush(supply);

        int databaseSizeBeforeUpdate = supplyRepository.findAll().size();

        // Update the supply using partial update
        Supply partialUpdatedSupply = new Supply();
        partialUpdatedSupply.setId(supply.getId());

        partialUpdatedSupply.receiver(UPDATED_RECEIVER);

        restSupplyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupply.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSupply))
            )
            .andExpect(status().isOk());

        // Validate the Supply in the database
        List<Supply> supplyList = supplyRepository.findAll();
        assertThat(supplyList).hasSize(databaseSizeBeforeUpdate);
        Supply testSupply = supplyList.get(supplyList.size() - 1);
        assertThat(testSupply.getReceiver()).isEqualTo(UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void fullUpdateSupplyWithPatch() throws Exception {
        // Initialize the database
        supplyRepository.saveAndFlush(supply);

        int databaseSizeBeforeUpdate = supplyRepository.findAll().size();

        // Update the supply using partial update
        Supply partialUpdatedSupply = new Supply();
        partialUpdatedSupply.setId(supply.getId());

        partialUpdatedSupply.receiver(UPDATED_RECEIVER);

        restSupplyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupply.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSupply))
            )
            .andExpect(status().isOk());

        // Validate the Supply in the database
        List<Supply> supplyList = supplyRepository.findAll();
        assertThat(supplyList).hasSize(databaseSizeBeforeUpdate);
        Supply testSupply = supplyList.get(supplyList.size() - 1);
        assertThat(testSupply.getReceiver()).isEqualTo(UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void patchNonExistingSupply() throws Exception {
        int databaseSizeBeforeUpdate = supplyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplySearchRepository.findAll());
        supply.setId(count.incrementAndGet());

        // Create the Supply
        SupplyDTO supplyDTO = supplyMapper.toDto(supply);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, supplyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(supplyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supply in the database
        List<Supply> supplyList = supplyRepository.findAll();
        assertThat(supplyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSupply() throws Exception {
        int databaseSizeBeforeUpdate = supplyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplySearchRepository.findAll());
        supply.setId(count.incrementAndGet());

        // Create the Supply
        SupplyDTO supplyDTO = supplyMapper.toDto(supply);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(supplyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Supply in the database
        List<Supply> supplyList = supplyRepository.findAll();
        assertThat(supplyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSupply() throws Exception {
        int databaseSizeBeforeUpdate = supplyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplySearchRepository.findAll());
        supply.setId(count.incrementAndGet());

        // Create the Supply
        SupplyDTO supplyDTO = supplyMapper.toDto(supply);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(supplyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Supply in the database
        List<Supply> supplyList = supplyRepository.findAll();
        assertThat(supplyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSupply() throws Exception {
        // Initialize the database
        supplyRepository.saveAndFlush(supply);
        supplyRepository.save(supply);
        supplySearchRepository.save(supply);

        int databaseSizeBeforeDelete = supplyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(supplySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the supply
        restSupplyMockMvc
            .perform(delete(ENTITY_API_URL_ID, supply.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Supply> supplyList = supplyRepository.findAll();
        assertThat(supplyList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(supplySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSupply() throws Exception {
        // Initialize the database
        supply = supplyRepository.saveAndFlush(supply);
        supplySearchRepository.save(supply);

        // Search the supply
        restSupplyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + supply.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supply.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER)));
    }
}
