package com.example.application.views;

import com.example.application.data.model.CashAccount;
import com.example.application.data.model.CashAccountTransaction;
import com.example.application.security.oauth.UserSession;
import com.example.application.service.CashAccountService;
import com.example.application.service.CashAccountTransactionService;
import com.storedobject.chart.CategoryData;
import com.storedobject.chart.Data;
import com.storedobject.chart.PieChart;
import com.storedobject.chart.SOChart;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll; // Note: Use jakarta.annotation.security.PermitAll for Spring Boot 3/4 & Vaadin 25

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard Cash Accounts")
@PermitAll
public class DashboardView extends VerticalLayout {

    private final UserSession userSession;
    private final CashAccountService cashAccountService;
    private final CashAccountTransactionService cashAccountTransactionService;

    public DashboardView(UserSession userSession,
                         CashAccountService cashAccountService,
                         CashAccountTransactionService cashAccountTransactionService) {
        this.userSession = userSession;
        this.cashAccountService = cashAccountService;
        this.cashAccountTransactionService = cashAccountTransactionService;

        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        String accessToken = this.userSession.getAccessToken();
        for (CashAccount cashAccount : this.cashAccountService.getCashAccount(accessToken).getAccounts()) {
            add(getCashAccountTransactionsStats(accessToken, cashAccount),
                    getCashAccountTransactionsChart(accessToken, cashAccount));
        }
    }

    private Component getCashAccountTransactionsStats(String accessToken, CashAccount cashAccount) {
        Span stats = new Span("Grouped cash account exchanges which have at least 2 identical " +
                "payment references for IBAN " + cashAccount.getIban());
        stats.addClassNames("text-tl-" + cashAccount.getIban(), "mt-m-" + cashAccount.getIban());
        return stats;
    }

    private Component getCashAccountTransactionsChart(String accessToken, CashAccount cashAccount) {
        SOChart soChart = new SOChart();
        // SOChart (Apache ECharts) requires explicit dimensions to render visibly in a VerticalLayout
        soChart.setSize("800px", "500px");

        List<CashAccountTransaction> cashAccountTransactions =
                cashAccountTransactionService.getCashAccountTransactions(accessToken, cashAccount.getIban());

        // Count expenses (< 0) per counterparty
        Map<String, Integer> counterPartyNameCounter = new HashMap<>();
        for (CashAccountTransaction tx : cashAccountTransactions) {
            if (tx.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                counterPartyNameCounter.merge(tx.getCounterPartyName(), 1, Integer::sum);
            }
        }

        // Sum amounts for counterparties appearing 2 or more times
        SortedMap<String, BigDecimal> paymentReferenceAmountMap = new TreeMap<>();
        for (CashAccountTransaction tx : cashAccountTransactions) {
            String counterPartyName = tx.getCounterPartyName();
            if (counterPartyNameCounter.getOrDefault(counterPartyName, 0) > 1) {
                paymentReferenceAmountMap.merge(counterPartyName, tx.getAmount(), BigDecimal::add);
            }
        }

        CategoryData labels = new CategoryData();
        Data values = new Data();

        paymentReferenceAmountMap.forEach((paymentReference, amount) -> {
            labels.add(paymentReference);
            // Round to 2 decimal places (replaces Vaadin Tooltip setValueDecimals(2))
            BigDecimal positiveRounded = amount.abs().setScale(2, RoundingMode.HALF_UP);
            values.add(positiveRounded);
        });

        PieChart pieChart = new PieChart(labels, values);
        pieChart.setName("Expenses"); // Displayed in tooltip header
        soChart.add(pieChart);

        return soChart;
    }
}
