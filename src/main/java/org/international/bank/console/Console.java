package org.international.bank.console;

import lombok.extern.slf4j.Slf4j;
import org.international.bank.console.command.CommandWithArguments;
import org.international.bank.console.command.CommandWithoutArguments;
import org.international.bank.exception.CommandException;
import org.international.bank.exception.ValidationException;
import org.international.bank.repository.db.impl.ConnectionPoolImpl;
import org.international.bank.service.AccountService;
import org.international.bank.service.BankService;
import org.international.bank.service.ClientService;
import org.international.bank.service.TransferService;
import org.international.bank.service.impl.AccountServiceImpl;
import org.international.bank.service.impl.BankServiceImpl;
import org.international.bank.service.impl.ClientServiceImpl;
import org.international.bank.service.impl.TransferServiceImpl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Slf4j
public class Console {
    private final BankService bankManager;
    private final AccountService accountService;
    private final ClientService clientService;
    private final TransferService transferService;
    private final Map<String, CommandWithArguments> commandsWithArguments = new HashMap<>();
    private final Map<String, CommandWithoutArguments> commandsWithoutArguments = new HashMap<>();

    public Console() {
        ConnectionPoolImpl.create();
        this.bankManager = new BankServiceImpl();
        this.accountService = new AccountServiceImpl();
        this.clientService = new ClientServiceImpl();
        this.transferService = new TransferServiceImpl();
        initializeCommands();
    }

    private void initializeCommands() {
        commandsWithoutArguments.put("help", this::printCommands);
        commandsWithoutArguments.put("exit", this::exit);

        commandsWithoutArguments.put("listbanks", bankManager::getBanks);
        commandsWithArguments.put("getbank", bankManager::getBank);
        commandsWithArguments.put("deletebank", bankManager::deleteBank);
        commandsWithArguments.put("updatebank", bankManager::updateBank);
        commandsWithArguments.put("createbank", bankManager::createBank);

        commandsWithArguments.put("listaccounts", accountService::getClientAccounts);
        commandsWithArguments.put("getaccount", accountService::getClientAccount);
        commandsWithArguments.put("deleteaccount", accountService::deleteAccount);
        commandsWithArguments.put("updateaccount", accountService::updateAccount);
        commandsWithArguments.put("createaccount", accountService::createAccount);

        commandsWithoutArguments.put("listclients", clientService::getClients);
        commandsWithArguments.put("getclient", clientService::getClient);
        commandsWithArguments.put("deleteclient", clientService::deleteClient);
        commandsWithArguments.put("updateclient", clientService::updateClient);
        commandsWithArguments.put("createclient", clientService::createClient);

        commandsWithArguments.put("transfer", transferService::executeTransaction);
        commandsWithArguments.put("transactions", transferService::getClientTransactions);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        printCommands();

        while (true) {
            log.info("Enter command: ");
            String input = scanner.nextLine();
            String[] arguments = input.split(" ");
            String command = arguments[0].toLowerCase();
            executeCommand(command, arguments);
        }
    }

    private void executeCommand(String command, String[] arguments) {
        try {
            if (commandsWithoutArguments.containsKey(command)) {
                commandsWithoutArguments.get(command).execute();
            } else if (commandsWithArguments.containsKey(command)) {
                commandsWithArguments.get(command).execute(arguments);
            } else {
                log.error("Invalid command. Type 'help' for a list of commands.");
            }
        } catch (ValidationException | CommandException e) {
            log.error(e.getMessage());
        }
    }

    private void printCommands() {
        log.info("Available commands:");
        log.info("help - Display available commands");

        log.info("listBanks - List all banks");
        log.info("getBank <bank id> - Get single bank");
        log.info("deleteBank <bank id> - Delete single bank");
        log.info("updateBank <bank id> <bank name> <legal Fee> <individual Fee> <external Fee> - Update bank");
        log.info("createBank <bank name> <legal Fee> <individual Fee> <external Fee> - Create bank");

        log.info("listClients - List all clients");
        log.info("getClient <client id> - Get single client");
        log.info("updateClient <client id> <client name> <client type> - Update a new client");
        log.info("deleteClient <client id> - Delete a client");
        log.info("createClient <client name> <client type> <bank id> - Add a new client");

        log.info("listAccounts <client id> - List all accounts");
        log.info("getAccount <account id>- Get single account");
        log.info("createAccount <bank id> <client id> <account currency> - Add a new account");
        log.info("updateAccount <account id> <bank id> <account currency> <client id> - List all accounts");
        log.info("deleteAccount <account id> - Add a new account");

        log.info("transfer <sender account id> <receiver account id> <amount> - Perform a fund transfer");
        log.info("transactions <client id> - List transactions for a client");

        log.info("exit - Exit the application");
    }



    private void exit() {
        log.info("Exiting...");
        try {
            ConnectionPoolImpl.getInstance().shutdown();
        } catch (SQLException e) {
            log.error("Connection pool shutdown failed.");
        }
        System.exit(0);
    }
}