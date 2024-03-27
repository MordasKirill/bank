package org.international.bank.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.international.bank.entity.Client;
import org.international.bank.entity.ClientTypes;
import org.international.bank.exception.CommandException;
import org.international.bank.exception.ValidationException;
import org.international.bank.repository.ClientRepository;
import org.international.bank.repository.impl.ClientRepositoryImpl;
import org.international.bank.service.ClientService;
import org.international.bank.validation.Validation;

import java.util.List;
import java.util.Optional;

@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl() {
        this.clientRepository = new ClientRepositoryImpl();
    }

    @Override
    public void deleteClient(String[] arguments) throws ValidationException, CommandException {
        if (Validation.getInstance().validateProvidedArguments(1, arguments)) {
            int clientId = Validation.getInstance().validateIntegerType(arguments[1], 1);
            clientRepository.deleteClient(clientId);
            log.info("Client with id:{} deleted.", clientId);
        }
    }

    @Override
    public void createClient(String[] arguments) throws ValidationException, CommandException {
        if (Validation.getInstance().validateProvidedArguments(3, arguments)) {
            ClientTypes clientTypes = Validation.getInstance().validateClientType(arguments[2]);
            int bankId = Validation.getInstance().validateIntegerType(arguments[3], 3);
            Client client = new Client();
            client.setName(arguments[1]);
            client.setType(clientTypes);
            int clientId = clientRepository.createClient(client, bankId);
            log.info("Client with id:{} created.", clientId);
        }
    }

    @Override
    public void updateClient(String[] arguments) throws CommandException, ValidationException {
        if (Validation.getInstance().validateProvidedArguments(3, arguments)) {
            ClientTypes clientTypes = Validation.getInstance().validateClientType(arguments[3]);
            int clientId = Validation.getInstance().validateIntegerType(arguments[1], 1);
            Client client = new Client();
            client.setId(clientId);
            client.setName(arguments[2]);
            client.setType(clientTypes);
            clientRepository.updateClient(client);
            log.info("Client with id:{} updated.", client.getId());
        }
    }

    @Override
    public void getClient(String[] arguments) throws ValidationException, CommandException {
        if (Validation.getInstance().validateProvidedArguments(1, arguments)) {
            int clientId = Validation.getInstance().validateIntegerType(arguments[1], 1);
            Optional<Client> client = clientRepository.getClient(clientId);
            if (client.isPresent()) {
                log.info(client.get().toString());
            } else {
                log.error("Client with id:{} not found.", clientId);
            }
        }
    }

    @Override
    public void getClients() throws CommandException {
        List<Client> clients = clientRepository.getClients();
        for (Client client : clients) {
            log.info(client.toString());
        }
    }
}
