package com.example.application.views.list;

import com.example.application.data.model.CashAccount;
import com.example.application.data.model.CashAccountTransaction;
import com.example.application.data.model.CashAccounts;
import com.example.application.service.CashAccountService;
import com.example.application.security.oauth.UserSession;
import com.example.application.service.CashAccountTransactionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import java.util.List;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard Cash Accounts")
@PermitAll
public class DashboardView extends VerticalLayout {

    private UserSession userSession;
    private CashAccountService cashAccountService;
    private CashAccountTransactionService cashAccountTransactionService;

    public DashboardView(UserSession userSession, CashAccountService cashAccountService, CashAccountTransactionService cashAccountTransactionService) {
        this.userSession = userSession;
        this.cashAccountService = cashAccountService;
        this.cashAccountTransactionService = cashAccountTransactionService;

        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        add(getCashAccountTransactionsStats(), getCashAccountTransactionsChart());
    }

    private Component getCashAccountTransactionsStats() {
        String accessToken = userSession.getAccessToken();
        CashAccounts cashAccounts = cashAccountService.getCashAccount(accessToken);
        CashAccount cashAccount = cashAccounts.getAccounts().get(0);

        List<CashAccountTransaction> cashAccountTransactions =
                cashAccountTransactionService.getCashAccountTransactions(accessToken, cashAccount.getIban());

        Span stats = new Span(cashAccountTransactions.size() + " cash account transactions for IBAN " + cashAccount.getIban());
        stats.addClassNames("text-tl", "mt-m");
        return stats;
    }

    private Component getCashAccountTransactionsChart() {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        String accessToken = userSession.getAccessToken();
        CashAccounts cashAccounts = cashAccountService.getCashAccount(accessToken);
        CashAccount cashAccount = cashAccounts.getAccounts().get(0);

        List<CashAccountTransaction> cashAccountTransactions =
                cashAccountTransactionService.getCashAccountTransactions(accessToken, cashAccount.getIban());

        cashAccountTransactions.forEach(cashAccountTransaction -> {
            dataSeries.add(new DataSeriesItem(cashAccountTransaction.getCounterPartyName(), cashAccountTransaction.getAmount()));
        });
        chart.getConfiguration().setSeries(dataSeries);
        return chart;
    }


}
