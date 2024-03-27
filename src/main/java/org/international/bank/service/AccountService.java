package org.international.bank.service;

import org.international.bank.exception.CommandException;
import org.international.bank.exception.ValidationException;

public interface AccountService {

    void deleteAccount(String[] arguments) throws ValidationException, CommandException;
    void createAccount(String[] arguments) throws ValidationException, CommandException;
    void updateAccount(String[] arguments) throws CommandException, ValidationException;
    void getClientAccount(String[] arguments) throws ValidationException, CommandException;
    void getClientAccounts(String[] arguments) throws CommandException, ValidationException;
}
