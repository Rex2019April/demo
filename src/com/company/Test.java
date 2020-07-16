package com.company;

import java.util.regex.Pattern;

public class Test {

    private static final  String regex="\\b[A-Za-z]{3}\\b";
    private static final Pattern p= Pattern.compile(regex);
    public static void main(String[] args) throws Exception {
        Main m = new Main();
//        m.main(new String[]{"-cdata/currency.txt", "-edata/exchange.txt"});
        t();
    }

    static void t(){
        String a="abc";
        String b="a b c";
        String c="abc 1";
        String d="ab1 ab 12a";
        d=" aaa BBB ";

        System.out.println(Pattern.matches(regex, a));
        System.out.println(Pattern.matches(regex, b));

        System.out.println(Pattern.matches(regex, c));
        System.out.println(Pattern.matches(regex, d));
        d=" aaa BBB ";
        System.out.println(Pattern.matches(regex, d));
        d=" BBB ";
        System.out.println(Pattern.matches(regex, d));
        d="a1a";
        System.out.println(Pattern.matches(regex, d));
        d="AAA";
        System.out.println(Pattern.matches(regex, d));
        d="Azz";
        System.out.println(Pattern.matches(regex, d));
    }
}
