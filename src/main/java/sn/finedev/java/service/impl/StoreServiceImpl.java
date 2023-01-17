package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.Store;
import sn.finedev.java.repository.StoreRepository;
import sn.finedev.java.repository.search.StoreSearchRepository;
import sn.finedev.java.service.StoreService;
import sn.finedev.java.service.dto.StoreDTO;
import sn.finedev.java.service.mapper.StoreMapper;

/**
 * Service Implementation for managing {@link Store}.
 */
@Service
@Transactional
public class StoreServiceImpl implements StoreService {

    private final Logger log = LoggerFactory.getLogger(StoreServiceImpl.class);

    private final StoreRepository storeRepository;

    private final StoreMapper storeMapper;

    private final StoreSearchRepository storeSearchRepository;

    public StoreServiceImpl(StoreRepository storeRepository, StoreMapper storeMapper, StoreSearchRepository storeSearchRepository) {
        this.storeRepository = storeRepository;
        this.storeMapper = storeMapper;
        this.storeSearchRepository = storeSearchRepository;
    }

    @Override
    public StoreDTO save(StoreDTO storeDTO) {
        log.debug("Request to save Store : {}", storeDTO);
        Store store = storeMapper.toEntity(storeDTO);
        store = storeRepository.save(store);
        StoreDTO result = storeMapper.toDto(store);
        storeSearchRepository.index(store);
        return result;
    }

    @Override
    public StoreDTO update(StoreDTO storeDTO) {
        log.debug("Request to update Store : {}", storeDTO);
        Store store = storeMapper.toEntity(storeDTO);
        store = storeRepository.save(store);
        StoreDTO result = storeMapper.toDto(store);
        storeSearchRepository.index(store);
        return result;
    }

    @Override
    public Optional<StoreDTO> partialUpdate(StoreDTO storeDTO) {
        log.debug("Request to partially update Store : {}", storeDTO);

        return storeRepository
            .findById(storeDTO.getId())
            .map(existingStore -> {
                storeMapper.partialUpdate(existingStore, storeDTO);

                return existingStore;
            })
            .map(storeRepository::save)
            .map(savedStore -> {
                storeSearchRepository.save(savedStore);

                return savedStore;
            })
            .map(storeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoreDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Stores");
        return storeRepository.findAll(pageable).map(storeMapper::toDto);
    }

    public Page<StoreDTO> findAllWithEagerRelationships(Pageable pageable) {
        return storeRepository.findAllWithEagerRelationships(pageable).map(storeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StoreDTO> findOne(Long id) {
        log.debug("Request to get Store : {}", id);
        return storeRepository.findOneWithEagerRelationships(id).map(storeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Store : {}", id);
        storeRepository.deleteById(id);
        storeSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoreDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Stores for query {}", query);
        return storeSearchRepository.search(query, pageable).map(storeMapper::toDto);
    }
}
