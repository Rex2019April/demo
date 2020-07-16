package com.company;

public class Test {

    public static void main(String[] args) throws Exception {
        Main m = new Main();
        m.main(new String[]{"-cdata/currency.txt", "-edata/exchange.txt"});
    }
}
