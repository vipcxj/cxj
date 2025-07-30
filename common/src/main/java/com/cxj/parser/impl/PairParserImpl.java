package com.cxj.parser.impl;

import com.cxj.parser.PairParser;
import com.cxj.utility.MatchHelper;
import com.cxj.utility.StringHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by vipcxj on 2018/2/2.
 */
public class PairParserImpl implements PairParser {

    final static Comparator<String> COMPARATOR = MatchHelper.reverse(MatchHelper.reverse(StringHelper.nonnullComparator()));

    private String left;
    private String right;
    private String escape;
    private Map<String, PairParser> subContexts;
    private String[] toSearches;
    private int idxRight;
    private int idxEscape;

    public PairParserImpl(@Nullable String left, @Nullable String right, @Nullable String escape, boolean nest) {
        this.left = left;
        this.right = right;
        this.escape = escape;
        if (escape != null && (Objects.equals(escape, left) || Objects.equals(escape, right))) {
            throw new IllegalArgumentException("The escape token should not be equal to the left or right token. left: " + left + ", right: " + left + ", escape: " + escape);
        }
        if (nest) {
            getSubContexts().put(left, this);
        }
    }

    public PairParserImpl(@Nullable String left, @Nullable String right, @Nullable String escape) {
        this(left, right, escape, true);
    }

    public PairParserImpl(@Nullable String left, @Nullable String right, boolean nest) {
        this(left, right, null, nest);
    }

    public PairParserImpl(@Nullable String left, @Nullable String right) {
        this(left, right, null);
    }

    public PairParserImpl(@Nullable String token, boolean nest) {
        this(token, token, null, nest);
    }

    public PairParserImpl(@Nullable String token) {
        this(token, token);
    }

    @Override
    public boolean isSymmetrical() {
        return Objects.equals(left, right);
    }

    @Nullable
    @Override
    public String getEscape() {
        return escape;
    }

    @Nullable
    @Override
    public String getLeft() {
        return left;
    }

    @Nullable
    @Override
    public String getRight() {
        return right;
    }

    private String[] getToSearches() {
        if (toSearches == null) {
            int sub = 0;
            int base = 2;
            if (escape == null || escape.isEmpty()) {
                idxEscape = -1;
                sub++;
                base--;
            } else {
                idxEscape = 0;
            }
            if (right == null || right.isEmpty()) {
                idxRight = -1;
            } else {
                idxRight = base - 1;
            }
            toSearches = new String[2 + getSubContextsCount() - sub];
            if (idxEscape != -1) {
                toSearches[idxEscape] = escape;
            }
            if (idxRight != -1) {
                toSearches[idxRight] = right;
            }
            if (subContexts != null) {
                int i = 0;
                for (String key : subContexts.keySet()) {
                    toSearches[base + i++] = key;
                }
            }
        }
        return toSearches;
    }

    Map<String, PairParser> getSubContexts() {
        if (subContexts == null) {
            subContexts = new TreeMap<>(COMPARATOR);
        }
        return subContexts;
    }

    PairParser getSubContext(String left) {
        return getSubContexts().get(left);
    }

    int getSubContextsCount() {
        return subContexts != null ? subContexts.size() : 0;
    }

    public PairParser addSubContextAndGet(PairParser context) {
        if (context.isEmptyLeft()) {
            throw new IllegalArgumentException("Invalid sub parser. The sub parser's left token should not be null or empty.");
        }
        if (context.isEmptyRight()) {
            throw new IllegalArgumentException("Invalid sub parser. The sub parser's right token should not be null or empty.");
        }
        if (Objects.equals(context.getLeft(), right) || Objects.equals(context.getLeft(), escape)) {
            throw new IllegalArgumentException("Invalid sub parser. The sub parser's left token should not be equal the parent's right token or escape token.");
        }
        getSubContexts().put(context.getLeft(), context);
        return context;
    }

    public PairParser addSubContextAndGet(String left, String right, String escape) {
        return addSubContextAndGet(PairParser.from(left, right, escape));
    }

    public PairParser addSubContextAndGet(String left, String right) {
        return addSubContextAndGet(PairParser.from(left, right));
    }

    public PairParser addSubContextAndGet(String token) {
        return addSubContextAndGet(PairParser.symToken(token));
    }

    @Override
    @Nonnull
    public PairParserImpl addSubContext(PairParser context) {
        addSubContextAndGet(context);
        return this;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        PairParserImpl clone = (PairParserImpl) super.clone();
        clone.subContexts = subContexts != null ? new HashMap<>(subContexts) : null;
        clone.toSearches = null;
        return clone;
    }

    public PairParserImpl addSubContext(String left, String right, String escape) {
        addSubContextAndGet(left, right, escape);
        return this;
    }

    public PairParserImpl addSubContext(String left, String right) {
        addSubContextAndGet(left, right);
        return this;
    }

    public PairParserImpl addSubContext(String token) {
        addSubContextAndGet(token);
        return this;
    }

