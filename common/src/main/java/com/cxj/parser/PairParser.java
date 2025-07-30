package com.cxj.parser;

import com.cxj.parser.impl.ImmutablePairParser;
import com.cxj.parser.impl.PairParserImpl;
import com.cxj.utility.MatchHelper;
import com.cxj.utility.StringHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by vipcxj on 2018/2/2.
 */
public interface PairParser extends Cloneable {

    Comparator<String> COMPARATOR = MatchHelper.reverse(MatchHelper.reverse(StringHelper.nonnullComparator()));
    PairParser PAR_ALL = immutable(from(null, null, false));
    PairParser PAR_PARENTHESIS = immutable(from("(", ")", false));
    PairParser PAR_PARENTHESIS_NEST = immutable(from("(", ")", true));
    PairParser PAR_BRACKET = immutable(from("[", "]", false));
    PairParser PAR_BRACKET_NEST = immutable(from("[", "]", true));
    PairParser PAR_DL_BRACKET = immutable(from("$[", "]", false));
    PairParser PAR_SP_BRACKET = immutable(from("#[", "]", false));
    PairParser PAR_DB_BRACKET = immutable(from("[[", "]]", false));
    PairParser PAR_BRACE = immutable(from("{", "}", false));
    PairParser PAR_BRACE_NEST = immutable(from("{", "}", true));
    PairParser PAR_DL_BRACE = immutable(from("${", "}", false));
    PairParser PAR_SP_BRACE = immutable(from("#{", "}", false));
    PairParser PAR_DB_BRACE = immutable(from("{{", "}}", false));
    PairParser PAR_ANG_BRACKET = immutable(from("<", ">", false));
    PairParser PAR_ANG_BRACKET_NEST = immutable(from("<", ">", true));
    PairParser PAR_DB_QUOTE = immutable(symToken("\"", "\\"));
    PairParser PAR_SG_QUOTE = immutable(symToken("'", "\\"));

    boolean isSymmetrical();

    @Nullable
    String getEscape();

    @Nullable
    String getLeft();

    @Nullable
    String getRight();

    default boolean isEmptyLeft() {
        return getLeft() == null || getLeft().isEmpty();
    }

    default boolean isEmptyRight() {
        return getRight() == null || getRight().isEmpty();
    }

    int findLeft(@Nonnull String content, int offset);

    default int findLeft(@Nonnull String content) {
        return findLeft(content, 0);
    }

    int findRight(@Nonnull String content, int offset);

    @Nullable
    String findContent(@Nonnull String content, int offset, boolean escape);

    @Nullable
    default String findContent(@Nonnull String content, int offset) {
        return findContent(content, offset, true);
    }

    @Nullable
    default String findContent(@Nonnull String content) {
        return findContent(content, 0, true);
    }

    @Nonnull
    String unescape(@Nonnull String content, int posLeft, int posRight);

    @Nonnull
    default String unescape(@Nonnull String content, int posLeft) {
        return unescape(content, posLeft, -1);
    }

    @Nonnull
    PairParser addSubContext(PairParser context);

    @Nonnull
    default PairParser addSubContexts(PairParser... parsers) {
        for (PairParser parser : parsers) {
            addSubContext(parser);
        }
        return this;
    }

    @Nonnull
    default PairParser addSubContexts(Iterable<PairParser> parsers) {
        for (PairParser parser : parsers) {
            addSubContext(parser);
        }
        return this;
    }

    @Nonnull
    default StringBuilder replaceBuilder(@Nonnull String input, @Nonnull Function<String, ?> replacer) {
        String rightToken = getRight();
        int szRightToken = rightToken != null ? rightToken.length() : 0;
        int offset = 0;
        int left = findLeft(input, offset);
        if (left == -1) {
            return new StringBuilder(input);
        }
        StringBuilder sb = new StringBuilder();
        while (true) {
            int right = findRight(input, left);
            if (right == -1) {
                throw new IllegalArgumentException("Unable to parse " + input + ".");
            }
            sb.append(input, offset, left);
            String capture = unescape(input, left, right);
            sb.append(replacer.apply(capture));
            offset = right + szRightToken;
            if (offset == input.length()) {
                break;
            }
            left = findLeft(input, offset);
            if (left == -1) {
                sb.append(input, offset, input.length());
                break;
            }
        }
        return sb;
    }

    @Nonnull
    default String replace(@Nonnull String input, @Nonnull Function<String, ?> replacer) {
        if (isEmptyLeft() && isEmptyRight()) {
            return input;
        }
        int offset = 0;
        int left = findLeft(input, offset);
        if (left == -1) {
            return input;
        }
        return replaceBuilder(input, replacer).toString();
    }

    @Nonnull
    default String replace(@Nonnull String input, @Nonnull Map<String, ?> map) {
        return replace(input, map::get);
    }

    default void foreach(@Nonnull String input, Consumer<String> action) {
        String rightToken = getRight();
        if (isEmptyLeft() && isEmptyRight()) {
            action.accept(unescape(input, 0, -1));
        }
        int szRightToken = rightToken != null ? rightToken.length() : 0;
        int offset = 0;
        int left = findLeft(input, offset);
        if (left == -1) {
            return;
        }
        while (true) {
            int right = findRight(input, left);
            if (right == -1) {
                throw new IllegalArgumentException("Unable to parse " + input + ".");
            }
            String capture = unescape(input, left, right);
            action.accept(capture);
            offset = right + szRightToken;
            if (offset == input.length()) {
                break;
            }
            left = findLeft(input, offset);
            if (left == -1) {
                break;
            }
        }
    }

    @Nonnull
    default <T> List<T> collect(@Nonnull String input, Function<String, T> collector) {
        String rightToken = getRight();
        if (isEmptyLeft() && isEmptyRight()) {
            List<T> result = new ArrayList<>();
            String capture = unescape(input, 0, -1);
            result.add(collector.apply(capture));
            return result;
        }
        int szRightToken = rightToken != null ? rightToken.length() : 0;
        int offset = 0;
        int left = findLeft(input, offset);
        if (left == -1) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>();
        while (true) {
            int right = findRight(input, left);
            if (right == -1) {
                throw new IllegalArgumentException("Unable to parse " + input + ".");
            }
            String capture = unescape(input, left, right);
            result.add(collector.apply(capture));
            offset = right + szRightToken;
            if (offset == input.length()) {
                break;
            }
            left = findLeft(input, offset);
            if (left == -1) {
                break;
            }
        }
        return result;
    }

    Object clone() throws CloneNotSupportedException;

    default PairParser copy() {
        try {
            return (PairParser) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    static PairParser from(@Nullable String left, @Nullable String right, @Nullable String escape) {
        return new PairParserImpl(left, right, escape);
    }

    static PairParser from(@Nullable String left, @Nullable String right, @Nullable String escape, boolean nest) {
        return new PairParserImpl(left, right, escape, nest);
    }

    static PairParser from(@Nullable String left, @Nullable String right) {
        return new PairParserImpl(left, right);
    }

    static PairParser from(@Nullable String left, @Nullable String right, boolean nest) {
        return new PairParserImpl(left, right, nest);
    }

    static PairParser symToken(@Nonnull String token, @Nullable String escape) {
        return new PairParserImpl(token, token, escape);
    }

    static PairParser symToken(@Nonnull String token) {
        return new PairParserImpl(token, token, null);
    }

    static PairParser immutable(@Nullable PairParser parser) {
        return parser != null ? new ImmutablePairParser(parser) : null;
    }
}
