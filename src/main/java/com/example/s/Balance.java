package com.example.s;

import java.math.BigDecimal;

public class Balance {
    public String name;
    BigDecimal balance;

    Balance(BigDecimal balance,String name) {
        this.name = name;
        this.balance = balance;
    }
}
