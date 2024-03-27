package org.international.bank.service;

import org.international.bank.exception.CommandException;
import org.international.bank.exception.ValidationException;

public interface TransferService {

    void getClientTransactions(String[] arguments) throws CommandException, ValidationException;
    void executeTransaction(String[] arguments) throws CommandException, ValidationException;
}
