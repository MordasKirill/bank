package org.international.bank.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.international.bank.entity.Account;
import org.international.bank.entity.Client;
import org.international.bank.entity.ClientTypes;
import org.international.bank.entity.Currency;
import org.international.bank.exception.CommandException;
import org.international.bank.repository.AccountRepository;
import org.international.bank.repository.ClientRepository;
import org.international.bank.repository.db.ConnectionPool;
import org.international.bank.repository.db.impl.ConnectionPoolImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ClientRepositoryImpl implements ClientRepository {

    private static final String CREATE_CLIENT = "delete from client where client_id = ?";
    private static final String DELETE_CLIENT = "insert into client (client_name, client_type) VALUES (?, ?)";
    private static final String UPDATE_CLIENT = "update client set client_name = ?, client_type = ? where client_id = ?";
    private static final String GET_CLIENT = "SELECT * FROM client where client_id = ?";
    private static final String GET_CLIENT_LIST = "SELECT * FROM client";

    private final ConnectionPool connectionPool;
    private Connection connection;
    private final AccountRepository accountRepository;
    private final BankRepositoryImpl bankRepository;

    public ClientRepositoryImpl() {
        this.connectionPool = ConnectionPoolImpl.getInstance();
        this.accountRepository = new AccountRepositoryImpl();
        this.bankRepository = new BankRepositoryImpl();
    }

    @Override
    public void deleteClient(int clientId) throws CommandException {
        connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(CREATE_CLIENT)) {
            statement.setInt(1, clientId);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Delete client failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public int createClient(Client client, int bankId) throws CommandException {
        int clientId;
        connection = connectionPool.getConnection();
        bankRepository.validateBankExist(bankId);
        try (PreparedStatement statement = connection.prepareStatement(DELETE_CLIENT,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, client.getName());
            statement.setString(2, client.getType().name().toLowerCase());
            statement.executeUpdate();
            clientId = getGeneratedId(statement.getGeneratedKeys());
        } catch (SQLException e) {
            log.error("Create client failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        accountRepository.createAccount(new Account(bankId, Currency.BLR, clientId));
        return clientId;
    }

    @Override
    public Client updateClient(Client client) throws CommandException {
        connection = connectionPool.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_CLIENT)) {
            statement.setString(1, client.getName());
            statement.setString(2, client.getType().name().toLowerCase());
            statement.setInt(3, client.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Update client failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return client;
    }

    @Override
    public Optional<Client> getClient(int clientId) throws CommandException {
        connection = connectionPool.getConnection();
        Optional<Client> optionalClient = Optional.empty();
        try (PreparedStatement statement = connection.prepareStatement(GET_CLIENT)) {
            statement.setInt(1, clientId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    optionalClient = Optional.of(getClient(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error("Get client failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return optionalClient;
    }

    @Override
    public List<Client> getClients() throws CommandException {
        connection = connectionPool.getConnection();
        List<Client> clients = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(GET_CLIENT_LIST)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    clients.add(getClient(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error("Get client list failed. Reason: {}", e.getMessage());
            throw new CommandException(e.getMessage());
        } finally {
            connectionPool.releaseConnection(connection);
        }
        return clients;
    }

    private Client getClient(ResultSet resultSet) throws SQLException {
        Client client = new Client();
        client.setId(resultSet.getInt("client_id"));
        client.setName(resultSet.getString("client_name"));
        client.setType(ClientTypes.valueOf(resultSet.getString("client_type").toUpperCase()));
        return client;
    }


    private int getGeneratedId(ResultSet resultSet) throws SQLException {
        resultSet.next();
        return resultSet.getInt(1);
    }
}
