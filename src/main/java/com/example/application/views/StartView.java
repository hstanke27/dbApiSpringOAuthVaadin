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

import javax.annotation.security.PermitAll;

@Route(value = "/start", layout = StartLayout.class)
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
        if(authentication != null) {
            OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();

            if (principal.getAttribute("sub") != null && principal.getAttribute("acr") != null) {
                root.add(new H2("You're logged in with " + principal.getName()));

                Button loginButton = new Button();
                loginButton.setText("Go to cash account overview");
                loginButton.getStyle().set("text-align", "center");
                loginButton.addClassName("toolbar");

                Anchor anchorLogin = new Anchor("/cashAccounts", loginButton);
                anchorLogin.getElement().setAttribute("router-ignore", true);
                root.add(anchorLogin);
            } else {
                root.add(new H2("You're logged in with " + principal.getName() + ". You're now able" +
                        "to consume APIs from your Github account"));
            }
        }
        return root;
    }

}
