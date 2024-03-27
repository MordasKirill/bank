package org.international.bank.entity;

import java.util.Objects;

public class Account {
    private int id;
    private int bankId;
    private double accountBalance;
    private Currency accountCurrency;
    private int clientId;

    public Account(){}

    public Account(int bankId, Currency accountCurrency, int clientId) {
        this.bankId = bankId;
        this.accountCurrency = accountCurrency;
        this.clientId = clientId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Currency getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(Currency accountCurrency) {
        this.accountCurrency = accountCurrency;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id && bankId == account.bankId && Double.compare(accountBalance, account.accountBalance) == 0 && clientId == account.clientId && Objects.equals(accountCurrency, account.accountCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bankId, accountBalance, accountCurrency, clientId);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", bankId=" + bankId +
                ", accountBalance=" + accountBalance +
                ", accountCurrency='" + accountCurrency + '\'' +
                ", clientId=" + clientId +
                '}';
    }
}
