package com.example.application.views.list;

import com.example.application.data.model.CashAccountTransaction;
import com.example.application.security.oauth.UserSession;
import com.example.application.service.CashAccountTransactionService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import java.util.List;

@Route("transactions/:iban?")
@PageTitle("Cash Account Transactions")
@PermitAll
public class CashAccountTransactionsListView extends VerticalLayout implements BeforeEnterObserver {

    Grid<CashAccountTransaction> cashAccountTransactionsGrid = new Grid<>(CashAccountTransaction.class);

    UserSession userSession;
    CashAccountTransactionService cashAccountTransactionService;

    public CashAccountTransactionsListView(UserSession userSession, CashAccountTransactionService cashAccountTransactionService) {
        this.userSession = userSession;
        this.cashAccountTransactionService = cashAccountTransactionService;
        addClassName("overview");
        setSizeFull();
        add(cashAccountTransactionsGrid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String iban = event.getRouteParameters().get("iban").orElse(null);
        if(iban != null) {
            String accessToken = userSession.getAccessToken();
            configureGrid(cashAccountTransactionService.getCashAccountTransactions(accessToken, iban));
        }
    }

    private void configureGrid(List<CashAccountTransaction> cashAccountTransactions) {
        cashAccountTransactionsGrid.addClassName("cashAccountTransactionsGrid");
        cashAccountTransactionsGrid.setSizeFull();
        cashAccountTransactionsGrid.setColumns("originIban", "amount", "counterPartyName", "counterPartyIban", "paymentReference", "bookingDate", "valueDate", "currencyCode");
        cashAccountTransactionsGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        cashAccountTransactionsGrid.setItems(cashAccountTransactions);
    }


}