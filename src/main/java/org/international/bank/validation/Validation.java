package org.international.bank.validation;

import lombok.extern.slf4j.Slf4j;
import org.international.bank.entity.Account;
import org.international.bank.entity.ClientTypes;
import org.international.bank.entity.Currency;
import org.international.bank.exception.ValidationException;

@Slf4j
public class Validation {

    private static Validation validation;

    private Validation() {
    }

    public static Validation getInstance() {
        if (validation == null) {
            validation = new Validation();
        }
        return validation;
    }

    public boolean validateProvidedArguments(int requiredArgumentsNum, String[] arguments) {
        boolean result = true;
        if (arguments.length - 1 != requiredArgumentsNum) {
            log.error("Arguments amount mismatch, use help to see command signature.");
            result = false;
        }
        return result;
    }

    public int validateIntegerType(String string, int argumentPosition) throws ValidationException {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new ValidationException(String.format("Argument %d should be int.", argumentPosition));
        }
    }

    public ClientTypes validateClientType(String type) throws ValidationException {
        for (ClientTypes currency : ClientTypes.values()) {
            if (!currency.name().equalsIgnoreCase(type.toUpperCase())) {
                return ClientTypes.valueOf(type);
            }
        }
        throw new ValidationException("Client type could only INDIVIDUAL or LEGAL.");
    }

    public Currency validateCurrency(String currencyString) throws ValidationException {
        for (Currency currency : Currency.values()) {
            if (currency.name().equalsIgnoreCase(currencyString)) {
                return Currency.valueOf(currencyString.toUpperCase());
            }
        }
        throw new ValidationException("Client type could be (USD, EUR, BLR, RUB).");
    }

    public double validateDoubleType(String string, int argumentNum) throws ValidationException {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            throw new ValidationException(String.format("Argument %d should be double (0.0).", argumentNum));
        }
    }

    public void checkAccountBalance(Account sender, double amount) throws ValidationException {
        if (sender.getAccountBalance() - amount < 0) {
            throw new ValidationException("Insufficient  funds.");
        }
    }
}
