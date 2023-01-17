package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.FinalAgent;
import sn.finedev.java.repository.FinalAgentRepository;
import sn.finedev.java.repository.search.FinalAgentSearchRepository;
import sn.finedev.java.service.FinalAgentService;
import sn.finedev.java.service.dto.FinalAgentDTO;
import sn.finedev.java.service.mapper.FinalAgentMapper;

/**
 * Service Implementation for managing {@link FinalAgent}.
 */
@Service
@Transactional
public class FinalAgentServiceImpl implements FinalAgentService {

    private final Logger log = LoggerFactory.getLogger(FinalAgentServiceImpl.class);

    private final FinalAgentRepository finalAgentRepository;

    private final FinalAgentMapper finalAgentMapper;

    private final FinalAgentSearchRepository finalAgentSearchRepository;

    public FinalAgentServiceImpl(
        FinalAgentRepository finalAgentRepository,
        FinalAgentMapper finalAgentMapper,
        FinalAgentSearchRepository finalAgentSearchRepository
    ) {
        this.finalAgentRepository = finalAgentRepository;
        this.finalAgentMapper = finalAgentMapper;
        this.finalAgentSearchRepository = finalAgentSearchRepository;
    }

    @Override
    public FinalAgentDTO save(FinalAgentDTO finalAgentDTO) {
        log.debug("Request to save FinalAgent : {}", finalAgentDTO);
        FinalAgent finalAgent = finalAgentMapper.toEntity(finalAgentDTO);
        finalAgent = finalAgentRepository.save(finalAgent);
        FinalAgentDTO result = finalAgentMapper.toDto(finalAgent);
        finalAgentSearchRepository.index(finalAgent);
        return result;
    }

    @Override
    public FinalAgentDTO update(FinalAgentDTO finalAgentDTO) {
        log.debug("Request to update FinalAgent : {}", finalAgentDTO);
        FinalAgent finalAgent = finalAgentMapper.toEntity(finalAgentDTO);
        finalAgent = finalAgentRepository.save(finalAgent);
        FinalAgentDTO result = finalAgentMapper.toDto(finalAgent);
        finalAgentSearchRepository.index(finalAgent);
        return result;
    }

    @Override
    public Optional<FinalAgentDTO> partialUpdate(FinalAgentDTO finalAgentDTO) {
        log.debug("Request to partially update FinalAgent : {}", finalAgentDTO);

        return finalAgentRepository
            .findById(finalAgentDTO.getId())
            .map(existingFinalAgent -> {
                finalAgentMapper.partialUpdate(existingFinalAgent, finalAgentDTO);

                return existingFinalAgent;
            })
            .map(finalAgentRepository::save)
            .map(savedFinalAgent -> {
                finalAgentSearchRepository.save(savedFinalAgent);

                return savedFinalAgent;
            })
            .map(finalAgentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinalAgentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FinalAgents");
        return finalAgentRepository.findAll(pageable).map(finalAgentMapper::toDto);
    }

    public Page<FinalAgentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return finalAgentRepository.findAllWithEagerRelationships(pageable).map(finalAgentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FinalAgentDTO> findOne(Long id) {
        log.debug("Request to get FinalAgent : {}", id);
        return finalAgentRepository.findOneWithEagerRelationships(id).map(finalAgentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete FinalAgent : {}", id);
        finalAgentRepository.deleteById(id);
        finalAgentSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinalAgentDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of FinalAgents for query {}", query);
        return finalAgentSearchRepository.search(query, pageable).map(finalAgentMapper::toDto);
    }
}
