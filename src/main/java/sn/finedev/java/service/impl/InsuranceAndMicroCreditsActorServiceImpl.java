package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.InsuranceAndMicroCreditsActor;
import sn.finedev.java.repository.InsuranceAndMicroCreditsActorRepository;
import sn.finedev.java.repository.search.InsuranceAndMicroCreditsActorSearchRepository;
import sn.finedev.java.service.InsuranceAndMicroCreditsActorService;
import sn.finedev.java.service.dto.InsuranceAndMicroCreditsActorDTO;
import sn.finedev.java.service.mapper.InsuranceAndMicroCreditsActorMapper;

/**
 * Service Implementation for managing {@link InsuranceAndMicroCreditsActor}.
 */
@Service
@Transactional
public class InsuranceAndMicroCreditsActorServiceImpl implements InsuranceAndMicroCreditsActorService {

    private final Logger log = LoggerFactory.getLogger(InsuranceAndMicroCreditsActorServiceImpl.class);

    private final InsuranceAndMicroCreditsActorRepository insuranceAndMicroCreditsActorRepository;

    private final InsuranceAndMicroCreditsActorMapper insuranceAndMicroCreditsActorMapper;

    private final InsuranceAndMicroCreditsActorSearchRepository insuranceAndMicroCreditsActorSearchRepository;

    public InsuranceAndMicroCreditsActorServiceImpl(
        InsuranceAndMicroCreditsActorRepository insuranceAndMicroCreditsActorRepository,
        InsuranceAndMicroCreditsActorMapper insuranceAndMicroCreditsActorMapper,
        InsuranceAndMicroCreditsActorSearchRepository insuranceAndMicroCreditsActorSearchRepository
    ) {
        this.insuranceAndMicroCreditsActorRepository = insuranceAndMicroCreditsActorRepository;
        this.insuranceAndMicroCreditsActorMapper = insuranceAndMicroCreditsActorMapper;
        this.insuranceAndMicroCreditsActorSearchRepository = insuranceAndMicroCreditsActorSearchRepository;
    }

    @Override
    public InsuranceAndMicroCreditsActorDTO save(InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO) {
        log.debug("Request to save InsuranceAndMicroCreditsActor : {}", insuranceAndMicroCreditsActorDTO);
        InsuranceAndMicroCreditsActor insuranceAndMicroCreditsActor = insuranceAndMicroCreditsActorMapper.toEntity(
            insuranceAndMicroCreditsActorDTO
        );
        insuranceAndMicroCreditsActor = insuranceAndMicroCreditsActorRepository.save(insuranceAndMicroCreditsActor);
        InsuranceAndMicroCreditsActorDTO result = insuranceAndMicroCreditsActorMapper.toDto(insuranceAndMicroCreditsActor);
        insuranceAndMicroCreditsActorSearchRepository.index(insuranceAndMicroCreditsActor);
        return result;
    }

    @Override
    public InsuranceAndMicroCreditsActorDTO update(InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO) {
        log.debug("Request to update InsuranceAndMicroCreditsActor : {}", insuranceAndMicroCreditsActorDTO);
        InsuranceAndMicroCreditsActor insuranceAndMicroCreditsActor = insuranceAndMicroCreditsActorMapper.toEntity(
            insuranceAndMicroCreditsActorDTO
        );
        insuranceAndMicroCreditsActor = insuranceAndMicroCreditsActorRepository.save(insuranceAndMicroCreditsActor);
        InsuranceAndMicroCreditsActorDTO result = insuranceAndMicroCreditsActorMapper.toDto(insuranceAndMicroCreditsActor);
        insuranceAndMicroCreditsActorSearchRepository.index(insuranceAndMicroCreditsActor);
        return result;
    }

    @Override
    public Optional<InsuranceAndMicroCreditsActorDTO> partialUpdate(InsuranceAndMicroCreditsActorDTO insuranceAndMicroCreditsActorDTO) {
        log.debug("Request to partially update InsuranceAndMicroCreditsActor : {}", insuranceAndMicroCreditsActorDTO);

        return insuranceAndMicroCreditsActorRepository
            .findById(insuranceAndMicroCreditsActorDTO.getId())
            .map(existingInsuranceAndMicroCreditsActor -> {
                insuranceAndMicroCreditsActorMapper.partialUpdate(existingInsuranceAndMicroCreditsActor, insuranceAndMicroCreditsActorDTO);

                return existingInsuranceAndMicroCreditsActor;
            })
            .map(insuranceAndMicroCreditsActorRepository::save)
            .map(savedInsuranceAndMicroCreditsActor -> {
                insuranceAndMicroCreditsActorSearchRepository.save(savedInsuranceAndMicroCreditsActor);

                return savedInsuranceAndMicroCreditsActor;
            })
            .map(insuranceAndMicroCreditsActorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InsuranceAndMicroCreditsActorDTO> findAll(Pageable pageable) {
        log.debug("Request to get all InsuranceAndMicroCreditsActors");
        return insuranceAndMicroCreditsActorRepository.findAll(pageable).map(insuranceAndMicroCreditsActorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InsuranceAndMicroCreditsActorDTO> findOne(Long id) {
        log.debug("Request to get InsuranceAndMicroCreditsActor : {}", id);
        return insuranceAndMicroCreditsActorRepository.findById(id).map(insuranceAndMicroCreditsActorMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete InsuranceAndMicroCreditsActor : {}", id);
        insuranceAndMicroCreditsActorRepository.deleteById(id);
        insuranceAndMicroCreditsActorSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InsuranceAndMicroCreditsActorDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of InsuranceAndMicroCreditsActors for query {}", query);
        return insuranceAndMicroCreditsActorSearchRepository.search(query, pageable).map(insuranceAndMicroCreditsActorMapper::toDto);
    }
}
