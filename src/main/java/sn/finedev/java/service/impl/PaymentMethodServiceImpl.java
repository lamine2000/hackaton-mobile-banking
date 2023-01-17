package sn.finedev.java.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.finedev.java.domain.PaymentMethod;
import sn.finedev.java.repository.PaymentMethodRepository;
import sn.finedev.java.repository.search.PaymentMethodSearchRepository;
import sn.finedev.java.service.PaymentMethodService;
import sn.finedev.java.service.dto.PaymentMethodDTO;
import sn.finedev.java.service.mapper.PaymentMethodMapper;

/**
 * Service Implementation for managing {@link PaymentMethod}.
 */
@Service
@Transactional
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final Logger log = LoggerFactory.getLogger(PaymentMethodServiceImpl.class);

    private final PaymentMethodRepository paymentMethodRepository;

    private final PaymentMethodMapper paymentMethodMapper;

    private final PaymentMethodSearchRepository paymentMethodSearchRepository;

    public PaymentMethodServiceImpl(
        PaymentMethodRepository paymentMethodRepository,
        PaymentMethodMapper paymentMethodMapper,
        PaymentMethodSearchRepository paymentMethodSearchRepository
    ) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentMethodMapper = paymentMethodMapper;
        this.paymentMethodSearchRepository = paymentMethodSearchRepository;
    }

    @Override
    public PaymentMethodDTO save(PaymentMethodDTO paymentMethodDTO) {
        log.debug("Request to save PaymentMethod : {}", paymentMethodDTO);
        PaymentMethod paymentMethod = paymentMethodMapper.toEntity(paymentMethodDTO);
        paymentMethod = paymentMethodRepository.save(paymentMethod);
        PaymentMethodDTO result = paymentMethodMapper.toDto(paymentMethod);
        paymentMethodSearchRepository.index(paymentMethod);
        return result;
    }

    @Override
    public PaymentMethodDTO update(PaymentMethodDTO paymentMethodDTO) {
        log.debug("Request to update PaymentMethod : {}", paymentMethodDTO);
        PaymentMethod paymentMethod = paymentMethodMapper.toEntity(paymentMethodDTO);
        paymentMethod = paymentMethodRepository.save(paymentMethod);
        PaymentMethodDTO result = paymentMethodMapper.toDto(paymentMethod);
        paymentMethodSearchRepository.index(paymentMethod);
        return result;
    }

    @Override
    public Optional<PaymentMethodDTO> partialUpdate(PaymentMethodDTO paymentMethodDTO) {
        log.debug("Request to partially update PaymentMethod : {}", paymentMethodDTO);

        return paymentMethodRepository
            .findById(paymentMethodDTO.getId())
            .map(existingPaymentMethod -> {
                paymentMethodMapper.partialUpdate(existingPaymentMethod, paymentMethodDTO);

                return existingPaymentMethod;
            })
            .map(paymentMethodRepository::save)
            .map(savedPaymentMethod -> {
                paymentMethodSearchRepository.save(savedPaymentMethod);

                return savedPaymentMethod;
            })
            .map(paymentMethodMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentMethodDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PaymentMethods");
        return paymentMethodRepository.findAll(pageable).map(paymentMethodMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentMethodDTO> findOne(Long id) {
        log.debug("Request to get PaymentMethod : {}", id);
        return paymentMethodRepository.findById(id).map(paymentMethodMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PaymentMethod : {}", id);
        paymentMethodRepository.deleteById(id);
        paymentMethodSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentMethodDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PaymentMethods for query {}", query);
        return paymentMethodSearchRepository.search(query, pageable).map(paymentMethodMapper::toDto);
    }
}
