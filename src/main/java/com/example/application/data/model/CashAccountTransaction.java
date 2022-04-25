package com.example.application.data.model;

import java.math.BigDecimal;

public class CashAccountTransaction {

    private String originIban;
    private BigDecimal amount;
    private String counterPartyName;
    private String counterPartyIban;
    private String paymentReference;
    private String bookingDate;
    private String currencyCode;
    private String transactionCode;
    private String externalBankTransactionDomainCode;
    private String externalBankTransactionFamilyCode;
    private String externalBankTransactionSubFamilyCode;
    private String mandateReference;
    private String creditorId;
    private String e2eReference;
    private String paymentIdentification;
    private String valueDate;

    public String getOriginIban() {
        return originIban;
    }

    public void setOriginIban(String originIban) {
        this.originIban = originIban;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCounterPartyName() {
        return counterPartyName;
    }

    public void setCounterPartyName(String counterPartyName) {
        this.counterPartyName = counterPartyName;
    }

    public String getCounterPartyIban() {
        return counterPartyIban;
    }

    public void setCounterPartyIban(String counterPartyIban) {
        this.counterPartyIban = counterPartyIban;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getExternalBankTransactionDomainCode() {
        return externalBankTransactionDomainCode;
    }

    public void setExternalBankTransactionDomainCode(String externalBankTransactionDomainCode) {
        this.externalBankTransactionDomainCode = externalBankTransactionDomainCode;
    }

    public String getExternalBankTransactionFamilyCode() {
        return externalBankTransactionFamilyCode;
    }

    public void setExternalBankTransactionFamilyCode(String externalBankTransactionFamilyCode) {
        this.externalBankTransactionFamilyCode = externalBankTransactionFamilyCode;
    }

    public String getExternalBankTransactionSubFamilyCode() {
        return externalBankTransactionSubFamilyCode;
    }

    public void setExternalBankTransactionSubFamilyCode(String externalBankTransactionSubFamilyCode) {
        this.externalBankTransactionSubFamilyCode = externalBankTransactionSubFamilyCode;
    }

    public String getMandateReference() {
        return mandateReference;
    }

    public void setMandateReference(String mandateReference) {
        this.mandateReference = mandateReference;
    }

    public String getCreditorId() {
        return creditorId;
    }

    public void setCreditorId(String creditorId) {
        this.creditorId = creditorId;
    }

    public String getE2eReference() {
        return e2eReference;
    }

    public void setE2eReference(String e2eReference) {
        this.e2eReference = e2eReference;
    }

    public String getPaymentIdentification() {
        return paymentIdentification;
    }

    public void setPaymentIdentification(String paymentIdentification) {
        this.paymentIdentification = paymentIdentification;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }
}
