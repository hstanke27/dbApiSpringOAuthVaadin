package com.example.application.views;

import com.example.application.security.oauth.UserSession;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import jakarta.annotation.security.PermitAll;

@Route(value = "start", layout = StartLayout.class)
@PermitAll
public class StartView extends VerticalLayout {

    public StartView(UserSession userSession) {
        setSpacing(false);
        setPadding(false);
        add(navbar());
        add(body());
    }

    private Component navbar() {
        HorizontalLayout root = new HorizontalLayout();
        root.setWidthFull();
        root.setAlignItems(Alignment.CENTER);

        root.setFlexGrow(1);
        root.addClassNames("contrast-5pct");

        return root;
    }

    private Component body() {
        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setJustifyContentMode(JustifyContentMode.CENTER);
        root.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        root.getStyle().set("text-align", "center");
        root.add(new H1("First page after login on Sample GAE application"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2AuthenticatedPrincipal principal) {

            root.add(new H2("You're logged in as: " + principal.getName()));

            // Direkt den Cash-Account-Button anzeigen, da der Login erfolgreich war
            Button accountButton = new Button("Go to cash account overview");
            accountButton.getStyle().set("text-align", "center");
            accountButton.addClassName("toolbar");

            Anchor anchorAccounts = new Anchor("/cashAccounts", accountButton);
            anchorAccounts.getElement().setAttribute("router-ignore", true);
            root.add(anchorAccounts);

        } else {
            root.add(new H2("Please log in."));
        }
        return root;
    }

}
