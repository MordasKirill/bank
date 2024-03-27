package org.international.bank.console.command;

import org.international.bank.exception.CommandException;

@FunctionalInterface
public interface CommandWithoutArguments {
    void execute() throws CommandException;
}
