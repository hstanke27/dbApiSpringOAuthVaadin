package com.example.application.views.list;

import com.example.application.data.model.CashAccount;
import com.example.application.data.model.CashAccounts;
import com.example.application.data.service.CashAccountService;
import com.example.application.security.oauth.UserSession;
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

    public DashboardView(UserSession userSession, CashAccountService cashAccountService) {
        this.userSession = userSession;
        this.cashAccountService = cashAccountService;

        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        add(getCashAccountStats(), getCashAccountsChart());
    }

    private Component getCashAccountStats() {
        String accessToken = userSession.getAccessToken();
        CashAccounts cashAccounts = cashAccountService.getCashAccount(accessToken);
        Span stats = new Span(cashAccounts.getTotalItems() + " cash account");
        stats.addClassNames("text-tl", "mt-m");
        return stats;
    }

    private Component getCashAccountsChart() {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        String accessToken = userSession.getAccessToken();
        List<CashAccount> cashAccounts = cashAccountService.getCashAccountList(accessToken);
        cashAccounts.forEach(cashAccount -> {
            dataSeries.add(new DataSeriesItem(cashAccount.getIban(), cashAccount.getCurrentBalance()));
        });
        chart.getConfiguration().setSeries(dataSeries);
        return chart;
    }


}
