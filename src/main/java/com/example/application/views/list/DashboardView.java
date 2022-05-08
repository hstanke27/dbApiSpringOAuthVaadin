package com.example.application.views.list;

import com.example.application.data.model.CashAccount;
import com.example.application.data.model.CashAccountTransaction;
import com.example.application.security.oauth.UserSession;
import com.example.application.service.CashAccountService;
import com.example.application.service.CashAccountTransactionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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

        String accessToken = userSession.getAccessToken();
        for(CashAccount cashAccount : cashAccountService.getCashAccount(accessToken).getAccounts())  {
            add(getCashAccountTransactionsStats(accessToken, cashAccount),
                getCashAccountTransactionsChart(accessToken, cashAccount));
        }
    }

    private Component getCashAccountTransactionsStats(String accessToken, CashAccount cashAccount) {
        List<CashAccountTransaction> cashAccountTransactions =
                cashAccountTransactionService.getCashAccountTransactions(accessToken, cashAccount.getIban());

        Span stats = new Span("Grouped cash account exchanges which have at least 2 identical " +
                "payment references for IBAN " + cashAccount.getIban());
        stats.addClassNames("text-tl-" + cashAccount.getIban(), "mt-m-" + cashAccount.getIban());
        return stats;
    }

    private Component getCashAccountTransactionsChart(String accessToken, CashAccount cashAccount) {
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();

        Tooltip tooltip = new Tooltip();
        tooltip.setValueDecimals(2);
        conf.setTooltip(tooltip);

        PlotOptionsPie plotOptions = new PlotOptionsPie();
        plotOptions.setAllowPointSelect(true);
        plotOptions.setCursor(Cursor.POINTER);
        plotOptions.setShowInLegend(true);
        conf.setPlotOptions(plotOptions);

        DataSeries dataSeries = new DataSeries();

        List<CashAccountTransaction> cashAccountTransactions =
                cashAccountTransactionService.getCashAccountTransactions(accessToken, cashAccount.getIban());

        //Check expenses only and only transactions which have 2 or more identical payment references
        Map<String, AtomicReference> counterPartyNameCounter = new HashMap<>();

        cashAccountTransactions.forEach(cashAccountTransaction -> {
            String counterPartyName = cashAccountTransaction.getCounterPartyName();
            BigDecimal amount = cashAccountTransaction.getAmount();
            if(amount.compareTo(BigDecimal.ZERO) < 0) {
                if (!counterPartyNameCounter.containsKey(counterPartyName)) {
                    AtomicReference<Integer> counter = new AtomicReference<>(0);
                    counter.getAndSet(counter.get() + 1);
                    counterPartyNameCounter.put(counterPartyName, counter);
                } else {
                    AtomicReference<Integer> atomicReference = counterPartyNameCounter.get(counterPartyName);
                    atomicReference.getAndSet(atomicReference.get() + 1);
                    counterPartyNameCounter.put(counterPartyName, atomicReference);
                }
            }
        });

        SortedMap<String, BigDecimal> paymentReferenceAmountMap = new TreeMap<>();
        cashAccountTransactions.forEach(cashAccountTransaction -> {
            String counterPartyName = cashAccountTransaction.getCounterPartyName();
            BigDecimal amount = cashAccountTransaction.getAmount();

            AtomicReference<Integer> paymentReferenceCounter = counterPartyNameCounter.get(counterPartyName);
            if(paymentReferenceCounter != null && paymentReferenceCounter.get() > 1) {
                if (!paymentReferenceAmountMap.containsKey(counterPartyName)) {
                    paymentReferenceAmountMap.put(counterPartyName, amount);
                } else {
                    BigDecimal currentAmount = paymentReferenceAmountMap.get(counterPartyName);
                    BigDecimal newAmount = currentAmount.add(amount);
                    paymentReferenceAmountMap.put(counterPartyName, newAmount);
                }
            }
        });

        paymentReferenceAmountMap.forEach(
                //Values must be positive in this chart - thus amount.abs() is required here.
                (paymentReference, amount) -> dataSeries.add(new DataSeriesItem(paymentReference, amount.abs()))
        );

        conf.setSeries(dataSeries);
        chart.setVisibilityTogglingDisabled(true);

        return chart;
    }


}
