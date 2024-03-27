package org.international.bank.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.international.bank.entity.Account;
import org.international.bank.entity.Currency;
import org.international.bank.exception.CommandException;
import org.international.bank.repository.AccountRepository;
import org.international.bank.repository.db.ConnectionPool;
import org.international.bank.repository.db.impl.ConnectionPoolImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class AccountRepositoryImpl implements AccountRepository {

    private static final String DELETE_ACCOUNT = "delete from account where account_id = ?";
    private static final String CREATE_ACCOUNT = "insert into account (bank_bank_id, account_balance, account_currency, client_client_id) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_ACCOUNT = "update account set account_balance = ? where account_id = ?";
    private static final String GET_ACCOUNT = "SELECT * FROM account where account_id = ?";
    private static final String GET_ACCOUNT_LIST = "SELECT * FROM account where client_client_id = ?";

    private final ConnectionPool connectionPool;
    private final BankRepositoryImpl bankRepository;
    private Connection connection;

    public AccountRepositoryImpl() {
        this.connectionPool = ConnectionPoolImpl.getInstance();
        this.bankRepository = new BankRepositoryImpl();
    }

    @Override
    public void deleteAccount(int accountId) throws CommandException {
        connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE_ACCOUNT)) {
            statement.setInt(1, accountId);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Delete account failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public int createAccount(Account account) throws CommandException {
        int accountId;
        bankRepository.validateBankExist(account.getBankId());
        connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(CREATE_ACCOUNT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, account.getBankId());
            statement.setInt(2, 0);
            statement.setString(3, account.getAccountCurrency().name());
            statement.setInt(4, account.getClientId());
            statement.executeUpdate();
            accountId = getGeneratedId(statement.getGeneratedKeys());
        } catch (SQLException e) {
            log.error("Create account failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return accountId;
    }

    @Override
    public Account updateAccount(Account account) throws CommandException {
        connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_ACCOUNT)) {
            statement.setDouble(1, account.getAccountBalance());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Update account failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return account;
    }

    @Override
    public Optional<Account> getAccount(int accountId) throws CommandException {
        connection = connectionPool.getConnection();
        Optional<Account> optionalBank = Optional.empty();
        try (PreparedStatement statement = connection.prepareStatement(GET_ACCOUNT)) {
            statement.setInt(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    optionalBank = Optional.of(getAccount(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error("Get account failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return optionalBank;
    }

    @Override
    public List<Account> getClientAccounts(int clientId) throws CommandException {
        connection = connectionPool.getConnection();
        List<Account> accounts = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(GET_ACCOUNT_LIST)) {
            statement.setInt(1, clientId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    accounts.add(getAccount(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error("Get account list failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return accounts;
    }

    private Account getAccount(ResultSet resultSet) throws SQLException {
        Account account = new Account();
        account.setId(resultSet.getInt("account_id"));
        account.setBankId(resultSet.getInt("bank_bank_id"));
        account.setAccountBalance(resultSet.getDouble("account_balance"));
        account.setAccountCurrency(Currency.valueOf(resultSet.getString("account_currency")));
        account.setClientId(resultSet.getInt("client_client_id"));
        return account;
    }

    private int getGeneratedId(ResultSet resultSet) throws SQLException {
        resultSet.next();
        return resultSet.getInt(1);
    }
}
