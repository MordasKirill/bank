package org.international.bank.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.international.bank.entity.Bank;
import org.international.bank.exception.CommandException;
import org.international.bank.exception.ValidationException;
import org.international.bank.repository.BankRepository;
import org.international.bank.repository.impl.BankRepositoryImpl;
import org.international.bank.service.BankService;
import org.international.bank.validation.Validation;

import java.util.List;
import java.util.Optional;

@Slf4j
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;

    public BankServiceImpl() {
        this.bankRepository = new BankRepositoryImpl();
    }

    @Override
    public void deleteBank(String[] arguments) throws ValidationException, CommandException {
        if (Validation.getInstance().validateProvidedArguments(1, arguments)) {
            int bankId = Validation.getInstance().validateIntegerType(arguments[1], 1);
            bankRepository.deleteBank(bankId);
            log.info("Bank with id:{} deleted.", bankId);
        }

    }

    @Override
    public void createBank(String[] arguments) throws ValidationException, CommandException {
        if (Validation.getInstance().validateProvidedArguments(4, arguments)) {
            double legalFee = Validation.getInstance().validateDoubleType(arguments[2], 2);
            double individualFee = Validation.getInstance().validateDoubleType(arguments[3], 3);
            double externalFee = Validation.getInstance().validateDoubleType(arguments[4], 4);
            Bank bank = new Bank();
            bank.setName(arguments[1]);
            bank.setLegalFee(legalFee);
            bank.setIndividualFee(individualFee);
            bank.setExternalFee(externalFee);
            int bankId = bankRepository.createBank(bank);
            log.info("Bank with id:{} created.", bankId);
        }
    }

    @Override
    public void updateBank(String[] arguments) throws CommandException, ValidationException {
        if (Validation.getInstance().validateProvidedArguments(5, arguments)) {
            int bankId = Validation.getInstance().validateIntegerType(arguments[1], 2);
            double legalFee = Validation.getInstance().validateDoubleType(arguments[3], 2);
            double individualFee = Validation.getInstance().validateDoubleType(arguments[4], 3);
            double externalFee = Validation.getInstance().validateDoubleType(arguments[5], 4);
            Bank bank = new Bank();
            bank.setId(bankId);
            bank.setName(arguments[2]);
            bank.setLegalFee(legalFee);
            bank.setIndividualFee(individualFee);
            bank.setExternalFee(externalFee);
            bankRepository.updateBank(bank);
            log.info("Bank with id:{} updated.", bank.getId());
        }
    }

    @Override
    public void getBank(String[] arguments) throws ValidationException, CommandException {
        if (Validation.getInstance().validateProvidedArguments(1, arguments)) {
            int bankId = Validation.getInstance().validateIntegerType(arguments[1], 1);
            Optional<Bank> bank = bankRepository.getBank(bankId);
            if (bank.isPresent()) {
                log.info(bank.get().toString());
            } else {
                log.error("Bank with id:{} not found.", bankId);
            }
        }
    }

    @Override
    public void getBanks() throws CommandException {
        List<Bank> banks = bankRepository.getBanks();
        for (Bank bank : banks) {
            log.info(bank.toString());
        }
    }
}
