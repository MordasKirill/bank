package org.international.bank.validation;

import org.international.bank.entity.Account;
import org.international.bank.entity.ClientTypes;
import org.international.bank.entity.Currency;
import org.international.bank.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

    @Test
    void testValidateProvidedArguments() {
        Validation validation = Validation.getInstance();
        String[] args = {"arg1", "arg2", "arg3"};
        assertFalse(validation.validateProvidedArguments(1, args));
    }

    @Test
    void testValidateIntegerType() throws ValidationException {
        Validation validation = Validation.getInstance();
        int result = validation.validateIntegerType("123", 1);
        assertEquals(123, result);
    }

    @Test
    void testValidateClientType() throws ValidationException {
        Validation validation = Validation.getInstance();
        ClientTypes clientType = validation.validateClientType("INDIVIDUAL");
        assertEquals(ClientTypes.INDIVIDUAL, clientType);
    }

    @Test
    void testValidateCurrency() throws ValidationException {
        Validation validation = Validation.getInstance();
        Currency currency = validation.validateCurrency("USD");
        assertEquals(Currency.USD, currency);
    }

    @Test
    void testValidateDoubleType() throws ValidationException {
        Validation validation = Validation.getInstance();
        double result = validation.validateDoubleType("12.34", 1);
        assertEquals(12.34, result, 0.001);
    }

    @Test
    void testCheckAccountBalance() throws ValidationException {
        Validation validation = Validation.getInstance();
        Account sender = new Account();
        sender.setAccountBalance(100);
        assertDoesNotThrow(() -> validation.checkAccountBalance(sender, 50.0));
        assertThrows(ValidationException.class, () -> validation.checkAccountBalance(sender, 150.0));
    }
}