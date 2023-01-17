package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.InsuranceAndMicroCreditsContribution;
import sn.finedev.java.repository.InsuranceAndMicroCreditsContributionRepository;
import sn.finedev.java.repository.search.InsuranceAndMicroCreditsContributionSearchRepository;
import sn.finedev.java.service.InsuranceAndMicroCreditsContributionService;
import sn.finedev.java.service.dto.InsuranceAndMicroCreditsContributionDTO;
import sn.finedev.java.service.mapper.InsuranceAndMicroCreditsContributionMapper;

/**
 * Service Implementation for managing {@link InsuranceAndMicroCreditsContribution}.
 */
@Service
@Transactional
public class InsuranceAndMicroCreditsContributionServiceImpl implements InsuranceAndMicroCreditsContributionService {

    private final Logger log = LoggerFactory.getLogger(InsuranceAndMicroCreditsContributionServiceImpl.class);

    private final InsuranceAndMicroCreditsContributionRepository insuranceAndMicroCreditsContributionRepository;

    private final InsuranceAndMicroCreditsContributionMapper insuranceAndMicroCreditsContributionMapper;

    private final InsuranceAndMicroCreditsContributionSearchRepository insuranceAndMicroCreditsContributionSearchRepository;

    public InsuranceAndMicroCreditsContributionServiceImpl(
        InsuranceAndMicroCreditsContributionRepository insuranceAndMicroCreditsContributionRepository,
        InsuranceAndMicroCreditsContributionMapper insuranceAndMicroCreditsContributionMapper,
        InsuranceAndMicroCreditsContributionSearchRepository insuranceAndMicroCreditsContributionSearchRepository
    ) {
        this.insuranceAndMicroCreditsContributionRepository = insuranceAndMicroCreditsContributionRepository;
        this.insuranceAndMicroCreditsContributionMapper = insuranceAndMicroCreditsContributionMapper;
        this.insuranceAndMicroCreditsContributionSearchRepository = insuranceAndMicroCreditsContributionSearchRepository;
    }

    @Override
    public InsuranceAndMicroCreditsContributionDTO save(InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO) {
        log.debug("Request to save InsuranceAndMicroCreditsContribution : {}", insuranceAndMicroCreditsContributionDTO);
        InsuranceAndMicroCreditsContribution insuranceAndMicroCreditsContribution = insuranceAndMicroCreditsContributionMapper.toEntity(
            insuranceAndMicroCreditsContributionDTO
        );
        insuranceAndMicroCreditsContribution = insuranceAndMicroCreditsContributionRepository.save(insuranceAndMicroCreditsContribution);
        InsuranceAndMicroCreditsContributionDTO result = insuranceAndMicroCreditsContributionMapper.toDto(
            insuranceAndMicroCreditsContribution
        );
        insuranceAndMicroCreditsContributionSearchRepository.index(insuranceAndMicroCreditsContribution);
        return result;
    }

    @Override
    public InsuranceAndMicroCreditsContributionDTO update(InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO) {
        log.debug("Request to update InsuranceAndMicroCreditsContribution : {}", insuranceAndMicroCreditsContributionDTO);
        InsuranceAndMicroCreditsContribution insuranceAndMicroCreditsContribution = insuranceAndMicroCreditsContributionMapper.toEntity(
            insuranceAndMicroCreditsContributionDTO
        );
        insuranceAndMicroCreditsContribution = insuranceAndMicroCreditsContributionRepository.save(insuranceAndMicroCreditsContribution);
        InsuranceAndMicroCreditsContributionDTO result = insuranceAndMicroCreditsContributionMapper.toDto(
            insuranceAndMicroCreditsContribution
        );
        insuranceAndMicroCreditsContributionSearchRepository.index(insuranceAndMicroCreditsContribution);
        return result;
    }

    @Override
    public Optional<InsuranceAndMicroCreditsContributionDTO> partialUpdate(
        InsuranceAndMicroCreditsContributionDTO insuranceAndMicroCreditsContributionDTO
    ) {
        log.debug("Request to partially update InsuranceAndMicroCreditsContribution : {}", insuranceAndMicroCreditsContributionDTO);

        return insuranceAndMicroCreditsContributionRepository
            .findById(insuranceAndMicroCreditsContributionDTO.getId())
            .map(existingInsuranceAndMicroCreditsContribution -> {
                insuranceAndMicroCreditsContributionMapper.partialUpdate(
                    existingInsuranceAndMicroCreditsContribution,
                    insuranceAndMicroCreditsContributionDTO
                );

                return existingInsuranceAndMicroCreditsContribution;
            })
            .map(insuranceAndMicroCreditsContributionRepository::save)
            .map(savedInsuranceAndMicroCreditsContribution -> {
                insuranceAndMicroCreditsContributionSearchRepository.save(savedInsuranceAndMicroCreditsContribution);

                return savedInsuranceAndMicroCreditsContribution;
            })
            .map(insuranceAndMicroCreditsContributionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InsuranceAndMicroCreditsContributionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all InsuranceAndMicroCreditsContributions");
        return insuranceAndMicroCreditsContributionRepository.findAll(pageable).map(insuranceAndMicroCreditsContributionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InsuranceAndMicroCreditsContributionDTO> findOne(Long id) {
        log.debug("Request to get InsuranceAndMicroCreditsContribution : {}", id);
        return insuranceAndMicroCreditsContributionRepository.findById(id).map(insuranceAndMicroCreditsContributionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete InsuranceAndMicroCreditsContribution : {}", id);
        insuranceAndMicroCreditsContributionRepository.deleteById(id);
        insuranceAndMicroCreditsContributionSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InsuranceAndMicroCreditsContributionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of InsuranceAndMicroCreditsContributions for query {}", query);
        return insuranceAndMicroCreditsContributionSearchRepository
            .search(query, pageable)
            .map(insuranceAndMicroCreditsContributionMapper::toDto);
    }
}