    private static boolean isSafe(int pos, int limit) {
        return pos >= 0 && pos < limit;
    }

    private static int cursor(int pos, int limit) {
        return isSafe(pos, limit) ? pos : -1;
    }

    private static int forward(int pos, int limit, int step) {
        pos += step;
        if (!isSafe(pos, limit)) {
            pos = -1;
        }
        return pos;
    }

    @Nullable
    @Override
    public String findContent(@Nonnull String content, int offset, boolean unescape) {
        if (offset == 0 && isEmptyLeft() && isEmptyRight()) {
            return unescape ? unescape(content, 0, -1) : content;
        }
        int left = findLeft(content, offset);
        if (left != -1) {
            int right = findRight(content, left);
            if (right != -1) {
                if (unescape && this.escape != null && !this.escape.isEmpty()) {
                    return unescape(content, left, right);
                } else {
                    return content.substring(left + this.left.length(), right);
                }
            }
        }
        return null;
    }

    @Override
    public int findLeft(@Nonnull String content, int offset) {
        if (isEmptyLeft()) {
            return offset;
        }
        int size = content.length();
        for (int i = offset; i < size; i++) {
            int matched = StringHelper.startWith(content, i, left, false);
            if (matched != -1) {
                return matched;
            }
        }
        return -1;
    }

    @Override
    public int findRight(@Nonnull String content, int offset) {
        if (!isEmptyLeft() && StringHelper.startWith(content, offset, left, false) != offset) {
            throw new IllegalArgumentException("Invalid offset! The offset must be the position of left token.");
        }
        if (isEmptyRight()) {
            return content.length();
        }
        int size = content.length();
        int pos = forward(offset, size, left.length());
        String[] toSearches = getToSearches();
        while (pos != -1) {
            int tkIdx = -1;
            for (int i = pos; i < size; i++) {
                int matched = StringHelper.startWith(content, i, toSearches);
                if (matched != -1) {
                    pos = cursor(i, size);
                    tkIdx = matched;
                    break;
                }
            }
            if (tkIdx == -1) {
                return -1;
            }
            if (tkIdx == idxRight) {
                return pos;
            } else if (tkIdx == idxEscape) {
                pos = forward(pos, size, escape.length() + 1);
            } else {
                String token = toSearches[tkIdx];
                PairParser subParser = getSubContext(token);
                int right = subParser.findRight(content, pos);
                if (right == -1) {
                    return -1;
                } else {
                    pos = forward(right, size, Objects.requireNonNull(subParser.getRight()).length());
                }
            }
        }
        return -1;
    }

    @Nonnull
    public String unescape(@Nonnull String content, int posLeft, int posRight) {
        if (isEmptyRight() && posRight == -1) {
            posRight = content.length();
        }
        if (!isEmptyLeft() && StringHelper.startWith(content, posLeft, left, false) != posLeft) {
            throw new IllegalArgumentException("Invalid left pos: " + posLeft + ".");
        }
        if (!isEmptyRight() && posRight != -1 && StringHelper.startWith(content, posRight, right, false) != posRight) {
            throw new IllegalArgumentException("Invalid right pos: " + posRight + ".");
        }
        int size = content.length();
        if ((escape == null || escape.isEmpty()) && posRight != -1) {
            int szLeftToken = isEmptyLeft() ? 0 : left.length();
            int start = posLeft + szLeftToken;
            if (start == 0 && posRight == size) {
                return content;
            } else {
                return content.substring(start, posRight);
            }
        }
        StringBuilder sb = new StringBuilder();
        int start = forward(posLeft, size, left.length());
        String[] toSearches = getToSearches();
        while (start != -1) {
            int tkIdx = -1;
            int pos = -1;
            for (int i = start; i < size; i++) {
                int matched = StringHelper.startWith(content, i, toSearches);
                if (matched != -1) {
                    pos = cursor(i, size);
                    tkIdx = matched;
                    break;
                }
            }
            if (tkIdx == -1) {
                throw new IllegalArgumentException("Unable to parse the string: " + content);
            }
            sb.append(content, start, pos);
            if (tkIdx == idxRight) {
                if (posRight != -1 && posRight != pos) {
                    throw new IllegalArgumentException("Invalid right pos: " + posRight + ".");
                }
                return sb.toString();
            } else if (tkIdx == idxEscape) {
                sb.append(content, pos + escape.length(), pos + escape.length() + 1);
                start = forward(pos, size, escape.length() + 1);
            } else {
                String token = toSearches[tkIdx];
                PairParser subParser = getSubContext(token);
                int right = subParser.findRight(content, pos);
                if (right == -1) {
                    throw new IllegalArgumentException("Unable to parse the string: " + content);
                } else {
                    int szRightToken = Objects.requireNonNull(subParser.getRight()).length();
                    sb.append(content, pos, right + szRightToken);
                    start = forward(right, size, szRightToken);
                }
            }
        }
        throw new IllegalArgumentException("Unable to parse the string: " + content);
    }
}
