package com.esco.etco.service;

import com.esco.etco.entity.request.ReqTransactionDTO;
import com.esco.etco.entity.response.ResTransactionDTO;
import com.esco.etco.util.error.IdInvalidException;

import java.util.List;

public interface TransactionService {
    ResTransactionDTO create(ReqTransactionDTO reqTransactionDTO) throws IdInvalidException;
    ResTransactionDTO update(long id, ReqTransactionDTO reqTransactionDTO) throws IdInvalidException;
    ResTransactionDTO getById(long id) throws IdInvalidException;
    List<ResTransactionDTO> getAll();
}
