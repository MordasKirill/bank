package org.international.bank.service;

import org.international.bank.exception.CommandException;
import org.international.bank.exception.ValidationException;


public interface BankService {
    void deleteBank(String[] arguments) throws ValidationException, CommandException;
    void createBank(String[] arguments) throws ValidationException, CommandException;
    void updateBank(String[] arguments) throws CommandException, ValidationException;
    void getBank(String[] arguments) throws ValidationException, CommandException;
    void getBanks() throws CommandException;
}
