package com.esco.etco.service.impl;

import com.esco.etco.entity.Producer;
import com.esco.etco.entity.request.ReqProducerDTO;
import com.esco.etco.entity.response.ResProducerDTO;
import com.esco.etco.repository.ProducerRepository;
import com.esco.etco.service.ProducerService;
import com.esco.etco.util.error.IdInvalidException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProducerServiceImpl implements ProducerService {
    private final ProducerRepository producerRepository;

    private ResProducerDTO toDto(Producer producer) {
        ResProducerDTO dto = new ResProducerDTO();
        dto.setId(producer.getId());
        dto.setProducerName(producer.getProducerName());
        dto.setBankName(producer.getBankName());
        dto.setBankAccountNumber(producer.getBankAccountNumber());
        dto.setContactEmail(producer.getContactEmail());
        dto.setCreatedAt(producer.getCreatedAt());
        dto.setUpdatedAt(producer.getUpdatedAt());
        dto.setCreatedBy(producer.getCreatedBy());
        dto.setUpdatedBy(producer.getUpdatedBy());
        return dto;
    }

    @Override
    public ResProducerDTO create(ReqProducerDTO reqProducerDTO) {
        Producer producer = new Producer();
        producer.setProducerName(reqProducerDTO.getProducerName());
        producer.setBankName(reqProducerDTO.getBankName());
        producer.setBankAccountNumber(reqProducerDTO.getBankAccountNumber());
        producer.setContactEmail(reqProducerDTO.getContactEmail());
        return toDto(producerRepository.save(producer));
    }

    @Override
    public ResProducerDTO update(long id, ReqProducerDTO reqProducerDTO) throws IdInvalidException {
        Producer producer = producerRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Producer không tồn tại với id: " + id));
        producer.setProducerName(reqProducerDTO.getProducerName());
        producer.setBankName(reqProducerDTO.getBankName());
        producer.setBankAccountNumber(reqProducerDTO.getBankAccountNumber());
        producer.setContactEmail(reqProducerDTO.getContactEmail());
        return toDto(producerRepository.save(producer));
    }

    @Override
    public void delete(long id) throws IdInvalidException {
        Producer producer = producerRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Producer không tồn tại với id: " + id));
        producerRepository.delete(producer);
    }

    @Override
    public ResProducerDTO getById(long id) throws IdInvalidException {
        Producer producer = producerRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Producer không tồn tại với id: " + id));
        return toDto(producer);
    }

    @Override
    public List<ResProducerDTO> getAll() {
        return producerRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }
}
