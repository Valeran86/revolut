package com.revolut.payments.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Account {
    public static final String ID_FIELD_NAME = "id";

    private final String id;
    private final String accountNumber;
    private final String client; // in real task it should be another model element
    private volatile double amount;
}
