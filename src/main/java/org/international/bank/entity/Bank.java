package org.international.bank.entity;

import java.util.Objects;

public class Bank {
    private int id;
    private String name;
    private double legalFee;
    private double individualFee;
    private double externalFee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLegalFee() {
        return legalFee;
    }

    public void setLegalFee(double legalFee) {
        this.legalFee = legalFee;
    }

    public double getIndividualFee() {
        return individualFee;
    }

    public void setIndividualFee(double individualFee) {
        this.individualFee = individualFee;
    }

    public double getExternalFee() {
        return externalFee;
    }

    public void setExternalFee(double externalFee) {
        this.externalFee = externalFee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bank bank = (Bank) o;
        return id == bank.id && Double.compare(legalFee, bank.legalFee) == 0 && Double.compare(individualFee, bank.individualFee) == 0 && Double.compare(externalFee, bank.externalFee) == 0 && Objects.equals(name, bank.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, legalFee, individualFee, externalFee);
    }

    @Override
    public String toString() {
        return "Bank{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", legalFee=" + legalFee +
                ", individualFee=" + individualFee +
                ", externalFee=" + externalFee +
                '}';
    }
}
