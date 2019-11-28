package com.revolut.payments.api;

import com.revolut.payments.dao.IAccountRepository;
import com.revolut.payments.exception.NotEnoughMoneyException;
import com.revolut.payments.model.Account;
import com.revolut.payments.model.Transaction;
import com.revolut.payments.service.JsonTransformer;
import com.revolut.payments.service.TransactionProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.rmi.UnexpectedException;

@RequiredArgsConstructor
public class TransactionRest {
    private static final Logger logger = LoggerFactory.getLogger( TransactionRest.class );

    private final IAccountRepository accountService;
    private final JsonTransformer jsonTransformer;

    public Object transfer( Request req, Response res ) {
        Transaction transaction = jsonTransformer.unmarshal( req.body(), Transaction.class );
        logger.debug( "transaction for processing: " + transaction );

        Account acc_dt = accountService.getAccountById( transaction.getAcc_dt() );
        Account acc_kt = accountService.getAccountById( transaction.getAcc_kt() );
        if ( acc_dt == null || acc_kt == null ) {
            logger.error( "One of account is not found, acc_dt: " +
                    acc_dt + " acc_dt: " + acc_kt );
            // do not show account details in error message
            throw new IllegalStateException( "One of account is not found, acc_dt: " +
                    ( acc_dt == null ) + " acc_dt: " + ( acc_kt == null ) );
        }

        Transaction result = transaction;
        try {
            result = TransactionProcessor.transfer( transaction, acc_dt, acc_kt );
        } catch ( UnexpectedException | NotEnoughMoneyException e ) {
            logger.error( "Transaction did't process from " + acc_dt.getAccountNumber() + " to " + acc_kt.getAccountNumber(), e );
            res.status( HttpStatus.SC_INTERNAL_SERVER_ERROR );
        }

        return result;
    }
}
