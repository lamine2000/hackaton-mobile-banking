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
import org.springframework.util.Base64Utils;
import sn.finedev.java.IntegrationTest;
import sn.finedev.java.domain.NotificationSettings;
import sn.finedev.java.repository.NotificationSettingsRepository;
import sn.finedev.java.repository.search.NotificationSettingsSearchRepository;
import sn.finedev.java.service.criteria.NotificationSettingsCriteria;
import sn.finedev.java.service.dto.NotificationSettingsDTO;
import sn.finedev.java.service.mapper.NotificationSettingsMapper;

/**
 * Integration tests for the {@link NotificationSettingsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationSettingsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/notification-settings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/notification-settings";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NotificationSettingsRepository notificationSettingsRepository;

    @Autowired
    private NotificationSettingsMapper notificationSettingsMapper;

    @Autowired
    private NotificationSettingsSearchRepository notificationSettingsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationSettingsMockMvc;

    private NotificationSettings notificationSettings;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationSettings createEntity(EntityManager em) {
        NotificationSettings notificationSettings = new NotificationSettings()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .value(DEFAULT_VALUE);
        return notificationSettings;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationSettings createUpdatedEntity(EntityManager em) {
        NotificationSettings notificationSettings = new NotificationSettings()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .value(UPDATED_VALUE);
        return notificationSettings;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        notificationSettingsSearchRepository.deleteAll();
        assertThat(notificationSettingsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        notificationSettings = createEntity(em);
    }

    @Test
    @Transactional
    void createNotificationSettings() throws Exception {
        int databaseSizeBeforeCreate = notificationSettingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        // Create the NotificationSettings
        NotificationSettingsDTO notificationSettingsDTO = notificationSettingsMapper.toDto(notificationSettings);
        restNotificationSettingsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationSettingsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the NotificationSettings in the database
        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        NotificationSettings testNotificationSettings = notificationSettingsList.get(notificationSettingsList.size() - 1);
        assertThat(testNotificationSettings.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testNotificationSettings.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testNotificationSettings.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    void createNotificationSettingsWithExistingId() throws Exception {
        // Create the NotificationSettings with an existing ID
        notificationSettings.setId(1L);
        NotificationSettingsDTO notificationSettingsDTO = notificationSettingsMapper.toDto(notificationSettings);

        int databaseSizeBeforeCreate = notificationSettingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationSettingsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationSettingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationSettings in the database
        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationSettingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        // set the field null
        notificationSettings.setName(null);

        // Create the NotificationSettings, which fails.
        NotificationSettingsDTO notificationSettingsDTO = notificationSettingsMapper.toDto(notificationSettings);

        restNotificationSettingsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationSettingsDTO))
            )
            .andExpect(status().isBadRequest());

        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllNotificationSettings() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        // Get all the notificationSettingsList
        restNotificationSettingsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationSettings.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    void getNotificationSettings() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        // Get the notificationSettings
        restNotificationSettingsMockMvc
            .perform(get(ENTITY_API_URL_ID, notificationSettings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notificationSettings.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    void getNotificationSettingsByIdFiltering() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        Long id = notificationSettings.getId();

        defaultNotificationSettingsShouldBeFound("id.equals=" + id);
        defaultNotificationSettingsShouldNotBeFound("id.notEquals=" + id);

        defaultNotificationSettingsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultNotificationSettingsShouldNotBeFound("id.greaterThan=" + id);

        defaultNotificationSettingsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultNotificationSettingsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllNotificationSettingsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        // Get all the notificationSettingsList where name equals to DEFAULT_NAME
        defaultNotificationSettingsShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the notificationSettingsList where name equals to UPDATED_NAME
        defaultNotificationSettingsShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllNotificationSettingsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        // Get all the notificationSettingsList where name in DEFAULT_NAME or UPDATED_NAME
        defaultNotificationSettingsShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the notificationSettingsList where name equals to UPDATED_NAME
        defaultNotificationSettingsShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllNotificationSettingsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        // Get all the notificationSettingsList where name is not null
        defaultNotificationSettingsShouldBeFound("name.specified=true");

        // Get all the notificationSettingsList where name is null
        defaultNotificationSettingsShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationSettingsByNameContainsSomething() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        // Get all the notificationSettingsList where name contains DEFAULT_NAME
        defaultNotificationSettingsShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the notificationSettingsList where name contains UPDATED_NAME
        defaultNotificationSettingsShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllNotificationSettingsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        // Get all the notificationSettingsList where name does not contain DEFAULT_NAME
        defaultNotificationSettingsShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the notificationSettingsList where name does not contain UPDATED_NAME
        defaultNotificationSettingsShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllNotificationSettingsByValueIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        // Get all the notificationSettingsList where value equals to DEFAULT_VALUE
        defaultNotificationSettingsShouldBeFound("value.equals=" + DEFAULT_VALUE);

        // Get all the notificationSettingsList where value equals to UPDATED_VALUE
        defaultNotificationSettingsShouldNotBeFound("value.equals=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllNotificationSettingsByValueIsInShouldWork() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        // Get all the notificationSettingsList where value in DEFAULT_VALUE or UPDATED_VALUE
        defaultNotificationSettingsShouldBeFound("value.in=" + DEFAULT_VALUE + "," + UPDATED_VALUE);

        // Get all the notificationSettingsList where value equals to UPDATED_VALUE
        defaultNotificationSettingsShouldNotBeFound("value.in=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllNotificationSettingsByValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        // Get all the notificationSettingsList where value is not null
        defaultNotificationSettingsShouldBeFound("value.specified=true");

        // Get all the notificationSettingsList where value is null
        defaultNotificationSettingsShouldNotBeFound("value.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationSettingsByValueContainsSomething() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        // Get all the notificationSettingsList where value contains DEFAULT_VALUE
        defaultNotificationSettingsShouldBeFound("value.contains=" + DEFAULT_VALUE);

        // Get all the notificationSettingsList where value contains UPDATED_VALUE
        defaultNotificationSettingsShouldNotBeFound("value.contains=" + UPDATED_VALUE);
    }

    @Test
    @Transactional
    void getAllNotificationSettingsByValueNotContainsSomething() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        // Get all the notificationSettingsList where value does not contain DEFAULT_VALUE
        defaultNotificationSettingsShouldNotBeFound("value.doesNotContain=" + DEFAULT_VALUE);

        // Get all the notificationSettingsList where value does not contain UPDATED_VALUE
        defaultNotificationSettingsShouldBeFound("value.doesNotContain=" + UPDATED_VALUE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationSettingsShouldBeFound(String filter) throws Exception {
        restNotificationSettingsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationSettings.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));

        // Check, that the count call also returns 1
        restNotificationSettingsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationSettingsShouldNotBeFound(String filter) throws Exception {
        restNotificationSettingsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationSettingsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingNotificationSettings() throws Exception {
        // Get the notificationSettings
        restNotificationSettingsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotificationSettings() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        int databaseSizeBeforeUpdate = notificationSettingsRepository.findAll().size();
        notificationSettingsSearchRepository.save(notificationSettings);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());

        // Update the notificationSettings
        NotificationSettings updatedNotificationSettings = notificationSettingsRepository.findById(notificationSettings.getId()).get();
        // Disconnect from session so that the updates on updatedNotificationSettings are not directly saved in db
        em.detach(updatedNotificationSettings);
        updatedNotificationSettings.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).value(UPDATED_VALUE);
        NotificationSettingsDTO notificationSettingsDTO = notificationSettingsMapper.toDto(updatedNotificationSettings);

        restNotificationSettingsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationSettingsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationSettingsDTO))
            )
            .andExpect(status().isOk());

        // Validate the NotificationSettings in the database
        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(databaseSizeBeforeUpdate);
        NotificationSettings testNotificationSettings = notificationSettingsList.get(notificationSettingsList.size() - 1);
        assertThat(testNotificationSettings.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testNotificationSettings.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testNotificationSettings.getValue()).isEqualTo(UPDATED_VALUE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<NotificationSettings> notificationSettingsSearchList = IterableUtils.toList(
                    notificationSettingsSearchRepository.findAll()
                );
                NotificationSettings testNotificationSettingsSearch = notificationSettingsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testNotificationSettingsSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testNotificationSettingsSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testNotificationSettingsSearch.getValue()).isEqualTo(UPDATED_VALUE);
            });
    }

    @Test
    @Transactional
    void putNonExistingNotificationSettings() throws Exception {
        int databaseSizeBeforeUpdate = notificationSettingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        notificationSettings.setId(count.incrementAndGet());

        // Create the NotificationSettings
        NotificationSettingsDTO notificationSettingsDTO = notificationSettingsMapper.toDto(notificationSettings);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationSettingsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationSettingsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationSettingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationSettings in the database
        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotificationSettings() throws Exception {
        int databaseSizeBeforeUpdate = notificationSettingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        notificationSettings.setId(count.incrementAndGet());

        // Create the NotificationSettings
        NotificationSettingsDTO notificationSettingsDTO = notificationSettingsMapper.toDto(notificationSettings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationSettingsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationSettingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationSettings in the database
        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotificationSettings() throws Exception {
        int databaseSizeBeforeUpdate = notificationSettingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        notificationSettings.setId(count.incrementAndGet());

        // Create the NotificationSettings
        NotificationSettingsDTO notificationSettingsDTO = notificationSettingsMapper.toDto(notificationSettings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationSettingsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationSettingsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationSettings in the database
        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateNotificationSettingsWithPatch() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        int databaseSizeBeforeUpdate = notificationSettingsRepository.findAll().size();

        // Update the notificationSettings using partial update
        NotificationSettings partialUpdatedNotificationSettings = new NotificationSettings();
        partialUpdatedNotificationSettings.setId(notificationSettings.getId());

        partialUpdatedNotificationSettings.value(UPDATED_VALUE);

        restNotificationSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationSettings.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotificationSettings))
            )
            .andExpect(status().isOk());

        // Validate the NotificationSettings in the database
        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(databaseSizeBeforeUpdate);
        NotificationSettings testNotificationSettings = notificationSettingsList.get(notificationSettingsList.size() - 1);
        assertThat(testNotificationSettings.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testNotificationSettings.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testNotificationSettings.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateNotificationSettingsWithPatch() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);

        int databaseSizeBeforeUpdate = notificationSettingsRepository.findAll().size();

        // Update the notificationSettings using partial update
        NotificationSettings partialUpdatedNotificationSettings = new NotificationSettings();
        partialUpdatedNotificationSettings.setId(notificationSettings.getId());

        partialUpdatedNotificationSettings.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).value(UPDATED_VALUE);

        restNotificationSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationSettings.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotificationSettings))
            )
            .andExpect(status().isOk());

        // Validate the NotificationSettings in the database
        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(databaseSizeBeforeUpdate);
        NotificationSettings testNotificationSettings = notificationSettingsList.get(notificationSettingsList.size() - 1);
        assertThat(testNotificationSettings.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testNotificationSettings.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testNotificationSettings.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingNotificationSettings() throws Exception {
        int databaseSizeBeforeUpdate = notificationSettingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        notificationSettings.setId(count.incrementAndGet());

        // Create the NotificationSettings
        NotificationSettingsDTO notificationSettingsDTO = notificationSettingsMapper.toDto(notificationSettings);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationSettingsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationSettingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationSettings in the database
        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotificationSettings() throws Exception {
        int databaseSizeBeforeUpdate = notificationSettingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        notificationSettings.setId(count.incrementAndGet());

        // Create the NotificationSettings
        NotificationSettingsDTO notificationSettingsDTO = notificationSettingsMapper.toDto(notificationSettings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationSettingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationSettings in the database
        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotificationSettings() throws Exception {
        int databaseSizeBeforeUpdate = notificationSettingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        notificationSettings.setId(count.incrementAndGet());

        // Create the NotificationSettings
        NotificationSettingsDTO notificationSettingsDTO = notificationSettingsMapper.toDto(notificationSettings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationSettingsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationSettingsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationSettings in the database
        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteNotificationSettings() throws Exception {
        // Initialize the database
        notificationSettingsRepository.saveAndFlush(notificationSettings);
        notificationSettingsRepository.save(notificationSettings);
        notificationSettingsSearchRepository.save(notificationSettings);

        int databaseSizeBeforeDelete = notificationSettingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the notificationSettings
        restNotificationSettingsMockMvc
            .perform(delete(ENTITY_API_URL_ID, notificationSettings.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<NotificationSettings> notificationSettingsList = notificationSettingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationSettingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchNotificationSettings() throws Exception {
        // Initialize the database
        notificationSettings = notificationSettingsRepository.saveAndFlush(notificationSettings);
        notificationSettingsSearchRepository.save(notificationSettings);

        // Search the notificationSettings
        restNotificationSettingsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + notificationSettings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationSettings.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }
}
