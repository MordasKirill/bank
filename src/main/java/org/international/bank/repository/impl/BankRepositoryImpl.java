package org.international.bank.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.international.bank.entity.Bank;
import org.international.bank.exception.CommandException;
import org.international.bank.repository.BankRepository;
import org.international.bank.repository.db.ConnectionPool;
import org.international.bank.repository.db.impl.ConnectionPoolImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
public class BankRepositoryImpl implements BankRepository {

    private static final String DELETE_BANK = "delete from bank where bank_id = ?";
    private static final String CREATE_BANK = "insert into bank (bank_name, bank_legal_fee, bank_individual_fee, bank_external_transfer_fee) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_BANK = "update bank set bank_name = ?, bank_legal_fee = ?, bank_individual_fee = ?, bank_external_transfer_fee = ? where bank_id = ?";
    private static final String GET_BANK = "SELECT * FROM bank where bank_id = ?";
    private static final String GET_BANK_LIST = "SELECT * FROM bank";
    private final ConnectionPool connectionPool;
    private Connection connection;

    public BankRepositoryImpl() {
        this.connectionPool = ConnectionPoolImpl.getInstance();
    }

    @Override
    public void deleteBank(int bankId) throws CommandException {
        connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE_BANK)) {
            statement.setInt(1, bankId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public int createBank(Bank bank) throws CommandException {
        int bankId;
        connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(CREATE_BANK,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, bank.getName());
            statement.setDouble(2, bank.getLegalFee());
            statement.setDouble(3, bank.getIndividualFee());
            statement.setDouble(4, bank.getExternalFee());
            statement.executeUpdate();
            bankId = getGeneratedId(statement.getGeneratedKeys());
        } catch (SQLException e) {
            log.error("Create bank failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return bankId;
    }

    @Override
    public Bank updateBank(Bank bank) throws CommandException {
        connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_BANK)) {
            statement.setString(1, bank.getName());
            statement.setDouble(2, bank.getLegalFee());
            statement.setDouble(3, bank.getIndividualFee());
            statement.setDouble(4, bank.getExternalFee());
            statement.setInt(5, bank.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Update bank failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return bank;
    }

    @Override
    public Optional<Bank> getBank(int bankId) throws CommandException {
        connection = connectionPool.getConnection();
        Optional<Bank> optionalBank = Optional.empty();
        try (PreparedStatement statement = connection.prepareStatement(GET_BANK)) {
            statement.setInt(1, bankId);
            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    optionalBank = Optional.of(getBank(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error("Get bank failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return optionalBank;
    }

    @Override
    public List<Bank> getBanks() throws CommandException {
        connection = connectionPool.getConnection();
        List<Bank> banks = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(GET_BANK_LIST);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                banks.add(getBank(resultSet));
            }
        } catch (SQLException e) {
            log.error("Get bank list failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return banks;
    }

    private Bank getBank(ResultSet resultSet) throws SQLException {
        Bank bank = new Bank();
        bank.setId(resultSet.getInt("bank_id"));
        bank.setName(resultSet.getString("bank_name"));
        bank.setLegalFee(resultSet.getDouble("bank_legal_fee"));
        bank.setIndividualFee(resultSet.getDouble("bank_individual_fee"));
        bank.setExternalFee(resultSet.getDouble("bank_external_transfer_fee"));
        return bank;
    }

    private int getGeneratedId(ResultSet resultSet) throws SQLException {
        resultSet.next();
        return resultSet.getInt(1);
    }

    public void validateBankExist(int bankId) throws CommandException {
        Optional<Bank> bankOptional = getBank(bankId);
        if (bankOptional.isEmpty()) {
            throw new CommandException("Bank should exist.");
        }
    }
}
