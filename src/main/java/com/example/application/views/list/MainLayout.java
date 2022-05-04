package com.example.application.views.list;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import org.springframework.security.core.context.SecurityContextHolder;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHaeder();
        createDrawer();
    }

    private void createHaeder() {
        H1 logo = new H1("Sample Application");
        logo.addClassNames("text-l", "m-m");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setSizeFull();
        header.addClassNames("py-0", "px-m");

        Button logoutButton = new Button("Logout");
        logoutButton.addClassName("logoutButton");
        logoutButton.setWidth("100px");
        logoutButton.addClickListener(event -> {
            SecurityContextHolder.clearContext();

            getUI().get().getSession().close();
            getUI().get().getCurrent().getPage().setLocation("/");
        });

        addToNavbar(header, logoutButton);
    }

    private void createDrawer() {
        RouterLink cashAccountsRouterLink = new RouterLink("Cash Accounts", CashAccountListView.class);
        cashAccountsRouterLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink addressesRoutLink = new RouterLink("Addresses", AddressListView.class);
        addressesRoutLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink dashBoardRouterLink = new RouterLink("Dashboard", DashboardView.class);
        dashBoardRouterLink.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(
            cashAccountsRouterLink,
            dashBoardRouterLink,
            addressesRoutLink
        ));
    }


}
