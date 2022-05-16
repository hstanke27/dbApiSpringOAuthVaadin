package com.example.application.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@PageTitle("Home")
@Route(value = "")
@AnonymousAllowed
public class HomeView extends VerticalLayout {
    private final OAuth2AuthorizedClientService clientService;

    public HomeView(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
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

        root.add(new H1("Sample Google App Engine (GAE) application"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            Button loginButton = new Button();
            loginButton.setText("Login");
            loginButton.getStyle().set("text-align", "center");
            loginButton.addClassName("toolbar");

            Anchor anchorLogin = new Anchor("/login", loginButton);
            anchorLogin.getElement().setAttribute("router-ignore", true);
            root.add(anchorLogin);
        } else {
            Notification.show("Logged In");
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizedClient client = this.clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
            String accessToken = client.getAccessToken().getTokenValue();
            Notification.show("Logged in with token: " + accessToken);
        }

        root.setSizeFull();
        root.setJustifyContentMode(JustifyContentMode.CENTER);
        root.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        root.getStyle().set("text-align", "center");

        return root;
    }
}
