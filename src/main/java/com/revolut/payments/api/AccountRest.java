package com.revolut.payments.api;

import com.revolut.payments.dao.IAccountRepository;
import com.revolut.payments.model.Account;
import com.revolut.payments.service.JsonTransformer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import static com.revolut.payments.model.Account.ID_FIELD_NAME;

@RequiredArgsConstructor
public class AccountRest {
    private static final Logger logger = LoggerFactory.getLogger( AccountRest.class );

    private final IAccountRepository accountService;
    private final JsonTransformer jsonTransformer;

    public Object getAccounts( Request req, Response resp ) {
        return  accountService.getAccountList().toArray();
    }

    public Object getAccount( Request req, Response resp ) {
        String accountId = req.queryParams( ID_FIELD_NAME );
        logger.debug( "account id for search: " + accountId );

        return accountService.getAccountById( accountId );
    }

    public Object createAccount( Request req, Response resp ) {
        Account account = jsonTransformer.unmarshal( req.body(), Account.class );
        logger.debug( "account for creation: " + account );

        return accountService.create( account );
    }
}
