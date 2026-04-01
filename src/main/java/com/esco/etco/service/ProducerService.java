package com.esco.etco.service;

import com.esco.etco.entity.request.ReqProducerDTO;
import com.esco.etco.entity.response.ResProducerDTO;
import com.esco.etco.util.error.IdInvalidException;

import java.util.List;

public interface ProducerService {
    ResProducerDTO create(ReqProducerDTO reqProducerDTO);
    ResProducerDTO update(long id, ReqProducerDTO reqProducerDTO) throws IdInvalidException;
    void delete(long id) throws IdInvalidException;
    ResProducerDTO getById(long id) throws IdInvalidException;
    List<ResProducerDTO> getAll();
}
