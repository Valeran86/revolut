package com.revolut.payments.model;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Builder
@Data
public class Transaction {
    private final String id;
    private final String acc_dt;
    private final String acc_kt;
    private final double amount;

    public boolean isValid() {
        return ! (
                acc_dt == null || acc_kt == null ||
                Double.isNaN( amount ) || Double.isInfinite( amount ) ||
                StringUtils.isBlank( id )
        );
    }
}
