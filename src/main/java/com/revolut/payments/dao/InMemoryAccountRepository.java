package com.revolut.payments.dao;

import com.revolut.payments.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAccountRepository implements IAccountRepository {
    private static final Logger logger = LoggerFactory.getLogger( InMemoryAccountRepository.class );

    private static final Map<String, Account> ACCOUNT_STORE = new ConcurrentHashMap<>();

    @Override
    public String create( Account account4Creation ) {
        final String accountid = UUID.randomUUID().toString();
        Account account = Account.builder()
                .id( accountid )
                .accountNumber( account4Creation.getAccountNumber() )
                .client( account4Creation.getClient() )
                .amount( account4Creation.getAmount() )
                .build();
        logger.debug( "created account: " + account );

        ACCOUNT_STORE.put( accountid, account );

        return accountid;
    }

    @Override
    public Account getAccountById( String id ) {
        return ACCOUNT_STORE.get( id );
    }

    @Override
    public List<Account> getAccountList() {
        return new ArrayList<>( ACCOUNT_STORE.values() );
    }

    @Override
    public void clear() {
        ACCOUNT_STORE.clear();
    }
}
