package com.cxj.parser.impl;

import com.cxj.parser.PairParser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by vipcxj on 2018/2/11.
 */
public class ImmutablePairParser implements PairParser {

    private PairParser wrapped;

    public ImmutablePairParser(PairParser wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean isSymmetrical() {
        return wrapped.isSymmetrical();
    }

    @Override
    @Nullable
    public String getEscape() {
        return wrapped.getEscape();
    }

    @Override
    @Nullable
    public String getLeft() {
        return wrapped.getLeft();
    }

    @Override
    @Nullable
    public String getRight() {
        return wrapped.getRight();
    }

    @Override
    public int findLeft(@Nonnull String content, int offset) {
        return wrapped.findLeft(content, offset);
    }

    @Override
    public int findRight(@Nonnull String content, int offset) {
        return wrapped.findRight(content, offset);
    }

    @Override
    @Nullable
    public String findContent(@Nonnull String content, int offset) {
        return wrapped.findContent(content, offset);
    }

    @Nonnull
    @Override
    public String unescape(@Nonnull String content, int posLeft, int posRight) {
        return wrapped.unescape(content, posLeft, posRight);
    }

    @Override
    @Nullable
    public String findContent(@Nonnull String content, int offset, boolean escape) {
        return wrapped.findContent(content, offset, escape);
    }

    @Override
    @Nonnull
    public PairParser addSubContext(PairParser context) {
        throw new UnsupportedOperationException("This parser is immutable!");
    }

    @Override
    @Nonnull
    public String replace(@Nonnull String input, @Nonnull Function<String, ?> replacer) {
        return wrapped.replace(input, replacer);
    }

    @Override
    @Nonnull
    public String replace(@Nonnull String input, @Nonnull Map<String, ?> map) {
        return wrapped.replace(input, map);
    }

    @Override
    public void foreach(@Nonnull String input, Consumer<String> action) {
        wrapped.foreach(input, action);
    }

    @Override
    @Nonnull
    public <T> List<T> collect(@Nonnull String input, Function<String, T> collector) {
        return wrapped.collect(input, collector);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        PairParser cloneWrapped = (PairParser) wrapped.clone();
        ImmutablePairParser clone = (ImmutablePairParser) super.clone();
        clone.wrapped = cloneWrapped;
        return clone;
    }

    @Override
    public PairParser copy() {
        return wrapped.copy();
    }
}
