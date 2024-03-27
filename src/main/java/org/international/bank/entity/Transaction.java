package org.international.bank.entity;

import java.sql.Date;
import java.util.Objects;

public class Transaction {

    private int id;
    private int senderAccountId;
    private int receiverAccountId;
    private double amount;
    private Date transactionDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderAccountId() {
        return senderAccountId;
    }

    public void setSenderAccountId(int senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public int getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(int receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id && senderAccountId == that.senderAccountId && receiverAccountId == that.receiverAccountId && Double.compare(amount, that.amount) == 0 && Objects.equals(transactionDate, that.transactionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, senderAccountId, receiverAccountId, amount, transactionDate);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", senderAccountId=" + senderAccountId +
                ", receiverAccountId=" + receiverAccountId +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
