package org.international.bank.repository;

import org.international.bank.entity.Bank;
import org.international.bank.exception.CommandException;

import java.util.List;
import java.util.Optional;

public interface BankRepository {

    void deleteBank(int bankId) throws CommandException;
    int createBank(Bank bank) throws CommandException;
    Bank updateBank(Bank bank) throws CommandException;
    Optional<Bank> getBank(int bankId) throws CommandException;
    List<Bank> getBanks() throws CommandException;
}
