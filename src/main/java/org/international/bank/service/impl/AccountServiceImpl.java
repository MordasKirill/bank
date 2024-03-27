package org.international.bank.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.international.bank.entity.Account;
import org.international.bank.entity.Currency;
import org.international.bank.exception.CommandException;
import org.international.bank.exception.ValidationException;
import org.international.bank.repository.AccountRepository;
import org.international.bank.repository.impl.AccountRepositoryImpl;
import org.international.bank.service.AccountService;
import org.international.bank.validation.Validation;

import java.util.List;
import java.util.Optional;

@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl() {
        this.accountRepository = new AccountRepositoryImpl();
    }

    @Override
    public void deleteAccount(String[] arguments) throws ValidationException, CommandException {
        if (Validation.getInstance().validateProvidedArguments(1, arguments)) {
            int accountId = Validation.getInstance().validateIntegerType(arguments[1], 1);
            accountRepository.deleteAccount(accountId);
            log.info("Account with id:{} deleted.", Integer.parseInt(arguments[1]));
        }
    }

    @Override
    public void createAccount(String[] arguments) throws ValidationException, CommandException {
        if (Validation.getInstance().validateProvidedArguments(3, arguments)) {
            int bankId = Validation.getInstance().validateIntegerType(arguments[1], 1);
            int clientId = Validation.getInstance().validateIntegerType(arguments[2], 2);
            Currency currency = Validation.getInstance().validateCurrency(arguments[3]);
            Account account = new Account();
            account.setBankId(bankId);
            account.setClientId(clientId);
            account.setAccountCurrency(currency);
            account.setAccountBalance(0);
            int accountId = accountRepository.createAccount(account);
            log.info("Account with id:{} created.", accountId);
        }
    }

    @Override
    public void updateAccount(String[] arguments) throws CommandException, ValidationException {
        if (Validation.getInstance().validateProvidedArguments(2, arguments)) {
            int accountId = Validation.getInstance().validateIntegerType(arguments[1], 1);
            double accountBalance = Validation.getInstance().validateIntegerType(arguments[2], 2);
            Account account = new Account();
            account.setId(accountId);
            account.setAccountBalance(accountBalance);
            accountRepository.updateAccount(account);
            log.info("Account with id:{} updated.", account.getId());
        }
    }

    @Override
    public void getClientAccount(String[] arguments) throws ValidationException, CommandException {
        if (Validation.getInstance().validateProvidedArguments(1, arguments)) {
            int accountId = Validation.getInstance().validateIntegerType(arguments[1], 1);
            Optional<Account> account = accountRepository.getAccount(accountId);
            if (account.isPresent()) {
                log.info(account.get().toString());
            } else {
                log.error("Account with id:{} not found.", Integer.parseInt(arguments[1]));
            }
        }
    }

    @Override
    public void getClientAccounts(String[] arguments) throws CommandException, ValidationException {
        if (Validation.getInstance().validateProvidedArguments(1, arguments)) {
            int clientId = Validation.getInstance().validateIntegerType(arguments[1], 1);
            List<Account> accounts = accountRepository.getClientAccounts(clientId);
            for (Account account : accounts) {
                log.info(account.toString());
            }
        }
    }
}
