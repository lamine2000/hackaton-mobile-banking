package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.FunctionalityCategory;
import sn.finedev.java.repository.FunctionalityCategoryRepository;
import sn.finedev.java.repository.search.FunctionalityCategorySearchRepository;
import sn.finedev.java.service.FunctionalityCategoryService;
import sn.finedev.java.service.dto.FunctionalityCategoryDTO;
import sn.finedev.java.service.mapper.FunctionalityCategoryMapper;

/**
 * Service Implementation for managing {@link FunctionalityCategory}.
 */
@Service
@Transactional
public class FunctionalityCategoryServiceImpl implements FunctionalityCategoryService {

    private final Logger log = LoggerFactory.getLogger(FunctionalityCategoryServiceImpl.class);

    private final FunctionalityCategoryRepository functionalityCategoryRepository;

    private final FunctionalityCategoryMapper functionalityCategoryMapper;

    private final FunctionalityCategorySearchRepository functionalityCategorySearchRepository;

    public FunctionalityCategoryServiceImpl(
        FunctionalityCategoryRepository functionalityCategoryRepository,
        FunctionalityCategoryMapper functionalityCategoryMapper,
        FunctionalityCategorySearchRepository functionalityCategorySearchRepository
    ) {
        this.functionalityCategoryRepository = functionalityCategoryRepository;
        this.functionalityCategoryMapper = functionalityCategoryMapper;
        this.functionalityCategorySearchRepository = functionalityCategorySearchRepository;
    }

    @Override
    public FunctionalityCategoryDTO save(FunctionalityCategoryDTO functionalityCategoryDTO) {
        log.debug("Request to save FunctionalityCategory : {}", functionalityCategoryDTO);
        FunctionalityCategory functionalityCategory = functionalityCategoryMapper.toEntity(functionalityCategoryDTO);
        functionalityCategory = functionalityCategoryRepository.save(functionalityCategory);
        FunctionalityCategoryDTO result = functionalityCategoryMapper.toDto(functionalityCategory);
        functionalityCategorySearchRepository.index(functionalityCategory);
        return result;
    }

    @Override
    public FunctionalityCategoryDTO update(FunctionalityCategoryDTO functionalityCategoryDTO) {
        log.debug("Request to update FunctionalityCategory : {}", functionalityCategoryDTO);
        FunctionalityCategory functionalityCategory = functionalityCategoryMapper.toEntity(functionalityCategoryDTO);
        functionalityCategory = functionalityCategoryRepository.save(functionalityCategory);
        FunctionalityCategoryDTO result = functionalityCategoryMapper.toDto(functionalityCategory);
        functionalityCategorySearchRepository.index(functionalityCategory);
        return result;
    }

    @Override
    public Optional<FunctionalityCategoryDTO> partialUpdate(FunctionalityCategoryDTO functionalityCategoryDTO) {
        log.debug("Request to partially update FunctionalityCategory : {}", functionalityCategoryDTO);

        return functionalityCategoryRepository
            .findById(functionalityCategoryDTO.getId())
            .map(existingFunctionalityCategory -> {
                functionalityCategoryMapper.partialUpdate(existingFunctionalityCategory, functionalityCategoryDTO);

                return existingFunctionalityCategory;
            })
            .map(functionalityCategoryRepository::save)
            .map(savedFunctionalityCategory -> {
                functionalityCategorySearchRepository.save(savedFunctionalityCategory);

                return savedFunctionalityCategory;
            })
            .map(functionalityCategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FunctionalityCategoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FunctionalityCategories");
        return functionalityCategoryRepository.findAll(pageable).map(functionalityCategoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FunctionalityCategoryDTO> findOne(Long id) {
        log.debug("Request to get FunctionalityCategory : {}", id);
        return functionalityCategoryRepository.findById(id).map(functionalityCategoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete FunctionalityCategory : {}", id);
        functionalityCategoryRepository.deleteById(id);
        functionalityCategorySearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FunctionalityCategoryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of FunctionalityCategories for query {}", query);
        return functionalityCategorySearchRepository.search(query, pageable).map(functionalityCategoryMapper::toDto);
    }
}
