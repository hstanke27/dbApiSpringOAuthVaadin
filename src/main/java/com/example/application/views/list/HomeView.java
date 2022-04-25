package com.example.application.views.list;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
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
        Span name = new Span("Unity");
        name.getStyle().set("padding-left", "1rem");
        root.add(name);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            Button loginButton = new Button();
            loginButton.setText("Login");
            loginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            loginButton.getStyle().set("padding-right", "1rem");
            loginButton.addClassName("toolbar");
            Anchor anchor = new Anchor("/login", loginButton);
            anchor.getElement().setAttribute("router-ignore", true);
            root.add(anchor);
        } else {
            Notification.show("Logged In");
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizedClient client = this.clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
            String accessToken = client.getAccessToken().getTokenValue();
            Notification.show("Logged in with token: " + accessToken);
        }

        root.setFlexGrow(1, name);
        root.addClassNames("contrast-5pct");

        return root;
    }


    private Component body() {
        VerticalLayout root = new VerticalLayout();
        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        root.add(img);

        root.add(new H2("This place intentionally left empty"));
        root.add(new Paragraph("It’s a place where you can grow your own UI 🤗"));

        root.setSizeFull();
        root.setJustifyContentMode(JustifyContentMode.CENTER);
        root.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        root.getStyle().set("text-align", "center");

        return root;
    }
}
