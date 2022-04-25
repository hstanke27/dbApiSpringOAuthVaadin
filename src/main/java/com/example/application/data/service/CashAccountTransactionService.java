package com.example.application.data.service;

import com.example.application.AppProperties;
import com.example.application.data.model.CashAccountTransaction;
import com.example.application.data.model.CashAccountTransactions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CashAccountTransactionService {

    @Autowired
    AppProperties appProperties;

    public List<CashAccountTransaction> getCashAccountTransactions(String accessToken, String iban) {
        RestTemplate restTemplate = new RestTemplate();

        String resourceUrl = appProperties.getDbApiCashAccountTransactionsUrl() + "?iban=" + iban;

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("iban", iban);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<CashAccountTransactions> response = restTemplate.exchange(
                resourceUrl,
                HttpMethod.GET,
                request,
                CashAccountTransactions.class
        );

        CashAccountTransactions cashAccountTransactions = response.getBody();

        return cashAccountTransactions.getTransactions();
    }

}
