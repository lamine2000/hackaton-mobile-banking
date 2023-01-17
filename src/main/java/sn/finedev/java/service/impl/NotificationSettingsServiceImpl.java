package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.NotificationSettings;
import sn.finedev.java.repository.NotificationSettingsRepository;
import sn.finedev.java.repository.search.NotificationSettingsSearchRepository;
import sn.finedev.java.service.NotificationSettingsService;
import sn.finedev.java.service.dto.NotificationSettingsDTO;
import sn.finedev.java.service.mapper.NotificationSettingsMapper;

/**
 * Service Implementation for managing {@link NotificationSettings}.
 */
@Service
@Transactional
public class NotificationSettingsServiceImpl implements NotificationSettingsService {

    private final Logger log = LoggerFactory.getLogger(NotificationSettingsServiceImpl.class);

    private final NotificationSettingsRepository notificationSettingsRepository;

    private final NotificationSettingsMapper notificationSettingsMapper;

    private final NotificationSettingsSearchRepository notificationSettingsSearchRepository;

    public NotificationSettingsServiceImpl(
        NotificationSettingsRepository notificationSettingsRepository,
        NotificationSettingsMapper notificationSettingsMapper,
        NotificationSettingsSearchRepository notificationSettingsSearchRepository
    ) {
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.notificationSettingsMapper = notificationSettingsMapper;
        this.notificationSettingsSearchRepository = notificationSettingsSearchRepository;
    }

    @Override
    public NotificationSettingsDTO save(NotificationSettingsDTO notificationSettingsDTO) {
        log.debug("Request to save NotificationSettings : {}", notificationSettingsDTO);
        NotificationSettings notificationSettings = notificationSettingsMapper.toEntity(notificationSettingsDTO);
        notificationSettings = notificationSettingsRepository.save(notificationSettings);
        NotificationSettingsDTO result = notificationSettingsMapper.toDto(notificationSettings);
        notificationSettingsSearchRepository.index(notificationSettings);
        return result;
    }

    @Override
    public NotificationSettingsDTO update(NotificationSettingsDTO notificationSettingsDTO) {
        log.debug("Request to update NotificationSettings : {}", notificationSettingsDTO);
        NotificationSettings notificationSettings = notificationSettingsMapper.toEntity(notificationSettingsDTO);
        notificationSettings = notificationSettingsRepository.save(notificationSettings);
        NotificationSettingsDTO result = notificationSettingsMapper.toDto(notificationSettings);
        notificationSettingsSearchRepository.index(notificationSettings);
        return result;
    }

    @Override
    public Optional<NotificationSettingsDTO> partialUpdate(NotificationSettingsDTO notificationSettingsDTO) {
        log.debug("Request to partially update NotificationSettings : {}", notificationSettingsDTO);

        return notificationSettingsRepository
            .findById(notificationSettingsDTO.getId())
            .map(existingNotificationSettings -> {
                notificationSettingsMapper.partialUpdate(existingNotificationSettings, notificationSettingsDTO);

                return existingNotificationSettings;
            })
            .map(notificationSettingsRepository::save)
            .map(savedNotificationSettings -> {
                notificationSettingsSearchRepository.save(savedNotificationSettings);

                return savedNotificationSettings;
            })
            .map(notificationSettingsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationSettingsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all NotificationSettings");
        return notificationSettingsRepository.findAll(pageable).map(notificationSettingsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationSettingsDTO> findOne(Long id) {
        log.debug("Request to get NotificationSettings : {}", id);
        return notificationSettingsRepository.findById(id).map(notificationSettingsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete NotificationSettings : {}", id);
        notificationSettingsRepository.deleteById(id);
        notificationSettingsSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationSettingsDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of NotificationSettings for query {}", query);
        return notificationSettingsSearchRepository.search(query, pageable).map(notificationSettingsMapper::toDto);
    }
}
