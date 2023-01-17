package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.Functionality;
import sn.finedev.java.repository.FunctionalityRepository;
import sn.finedev.java.repository.search.FunctionalitySearchRepository;
import sn.finedev.java.service.FunctionalityService;
import sn.finedev.java.service.dto.FunctionalityDTO;
import sn.finedev.java.service.mapper.FunctionalityMapper;

/**
 * Service Implementation for managing {@link Functionality}.
 */
@Service
@Transactional
public class FunctionalityServiceImpl implements FunctionalityService {

    private final Logger log = LoggerFactory.getLogger(FunctionalityServiceImpl.class);

    private final FunctionalityRepository functionalityRepository;

    private final FunctionalityMapper functionalityMapper;

    private final FunctionalitySearchRepository functionalitySearchRepository;

    public FunctionalityServiceImpl(
        FunctionalityRepository functionalityRepository,
        FunctionalityMapper functionalityMapper,
        FunctionalitySearchRepository functionalitySearchRepository
    ) {
        this.functionalityRepository = functionalityRepository;
        this.functionalityMapper = functionalityMapper;
        this.functionalitySearchRepository = functionalitySearchRepository;
    }

    @Override
    public FunctionalityDTO save(FunctionalityDTO functionalityDTO) {
        log.debug("Request to save Functionality : {}", functionalityDTO);
        Functionality functionality = functionalityMapper.toEntity(functionalityDTO);
        functionality = functionalityRepository.save(functionality);
        FunctionalityDTO result = functionalityMapper.toDto(functionality);
        functionalitySearchRepository.index(functionality);
        return result;
    }

    @Override
    public FunctionalityDTO update(FunctionalityDTO functionalityDTO) {
        log.debug("Request to update Functionality : {}", functionalityDTO);
        Functionality functionality = functionalityMapper.toEntity(functionalityDTO);
        functionality = functionalityRepository.save(functionality);
        FunctionalityDTO result = functionalityMapper.toDto(functionality);
        functionalitySearchRepository.index(functionality);
        return result;
    }

    @Override
    public Optional<FunctionalityDTO> partialUpdate(FunctionalityDTO functionalityDTO) {
        log.debug("Request to partially update Functionality : {}", functionalityDTO);

        return functionalityRepository
            .findById(functionalityDTO.getId())
            .map(existingFunctionality -> {
                functionalityMapper.partialUpdate(existingFunctionality, functionalityDTO);

                return existingFunctionality;
            })
            .map(functionalityRepository::save)
            .map(savedFunctionality -> {
                functionalitySearchRepository.save(savedFunctionality);

                return savedFunctionality;
            })
            .map(functionalityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FunctionalityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Functionalities");
        return functionalityRepository.findAll(pageable).map(functionalityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FunctionalityDTO> findOne(Long id) {
        log.debug("Request to get Functionality : {}", id);
        return functionalityRepository.findById(id).map(functionalityMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Functionality : {}", id);
        functionalityRepository.deleteById(id);
        functionalitySearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FunctionalityDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Functionalities for query {}", query);
        return functionalitySearchRepository.search(query, pageable).map(functionalityMapper::toDto);
    }
}
