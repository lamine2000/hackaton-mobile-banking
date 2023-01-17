package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.MobileBankingActor;
import sn.finedev.java.repository.MobileBankingActorRepository;
import sn.finedev.java.repository.search.MobileBankingActorSearchRepository;
import sn.finedev.java.service.MobileBankingActorService;
import sn.finedev.java.service.dto.MobileBankingActorDTO;
import sn.finedev.java.service.mapper.MobileBankingActorMapper;

/**
 * Service Implementation for managing {@link MobileBankingActor}.
 */
@Service
@Transactional
public class MobileBankingActorServiceImpl implements MobileBankingActorService {

    private final Logger log = LoggerFactory.getLogger(MobileBankingActorServiceImpl.class);

    private final MobileBankingActorRepository mobileBankingActorRepository;

    private final MobileBankingActorMapper mobileBankingActorMapper;

    private final MobileBankingActorSearchRepository mobileBankingActorSearchRepository;

    public MobileBankingActorServiceImpl(
        MobileBankingActorRepository mobileBankingActorRepository,
        MobileBankingActorMapper mobileBankingActorMapper,
        MobileBankingActorSearchRepository mobileBankingActorSearchRepository
    ) {
        this.mobileBankingActorRepository = mobileBankingActorRepository;
        this.mobileBankingActorMapper = mobileBankingActorMapper;
        this.mobileBankingActorSearchRepository = mobileBankingActorSearchRepository;
    }

    @Override
    public MobileBankingActorDTO save(MobileBankingActorDTO mobileBankingActorDTO) {
        log.debug("Request to save MobileBankingActor : {}", mobileBankingActorDTO);
        MobileBankingActor mobileBankingActor = mobileBankingActorMapper.toEntity(mobileBankingActorDTO);
        mobileBankingActor = mobileBankingActorRepository.save(mobileBankingActor);
        MobileBankingActorDTO result = mobileBankingActorMapper.toDto(mobileBankingActor);
        mobileBankingActorSearchRepository.index(mobileBankingActor);
        return result;
    }

    @Override
    public MobileBankingActorDTO update(MobileBankingActorDTO mobileBankingActorDTO) {
        log.debug("Request to update MobileBankingActor : {}", mobileBankingActorDTO);
        MobileBankingActor mobileBankingActor = mobileBankingActorMapper.toEntity(mobileBankingActorDTO);
        mobileBankingActor = mobileBankingActorRepository.save(mobileBankingActor);
        MobileBankingActorDTO result = mobileBankingActorMapper.toDto(mobileBankingActor);
        mobileBankingActorSearchRepository.index(mobileBankingActor);
        return result;
    }

    @Override
    public Optional<MobileBankingActorDTO> partialUpdate(MobileBankingActorDTO mobileBankingActorDTO) {
        log.debug("Request to partially update MobileBankingActor : {}", mobileBankingActorDTO);

        return mobileBankingActorRepository
            .findById(mobileBankingActorDTO.getId())
            .map(existingMobileBankingActor -> {
                mobileBankingActorMapper.partialUpdate(existingMobileBankingActor, mobileBankingActorDTO);

                return existingMobileBankingActor;
            })
            .map(mobileBankingActorRepository::save)
            .map(savedMobileBankingActor -> {
                mobileBankingActorSearchRepository.save(savedMobileBankingActor);

                return savedMobileBankingActor;
            })
            .map(mobileBankingActorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MobileBankingActorDTO> findAll(Pageable pageable) {
        log.debug("Request to get all MobileBankingActors");
        return mobileBankingActorRepository.findAll(pageable).map(mobileBankingActorMapper::toDto);
    }

    public Page<MobileBankingActorDTO> findAllWithEagerRelationships(Pageable pageable) {
        return mobileBankingActorRepository.findAllWithEagerRelationships(pageable).map(mobileBankingActorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MobileBankingActorDTO> findOne(Long id) {
        log.debug("Request to get MobileBankingActor : {}", id);
        return mobileBankingActorRepository.findOneWithEagerRelationships(id).map(mobileBankingActorMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete MobileBankingActor : {}", id);
        mobileBankingActorRepository.deleteById(id);
        mobileBankingActorSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MobileBankingActorDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MobileBankingActors for query {}", query);
        return mobileBankingActorSearchRepository.search(query, pageable).map(mobileBankingActorMapper::toDto);
    }
}
