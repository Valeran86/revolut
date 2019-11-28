package com.revolut.payments.service;

import com.revolut.payments.exception.NotEnoughMoneyException;
import com.revolut.payments.model.Account;
import com.revolut.payments.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.UnexpectedException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionProcessor {
    private static final Logger logger = LoggerFactory.getLogger( TransactionProcessor.class );

    private static final Set<Account> accountLockSet = new HashSet<>();
    private static final ReentrantLock locker = new ReentrantLock();

    public static Transaction transfer( Transaction transaction, Account acc_dt, Account acc_kt )
            throws UnexpectedException, NotEnoughMoneyException {
        try {
            logger.trace( "transaction: " + transaction +
                    "\nacc_dt: " + acc_dt +
                    "\nacc_kt: " + acc_kt );
            lockAccounts( transaction, acc_dt, acc_kt );

            double amount = acc_dt.getAmount();
            if ( transaction.getAmount() > amount ) {
                throw new NotEnoughMoneyException( "Account's amount less then transaction's amount" );
            }

            acc_dt.setAmount( amount - transaction.getAmount() );
            acc_kt.setAmount( acc_kt.getAmount() + transaction.getAmount() );

            return Transaction.builder()
                    .id( UUID.randomUUID().toString() )
                    .acc_dt( transaction.getAcc_dt() )
                    .acc_kt( transaction.getAcc_kt() )
                    .amount( transaction.getAmount() )
                    .build();
        } finally {
            unlockAccounts( transaction, acc_dt, acc_kt );
        }
    }

    private static void lockAccounts( Transaction transaction, Account acc_dt, Account acc_kt ) throws UnexpectedException {
        try {
            // check if accounts in lock
            while ( accountLockSet.contains( acc_dt ) || accountLockSet.contains( acc_kt ) ) {
                Thread.sleep( 100 );
            }

            // only one thread able to put account in set
            locker.lock();
            try {
                accountLockSet.add( acc_dt );
                accountLockSet.add( acc_kt );
                logger.info( "account locked: " +
                        "\nacc_dt: " + acc_dt +
                        "\nacc_kt: " + acc_kt );
            } finally {
                locker.unlock();
            }
        } catch ( InterruptedException i ) {
            throw new UnexpectedException( "transaction " + transaction + " didn't process", i );
        }
    }

    private static void unlockAccounts( Transaction transaction, Account acc_dt, Account acc_kt ) {
        // only one thread able to put account in set
        locker.lock();
        try {
            accountLockSet.remove( acc_dt );
            accountLockSet.remove( acc_kt );
            logger.info( "account unlocked: " +
                    "\nacc_dt: " + acc_dt +
                    "\nacc_kt: " + acc_kt );
        } finally {
            locker.unlock();
        }
    }

}
