package com.revolut.payments;

import com.revolut.payments.api.AccountRest;
import com.revolut.payments.api.TransactionRest;
import com.revolut.payments.dao.IAccountRepository;
import com.revolut.payments.dao.InMemoryAccountRepository;
import com.revolut.payments.service.JsonTransformer;

import static spark.Spark.*;

public class PaymentApp {

    private static final InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();

    public static void main( String[] args ) {
        // can be in `args` if it's necessary
        final String apiRoot = "pay/api";
        final int defaultPort = 9876;

        final JsonTransformer jsonTransformer = new JsonTransformer();
        final AccountRest accountRest = new AccountRest( accountRepository, jsonTransformer );
        final TransactionRest transactionRest = new TransactionRest( accountRepository, jsonTransformer );

        port( defaultPort );

        // accounts
        get( apiRoot + "/accountList", "application/json" , accountRest::getAccounts, jsonTransformer );

        get( apiRoot + "/account", "application/json", accountRest::getAccount, jsonTransformer );
        put( apiRoot + "/account", "application/json", accountRest::createAccount, jsonTransformer );

        // transaction
        post( apiRoot + "/transfer", "application/json", transactionRest::transfer, jsonTransformer );
    }

    static IAccountRepository getAccountRepository() {
        return accountRepository;
    }
}
