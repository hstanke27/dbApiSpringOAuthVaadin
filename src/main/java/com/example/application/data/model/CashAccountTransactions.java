package com.example.application.data.model;

import java.util.List;

public class CashAccountTransactions {

    private int totalItems;
    private int limit;
    private int offset;
    private List<CashAccountTransaction> transactions;

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<CashAccountTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<CashAccountTransaction> transactions) {
        this.transactions = transactions;
    }
}
