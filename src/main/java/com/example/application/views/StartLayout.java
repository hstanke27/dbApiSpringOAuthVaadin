package com.example.application.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.security.core.context.SecurityContextHolder;

public class StartLayout extends AppLayout {
    public StartLayout() {
        createHaeder();
    }

    private void createHaeder() {
        H1 logo = new H1("Sample GAE application");
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

}
