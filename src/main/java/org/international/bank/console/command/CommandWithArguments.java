package org.international.bank.console.command;

import org.international.bank.exception.CommandException;
import org.international.bank.exception.ValidationException;

@FunctionalInterface
public interface CommandWithArguments {
    void execute(String[] arguments) throws ValidationException, CommandException;
}
