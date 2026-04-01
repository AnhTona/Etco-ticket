package com.esco.etco.service.impl;

import com.esco.etco.entity.Transaction;
import com.esco.etco.entity.UserTicket;
import com.esco.etco.entity.request.ReqTransactionDTO;
import com.esco.etco.entity.response.ResTransactionDTO;
import com.esco.etco.repository.TransactionRepository;
import com.esco.etco.repository.UserTicketRepository;
import com.esco.etco.service.TransactionService;
import com.esco.etco.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserTicketRepository userTicketRepository;

    private ResTransactionDTO toDto(Transaction transaction) {
        ResTransactionDTO dto = new ResTransactionDTO();
        dto.setId(transaction.getId());
        if (transaction.getUserTicket() != null) {
            dto.setUserTicketId(transaction.getUserTicket().getId());
        }
        dto.setAmount(transaction.getAmount());
        dto.setPaymentMethod(transaction.getPaymentMethod());
        dto.setStatus(transaction.getStatus());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setUpdatedAt(transaction.getUpdatedAt());
        dto.setCreatedBy(transaction.getCreatedBy());
        dto.setUpdatedBy(transaction.getUpdatedBy());
        return dto;
    }

    @Override
    public ResTransactionDTO create(ReqTransactionDTO reqTransactionDTO) throws IdInvalidException {
        UserTicket userTicket = userTicketRepository.findById(reqTransactionDTO.getUserTicketId())
                .orElseThrow(() -> new IdInvalidException("UserTicket không tồn tại với id: " + reqTransactionDTO.getUserTicketId()));

        Transaction transaction = new Transaction();
        transaction.setUserTicket(userTicket);
        transaction.setAmount(reqTransactionDTO.getAmount());
        transaction.setPaymentMethod(reqTransactionDTO.getPaymentMethod());
        transaction.setStatus(reqTransactionDTO.getStatus());
        return toDto(transactionRepository.save(transaction));
    }

    @Override
    public ResTransactionDTO update(long id, ReqTransactionDTO reqTransactionDTO) throws IdInvalidException {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Transaction không tồn tại với id: " + id));
        UserTicket userTicket = userTicketRepository.findById(reqTransactionDTO.getUserTicketId())
                .orElseThrow(() -> new IdInvalidException("UserTicket không tồn tại với id: " + reqTransactionDTO.getUserTicketId()));

        transaction.setUserTicket(userTicket);
        transaction.setAmount(reqTransactionDTO.getAmount());
        transaction.setPaymentMethod(reqTransactionDTO.getPaymentMethod());
        transaction.setStatus(reqTransactionDTO.getStatus());
        return toDto(transactionRepository.save(transaction));
    }

    @Override
    public ResTransactionDTO getById(long id) throws IdInvalidException {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Transaction không tồn tại với id: " + id));
        return toDto(transaction);
    }

    @Override
    public List<ResTransactionDTO> getAll() {
        return transactionRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }
}
