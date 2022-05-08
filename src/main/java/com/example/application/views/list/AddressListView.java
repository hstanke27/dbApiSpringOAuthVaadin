package com.example.application.views.list;

import com.example.application.data.model.Address;
import com.example.application.service.AddressesService;
import com.example.application.security.oauth.UserSession;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import java.util.List;

@Route(value = "addresses", layout = MainLayout.class)
@PermitAll
public class AddressListView extends VerticalLayout {

    Grid<Address> cashAccountsGrid = new Grid<>(Address.class);

    public AddressListView(UserSession userSession, AddressesService addressesService) {
        addClassName("overview");
        setSizeFull();

        add(cashAccountsGrid);

        String accessToken = userSession.getAccessToken();
        configureGrid(addressesService.getAddresses(accessToken));
    }

    private void configureGrid(List<Address> addresses) {
        cashAccountsGrid.addClassName("addressesGrid");
        cashAccountsGrid.setSizeFull();
        cashAccountsGrid.setColumns("street", "houseNumber", "zip", "country", "registeredResidence", "addressType");
        cashAccountsGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        cashAccountsGrid.setItems(addresses);
    }

}
