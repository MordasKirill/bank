package org.international.bank.repository;

import org.international.bank.entity.Client;
import org.international.bank.exception.CommandException;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {

    void deleteClient(int clientId) throws CommandException;
    int createClient(Client client, int bankId) throws CommandException;
    Client updateClient(Client client) throws CommandException;
    Optional<Client> getClient(int clientId) throws CommandException;
    List<Client> getClients() throws CommandException;
}
