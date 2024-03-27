package org.international.bank.service;

import org.international.bank.exception.CommandException;
import org.international.bank.exception.ValidationException;

public interface ClientService {

    void deleteClient(String[] arguments) throws ValidationException, CommandException;
    void createClient(String[] arguments) throws ValidationException, CommandException;
    void updateClient(String[] arguments) throws CommandException, ValidationException;
    void getClient(String[] arguments) throws ValidationException, CommandException;
    void getClients() throws CommandException;
}
