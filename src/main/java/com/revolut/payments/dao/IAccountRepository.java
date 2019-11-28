package com.revolut.payments.dao;

import com.revolut.payments.model.Account;

import java.util.List;

public interface IAccountRepository {
    String create( Account account4Creation );

    Account getAccountById( String id );

    List<Account> getAccountList();

    void clear();
}
