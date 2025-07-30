/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.bean;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.expression.DefaultResolver;
import org.apache.commons.beanutils.expression.Resolver;

/**
 *
 * @author Administrator
 */
public class ExResolver implements Resolver {

    private static final char NESTED = '.';
    private static final char MAPPED_START = '(';
    private static final char MAPPED_END = ')';
    private static final char INDEXED_START = '[';
    private static final char INDEXED_END = ']';

    private final Resolver resolver;
    private final Map<String, String> pMap;

    public ExResolver(Map<String, String> pMap) {
        this(new DefaultResolver(), pMap);
    }

    public ExResolver(Resolver resolver, Map<String, String> pMap) {
        this.resolver = resolver;
        this.pMap = new HashMap<>(pMap);
    }

    private String resolveExpr(String expression) {
        for (Map.Entry<String, String> entry : pMap.entrySet()) {
            if (expression.startsWith(entry.getValue())) {
                String to = entry.getValue();
                if (expression.length() == entry.getValue().length()) {
                    return entry.getKey();
                } else {
                    int toTest = expression.codePointAt(to.length());
                    if (toTest == NESTED || toTest == MAPPED_START || toTest == INDEXED_START) {
                        return entry.getKey() + expression.substring(to.length(), expression.length());
                    } else {
                        return expression;
                    }
                }
            }
        }
        return expression;
    }

    @Override
    public int getIndex(String expression) {
        expression = resolveExpr(expression);
        return resolver.getIndex(expression);
    }

    @Override
    public String getKey(String expression) {
        expression = resolveExpr(expression);
        return resolver.getKey(expression);
    }

    @Override
    public String getProperty(String expression) {
        expression = resolveExpr(expression);
        return resolver.getProperty(expression);
    }

    @Override
    public boolean hasNested(String expression) {
        expression = resolveExpr(expression);
        return resolver.hasNested(expression);
    }

    @Override
    public boolean isIndexed(String expression) {
        expression = resolveExpr(expression);
        return resolver.isIndexed(expression);
    }

    @Override
    public boolean isMapped(String expression) {
        expression = resolveExpr(expression);
        return resolver.isMapped(expression);
    }

    @Override
    public String next(String expression) {
        expression = resolveExpr(expression);
        return resolver.next(expression);
    }

    @Override
    public String remove(String expression) {
        expression = resolveExpr(expression);
        return resolver.remove(expression);
    }

}
