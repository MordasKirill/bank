package org.international.bank.repository;

import org.international.bank.entity.Transaction;
import org.international.bank.exception.CommandException;

import java.util.List;

public interface TransactionRepository {

    List<Transaction> getClientTransactions(int clientId) throws CommandException;
    boolean executeTransaction(int senderId, int receiverId, double amount) throws CommandException;

}
