package com.example.application.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;

import jakarta.annotation.security.PermitAll;

@PermitAll
public class StartLayout extends AppLayout {

    // AuthenticationContext is not Serializable, so it must be kept transient
    // as documented by Vaadin when referenced from a view/layout field.
    private final transient AuthenticationContext authenticationContext;

    public StartLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
        createHaeder();
    }

    private void createHaeder() {
        H1 logo = new H1("Sample Google App Engine (GAE) application");
        logo.addClassNames("text-l", "m-m");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setSizeFull();
        header.addClassNames("py-0", "px-m");

        Button logoutButton = new Button("Logout");
        logoutButton.addClassName("logoutButton");
        logoutButton.setWidth("100px");
        // IMPORTANT: Manually clearing SecurityContextHolder and closing the
        // Vaadin UI session (the previous implementation) does NOT invoke
        // Spring Security's logout handlers, so the underlying HttpSession
        // and its persisted SecurityContext survive - the user appears
        // "logged out" for an instant but is immediately re-authenticated
        // from the still-valid session on the next request. The injectable
        // AuthenticationContext#logout() runs the full configured
        // LogoutHandler chain (session invalidation, SecurityContext
        // clearing, cookie deletion) and redirects per logoutSuccessUrl.
        logoutButton.addClickListener(event -> authenticationContext.logout());

        addToNavbar(header, logoutButton);
    }

}
