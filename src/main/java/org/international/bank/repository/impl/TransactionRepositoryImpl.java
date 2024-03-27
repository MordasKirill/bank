package org.international.bank.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.international.bank.entity.*;
import org.international.bank.exception.CommandException;
import org.international.bank.exception.ValidationException;
import org.international.bank.repository.AccountRepository;
import org.international.bank.repository.BankRepository;
import org.international.bank.repository.ClientRepository;
import org.international.bank.repository.db.ConnectionPool;
import org.international.bank.repository.db.impl.ConnectionPoolImpl;
import org.international.bank.repository.TransactionRepository;
import org.international.bank.validation.Validation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TransactionRepositoryImpl implements TransactionRepository {

    private static final String GET_TRANSACTIONS = "SELECT * FROM transaction where receiver_account_id = ? or sender_account_id = ?";
    private static final String CREATE_TRANSACTION = "insert into transaction (transactions_amount, receiver_account_id, sender_account_id) VALUES (?, ?, ?)";
    private static final String DEBIT = "UPDATE account SET account_balance = account.account_balance - ? WHERE account_id = ?";
    private static final String CREDIT = "UPDATE account SET account_balance = account.account_balance + ? WHERE account_id = ?";
    private final ConnectionPool connectionPool;
    private Connection connection;
    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;
    private final ClientRepository clientRepository;

    public TransactionRepositoryImpl() {
        this.connectionPool = ConnectionPoolImpl.getInstance();
        this.accountRepository = new AccountRepositoryImpl();
        this.bankRepository = new BankRepositoryImpl();
        this.clientRepository = new ClientRepositoryImpl();
    }

    @Override
    public List<Transaction> getClientTransactions(int clientId) throws CommandException {
        connection = connectionPool.getConnection();
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(GET_TRANSACTIONS)) {
            statement.setInt(1, clientId);
            statement.setInt(2, clientId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setAmount(resultSet.getDouble("transactions_amount"));
                    transaction.setTransactionDate(resultSet.getDate("transaction_date"));
                    transaction.setReceiverAccountId(resultSet.getInt("receiver_account_id"));
                    transaction.setSenderAccountId(resultSet.getInt("sender_account_id"));
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            log.error("Get client transactions failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return transactions;
    }

    @Override
    public boolean executeTransaction(int senderId, int receiverId, double amount) throws CommandException {
        connection = connectionPool.getConnection();
        Optional<Account> senderAccountOptional = accountRepository.getAccount(senderId);
        Optional<Account> receiverAccountOptional = accountRepository.getAccount(receiverId);
        if (senderAccountOptional.isEmpty() || receiverAccountOptional.isEmpty()) {
            throw new CommandException("Sender and receiver accounts should exist.");
        }
        Account senderAccount = senderAccountOptional.get();
        Account receiverAccount = receiverAccountOptional.get();
        try {
            connection.setAutoCommit(false);
            if (isTransactionInternal(senderAccount, receiverAccount)) {
                executeInternalTransaction(senderAccount, receiverAccount, amount);
            } else {
                executeExternalTransaction(senderAccount, receiverAccount, amount);
            }
            connection.commit();
        } catch (SQLException | ValidationException e) {
            throw new CommandException(String.format("Transaction failed. Reason: %s", e.getMessage()));
        } finally {
            try {
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                log.error("Failed to set autocommit to false.");
            }
            connectionPool.releaseConnection(connection);
        }
        return false;
    }

    private boolean isTransactionInternal(Account sender, Account receiver) {
        return sender.getBankId() == receiver.getBankId();
    }

    private void executeInternalTransaction(Account sender, Account receiver, double amount) throws SQLException, ValidationException, CommandException {
        Bank bank = getAccountBank(sender);
        double fundsWithFee = amount + getBankFeeDependingOnClientType(sender, bank);
        Validation.getInstance().checkAccountBalance(sender, fundsWithFee);
        takeMoneyFromSender(sender, fundsWithFee);
        addMoneyToReceiver(receiver, amount);
        createTransaction(sender.getId(), receiver.getId(), amount);
    }

    private void executeExternalTransaction(Account sender, Account receiver, double amount) throws CommandException, ValidationException, SQLException {
        Bank bank = getAccountBank(sender);
        double fundsWithFee = addExternalTransferFeeToTransaction(amount, bank.getExternalFee(), bank, sender);
        Validation.getInstance().checkAccountBalance(sender, fundsWithFee);
        takeMoneyFromSender(sender, fundsWithFee);
        addMoneyToReceiver(receiver, amount);
        createTransaction(sender.getId(), receiver.getId(), amount);
    }

    private void createTransaction(int senderId, int receiverId, double amount) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_TRANSACTION)) {
            statement.setDouble(1, amount);
            statement.setInt(2, senderId);
            statement.setInt(3, receiverId);
            statement.executeUpdate();
        }
    }

    private void takeMoneyFromSender(Account sender, double amount) throws SQLException {
        try (PreparedStatement debitStatement = connection.prepareStatement(DEBIT)) {
            debitStatement.setDouble(1, amount);
            debitStatement.setInt(2, sender.getId());
            debitStatement.executeUpdate();
        }
    }

    private void addMoneyToReceiver(Account receiver, double amount) throws SQLException {
        try (PreparedStatement creditStatement = connection.prepareStatement(CREDIT)) {
            creditStatement.setDouble(1, amount);
            creditStatement.setInt(2, receiver.getId());
            creditStatement.executeUpdate();
        }
    }

    private Bank getAccountBank(Account account) throws CommandException, ValidationException {
        Optional<Bank> optionalBank = bankRepository.getBank(account.getBankId());
        if (optionalBank.isEmpty()) {
            throw new ValidationException("Bank should exist.");
        }
        return optionalBank.get();
    }

    private double addExternalTransferFeeToTransaction(double amount, double externalTransferFee, Bank bank, Account account) throws ValidationException, CommandException {
        return amount + externalTransferFee + getBankFeeDependingOnClientType(account, bank);
    }

    private double getBankFeeDependingOnClientType(Account account, Bank bank) throws CommandException, ValidationException {
        Client client = getClient(account);
        return client.getType().equals(ClientTypes.INDIVIDUAL) ? bank.getIndividualFee() : bank.getLegalFee();
    }

    private Client getClient(Account account) throws CommandException, ValidationException {
        Optional<Client> client = clientRepository.getClient(account.getClientId());
        if (client.isEmpty()) {
            throw new ValidationException("Client should exist.");
        }
        return client.get();
    }
}
