package com.example.application.data.model;

import java.util.List;

public class CashAccounts {

    private int totalItems;
    private int limit;
    private int offset;
    private List<CashAccount> accounts;

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

    public List<CashAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<CashAccount> accounts) {
        this.accounts = accounts;
    }
}
