package com.cxj.parser;

import com.cxj.utility.StringHelper;

import java.util.*;

/**
 * Created by vipcxj on 2018/6/27.
 */
public class Tokenizer implements Enumeration<String> {

    private List<String> symbols;

    private final String value;
    private int pos;

    public Tokenizer(String value) {
        this.value = value;
        this.pos = 0;
        this.symbols = new ArrayList<>();
    }

    public Tokenizer addSymbol(String symbol) {
        this.symbols.add(symbol);
        return this;
    }

    public Tokenizer addSymbols(Collection<String> symbols) {
        this.symbols.addAll(symbols);
        return this;
    }

    public Tokenizer removeSymbol(String symbol) {
        this.symbols.remove(symbol);
        return this;
    }

    public Tokenizer removeSymbols(Collection<String> symbols) {
        this.symbols.removeAll(symbols);
        return this;
    }

    public Tokenizer setSymbols(Collection<String> symbols) {
        this.symbols.clear();
        this.symbols.addAll(symbols);
        return this;
    }

    public Tokenizer clearSymbols() {
        this.symbols.clear();
        return this;
    }

    @Override
    public boolean hasMoreElements() {
        if (pos == -1) {
            return false;
        }
        pos = StringHelper.skipSpace(value, pos);
        return pos != -1;
    }

    @Override
    public String nextElement() {
        if (!hasMoreElements()) {
            throw new NoSuchElementException();
        }
        int i = StringHelper.startWith(value, pos, symbols);
        if (i == -1) {
            i = StringHelper.nextSpace(value, pos);
            String token = value.substring(pos, i != -1 ? i : value.length());
            pos = i;
            return token;
        } else {
            String token = symbols.get(i);
            pos += token.length();
            if (pos == value.length()) {
                pos = -1;
            }
            return token;
        }
    }

    public int nextPos() {
        if (!hasMoreElements()) {
            return -1;
        }
        return pos;
    }
}
