package org.international.bank.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.international.bank.entity.Transaction;
import org.international.bank.exception.CommandException;
import org.international.bank.exception.ValidationException;
import org.international.bank.repository.TransactionRepository;
import org.international.bank.repository.impl.TransactionRepositoryImpl;
import org.international.bank.service.TransferService;
import org.international.bank.validation.Validation;

@Slf4j
public class TransferServiceImpl implements TransferService {

    private final TransactionRepository transactionRepository;
    public TransferServiceImpl() {
        this.transactionRepository = new TransactionRepositoryImpl();
    }

    @Override
    public void getClientTransactions(String[] arguments) throws CommandException, ValidationException {
        int clientId = 0;
        if (Validation.getInstance().validateProvidedArguments(1, arguments)) {
            clientId = Validation.getInstance().validateIntegerType(arguments[1], 1);
        }
        for (Transaction transaction : transactionRepository.getClientTransactions(clientId)) {
            log.info(transaction.toString());
        }
    }

    @Override
    public void executeTransaction(String[] arguments) throws CommandException, ValidationException {
        if (Validation.getInstance().validateProvidedArguments(3, arguments)) {
            int senderId = Validation.getInstance().validateIntegerType(arguments[1], 1);
            int receiverId = Validation.getInstance().validateIntegerType(arguments[2], 2);
            double amount = Validation.getInstance().validateDoubleType(arguments[3], 3);
            transactionRepository.executeTransaction(senderId, receiverId, amount);
            log.info("Transaction executed.");
        }
    }
}
