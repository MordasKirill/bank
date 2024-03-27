package org.international.bank.repository;

import org.international.bank.entity.Account;
import org.international.bank.exception.CommandException;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    void deleteAccount(int accountId) throws CommandException;
    int createAccount(Account account) throws CommandException;
    Account updateAccount(Account account) throws CommandException;
    Optional<Account> getAccount(int accountId) throws CommandException;
    List<Account> getClientAccounts(int clientId) throws CommandException;
}
