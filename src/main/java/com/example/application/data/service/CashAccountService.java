package com.example.application.data.service;

import com.example.application.AppProperties;
import com.example.application.data.model.CashAccount;
import com.example.application.data.model.CashAccounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class CashAccountService {

    @Autowired
    AppProperties appProperties;

    public List<CashAccount> getCashAccountList(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = appProperties.getDbApiCashAccountsUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<CashAccounts> response = restTemplate.exchange(
                resourceUrl,
                HttpMethod.GET,
                request,
                CashAccounts.class
        );

        CashAccounts cashAccounts = response.getBody();

        return cashAccounts.getAccounts();
    }

    public CashAccounts getCashAccount(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = appProperties.getDbApiCashAccountsUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<CashAccounts> response = restTemplate.exchange(
                resourceUrl,
                HttpMethod.GET,
                request,
                CashAccounts.class
        );

        CashAccounts cashAccounts = response.getBody();
        return cashAccounts;
    }

}
