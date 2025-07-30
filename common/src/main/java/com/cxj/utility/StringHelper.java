/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.Collator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.cxj.utility.MatchHelper.COMPARE_RESULT_UNDEFINE;

/**
 *
 * @author Administrator
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public class StringHelper {

    public static int startWith(@Nonnull String content, int offset, String toMatch, boolean ignoreSpace) {
        if (ignoreSpace) {
            int i = offset;
            for (; i < content.length();) {
                int cp = content.codePointAt(i);
                if (!Character.isWhitespace(cp)) {
                    break;
                }
                i += Character.charCount(cp);
            }
            offset = i;
        }
        if (content.regionMatches(offset, toMatch, 0, toMatch.length())) {
            return offset;
        } else {
            return -1;
        }
    }

    public static int startWith(@Nonnull String content, int offset, String... toMatches) {
        for (int i = 0; i < toMatches.length; i++) {
            String toMatch = toMatches[i];
            if (toMatch == null) {
                continue;
            }
            if (content.regionMatches(offset, toMatch, 0, toMatch.length())) {
                return i;
            }
        }
        return -1;
    }

    public static int startWith(@Nonnull String content, int offset, Iterable<String> toMatches) {
        int i = 0;
        for (String toMatch : toMatches) {
            if (toMatch == null) {
                ++i;
                continue;
            }
            if (content.regionMatches(offset, toMatch, 0, toMatch.length())) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    private static String[] createToSearchs(String[] toSearchs, String escape, String... tokens) {
        int length = tokens.length / 3 * 2 + 1;
        if (toSearchs == null || toSearchs.length != length) {
            toSearchs = new String[length];
        }
        for (int i = 0; i < tokens.length / 3; i++) {
            toSearchs[i * 2] = tokens[i * 3];
            toSearchs[i * 2 + 1] = tokens[i * 3 + 1];
        }
        toSearchs[length - 1] = escape;
        return toSearchs;
    }

    private static int dealWithSymmetricalToken(@Nonnull Stack<String> stack, String token) {
        boolean empty = stack.isEmpty();

        if (!empty && Objects.equals(stack.peek(), token)) {
            stack.pop();
            return -1;
        } else {
            stack.push(token);
            return 1;
        }
    }

    private static boolean isSafe(@Nonnull String content, int pos) {
        return pos >= 0 && pos < content.length();
    }

    private static int forward(@Nonnull String content, int pos, int step) {
        pos += step;
        if (!isSafe(content, pos)) {
            pos = -1;
        }
        return pos;
    }

    /**
     * Find the end of the pairs. The offset should be beyond of the left pair For example:
     * <pre>
     * assert findPairEndIndex("[]", 0, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\") == -1
     * assert findPairEndIndex("[]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\") == 1
     * assert findPairEndIndex("[a]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\") == 2
     * assert findPairEndIndex("[[a]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\") == -1
     * assert findPairEndIndex("[[a]]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\") == 4
     * assert findPairEndIndex("[\"[a\"]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\") == 5
     * assert findPairEndIndex("[\"[a]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\") == -1
     * assert findPairEndIndex("[\"[a\\\"\"]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\") == 7
     * </pre>
     * @param content string to search
     * @param offset offset index
     * @param tokens [left pair, right pair, escape, ...ignores]
     * @return the end of the pairs
     */
    public static int findPairEndIndex(@Nonnull String content, int offset, String... tokens) {
        if (tokens.length < 3 || tokens.length % 3 != 0) {
            throw new IllegalArgumentException("Tokens: left, right, escape, ignores... Not enough tokens: " + Arrays.toString(tokens));
        }
        int pos = offset;
        Stack<String> balanceStack = new Stack<>();
        Stack<String> ignoreStack = new Stack<>();
        boolean[] flags = new boolean[tokens.length / 3];
        for (int i = 0; i < tokens.length / 3; i++) {
            flags[i] = Objects.equals(tokens[i * 3], tokens[i * 3 + 1]);
        }
        boolean ignore = false;
        String escape = tokens[2];
        String[] toSearchs = createToSearchs(null, escape, tokens);
        int escapeIdx = toSearchs.length - 1;
        String currentSymToken = null;
        while (true) {
            int tkIdx = -1;
            if (pos == -1) {
                tkIdx = -1;
            } else {
                int size = content.length();
                for (int i = pos; i < size; i++) {
                    int matched = startWith(content, i, toSearchs);
                    if (matched != -1) {
                        pos = i;
                        tkIdx = matched;
                        break;
                    }
                }
                if (tkIdx == -1) {
                    pos = -1;
                }
            }
            if (tkIdx != -1) {
                String token = toSearchs[tkIdx];
                boolean symmetrical = tkIdx != escapeIdx && flags[tkIdx / 2];
                if (symmetrical) {
                    if (tkIdx == 0 && !ignore) {
                        if (balanceStack.isEmpty()) {
                            return pos;
                        }
                    } else {
                        int result = dealWithSymmetricalToken(ignoreStack, token);
                        if (result == 1) {
                            if (ignoreStack.size() == 1) {
                                ignore = true;
                            }
                            escape = tokens[tkIdx / 2 * 3 + 2];
                            toSearchs = createToSearchs(toSearchs, escape, tokens);
                        } else if (result == -1) {
                            if (ignoreStack.isEmpty()) {
                                ignore = false;
                                escape = tokens[2];
                                toSearchs = createToSearchs(toSearchs, escape, tokens);
                            }
                        }
                    }
                } else {
                    if (tkIdx == 0 && !ignore) {
                        balanceStack.push(token);
                    } else if (tkIdx == 1 && !ignore) {
                        if (balanceStack.isEmpty()) {
                            return pos;
                        } else {
                            balanceStack.pop();
                        }
                    } else if (tkIdx == escapeIdx) {
                        pos = forward(content, pos, 1);
                    } else if (tkIdx != 0 && tkIdx != 1) {
                        if (tkIdx % 2 == 0) {
                            if (ignoreStack.isEmpty()) {
                                ignore = true;
                            }
                            ignoreStack.push(token);
                            escape = tokens[tkIdx / 2 * 3 + 2];
                            toSearchs = createToSearchs(toSearchs, escape, tokens);
                        } else {
                            if (ignoreStack.isEmpty()) {
                                throw new IllegalArgumentException("Mismatched token " + token + " for parsed string: " + content);
                            }
                            ignoreStack.pop();
                            if (ignoreStack.isEmpty()) {
                                ignore = false;
                                escape = tokens[2];
                                toSearchs = createToSearchs(toSearchs, escape, tokens);
                            }
                        }
                    }
                }
                pos = forward(content, pos, toSearchs[tkIdx].length());
            } else {
                return -1;
            }
        }
    }

    /**
     * find the first code unit from offset index which is not a space character, and return the index of it or return -1 when not find..
     * @param value input string
     * @param offset offset index
     * @return the index of the first code point which is not a space character or -1 when not find.
     */
    public static int skipSpace(String value, int offset) {
        int length = value.length();
        for (int i = offset; i < length;) {
            int cp = value.codePointAt(i);
            if (!Character.isWhitespace(cp)) {
                return i;
            }
            i += Character.charCount(cp);
        }
        return -1;
    }

    public static int nextSpace(String value, int offset) {
        int length = value.length();
        for (int i = offset; i < length;) {
            int cp = value.codePointAt(i);
            if (Character.isWhitespace(cp)) {
                return i;
            }
            i += Character.charCount(cp);
        }
        return -1;
    }
    
    /**
     * 判断是否为空字符串。
     * 非空字符串标准为保护任意可视字符
     * @param value 判断对象
     * @return 判断结果
     */
    public static boolean isEmpty(String value) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        PrimitiveIterator.OfInt iterator = value.codePoints().iterator();
        while (iterator.hasNext()) {
            int code = iterator.nextInt();
            if (!Character.isWhitespace(code)) {
                return false;
            }
        }
        return true;
    }

    public static boolean startsWithIngoreSpace(@Nonnull String value, @Nonnull String toMatch) {
        PrimitiveIterator.OfInt iter = value.codePoints().iterator();
        PrimitiveIterator.OfInt matchIter = toMatch.codePoints().iterator();
        while (iter.hasNext()) {
            int code = iter.nextInt();
            if (Character.isWhitespace(code)) {
                continue;
            }
            if (matchIter.hasNext()) {
                int matchCode = matchIter.nextInt();
                if (code != matchCode) {
                    return false;
                }
            }
        }
        return !matchIter.hasNext();
    }

    public static boolean endsWithIgnoreSpace(@Nonnull String value, @Nonnull String toMatch) {
        int i = value.length(), j = toMatch.length();
        for (; i > 0 && j > 0; --i) {
            int code = value.codePointBefore(i);
            if (!Character.isBmpCodePoint(code)) {
                --i;
            }
            if (Character.isWhitespace(code)) {
                continue;
            }
            int matchCode = toMatch.codePointBefore(j);
            if (Character.isBmpCodePoint(matchCode)) {
                --j;
            } else {
                --j;
                --j;
            }
            if (code != matchCode) {
                return false;
            }
        }
        return j > 0;
    }

    /**
     * 将普通字符串转换为兼容的正则表达式
     *
     * @param s 普通字符串
     * @param isInArray 是否将要放在正则表达式"[]"内部
     * @return 兼容的正则表达式
     */
    public static String toRegex(String s, boolean isInArray) {
        s = s.replaceAll("\\\\", Matcher.quoteReplacement("\\\\"));
        s = s.replaceAll("\\^", "\\\\^");
        s = s.replaceAll("\\$", Matcher.quoteReplacement("\\$"));
        s = s.replaceAll("\\(", "\\\\(");
        s = s.replaceAll("\\)", "\\\\)");
        s = s.replaceAll("\\*", "\\\\*");
        s = s.replaceAll("\\+", "\\\\+");
        s = s.replaceAll("\\?", "\\\\?");
        s = s.replaceAll("\\.", "\\\\.");
        s = s.replaceAll("\\[", "\\\\[");
        if (isInArray) {
            s = s.replaceAll("]", "\\\\]");
        }
        s = s.replaceAll("\\{", "\\\\{");
        return s.replaceAll("\\|", "\\\\|");
    }

    public static String unescape(@Nonnull String content, @Nullable String sign) {
        if (content.isEmpty()) {
            return content;
        }
        if (sign == null || sign.isEmpty()) {
            return content;
        }
        return content.replaceAll(Pattern.quote(sign) + "(.)", "$1");
    }

    /**
     * 将普通字符串转换为兼容的正则表达式，同
     * {@link #toRegex(java.lang.String, boolean) toRegex(s, false)}
     *
     * @param s 普通字符串
     * @return 兼容的正则表达式
     */
    public static String toRegex(String s) {
        return toRegex(s, false);
    }

    /**
     * sql语法中的like操作符。"%"，"_"为通配符。"%"匹配任意数量个任意字符，"_"匹配单个任意字符
     *
     * @param str 匹配对象
     * @param patten 待匹配的模式，支持使用"%"，"_"通配符
     * @param escape 指定转义符，为null或空时不支持转义符
     * @return 是否符合like语义
     */
    public static boolean jpqlLike(String str, String patten, String escape) {
        patten = toRegex(patten);
        if (escape == null || escape.isEmpty()) {
            patten = patten.replaceAll("%", "[\\\\s\\\\S]*").replaceAll("_", "[\\\\s\\\\S]");
        } else {
            escape = toRegex(escape);
            patten = patten.replaceAll("([^(?:" + escape + ")])%", "$1[\\\\s\\\\S]*")
                    .replaceAll("([^(?:" + escape + ")])_", "$1[\\\\s\\\\S]")
                    .replaceAll("(" + escape + ")%", "%")
                    .replaceAll("(" + escape + ")_", "_");
        }
        return str.matches(patten);
    }

    public static boolean jpqlLike(String str, String patten) {
        return jpqlLike(str, patten, null);
    }

    public static List<Boolean> jpqlLike(List<String> strs, String patten, String escape) {
        return strs.stream().map(e -> e == null ? null : jpqlLike(e, patten, escape)).collect(Collectors.toList());
    }

    public static List<Boolean> jpqlLike(List<String> strs, String patten) {
        return jpqlLike(strs, patten, null);
    }

    public static String parseEscape(String input, boolean isSingle) {
        String quote = isSingle ? "'" : "\"";
        return input.replaceAll("\\\\\\\\", "\\").replaceAll("\\\\n", "\n").replaceAll("\\\\r", "\r").replaceAll("\\\\" + toRegex(quote), quote);
    }

    public static boolean isEmptyString(Object input) {
        return input instanceof String && isEmpty((String) input);
    }

    public static boolean isNotEmptyString(Object input) {
        return input instanceof String && !isEmpty((String) input);
    }

    /**
     * 将String中的所有regex匹配的字串全部替换掉
     *
     * @param string 代替换的字符串
     * @param regex 替换查找的正则表达式
     * @param replacement 替换函数
     * @return 结果
     */
    public static String replaceAll(String string, String regex, ReplaceCallBack replacement) {
        return replaceAll(string, Pattern.compile(regex), replacement);
    }

    /**
     * 将String中的所有pattern匹配的字串替换掉
     *
     * @param string 代替换的字符串
     * @param pattern 替换查找的正则表达式对象
     * @param replacement 替换函数
     * @return 结果
     */
    public static String replaceAll(String string, Pattern pattern, ReplaceCallBack replacement) {
        if (string == null) {
            return null;
        }
        Matcher m = pattern.matcher(string);
        if (m.find()) {
            StringBuffer sb = new StringBuffer();
            int index = 0;
            while (true) {
                m.appendReplacement(sb, replacement.replace(m.group(0), index++, m));
                if (!m.find()) {
                    break;
                }
            }
            m.appendTail(sb);
            return sb.toString();
        }
        return string;
    }

    /**
     * 将String中的regex第一次匹配的字串替换掉
     *
     * @param string 代替换的字符串
     * @param regex 替换查找的正则表达式
     * @param replacement 替换函数
     * @return 结果
     */
    public static String replaceFirst(String string, String regex, ReplaceCallBack replacement) {
        return replaceFirst(string, Pattern.compile(regex), replacement);
    }

    /**
     * 将String中的pattern第一次匹配的字串替换掉
     *
     * @param string 代替换的字符串
     * @param pattern 替换查找的正则表达式对象
     * @param replacement 替换函数
     * @return 结果
     */
    public static String replaceFirst(String string, Pattern pattern, ReplaceCallBack replacement) {
        if (string == null) {
            return null;
        }
        Matcher m = pattern.matcher(string);
        StringBuffer sb = new StringBuffer();
        if (m.find()) {
            m.appendReplacement(sb, replacement.replace(m.group(0), 0, m));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String trimLeft(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.replaceAll("^\\s+", "");
    }

    public static String trimRight(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.replaceAll("\\s+$", "");
    }

    /**
     * Map<String,Object> map = new HashMap();
     * map.put("name","world");
     * StringHelper.template("hello ${name}!",map) = "hello world!";
     * @param pattern 模板
     * @param map 参数
     * @return 结果
     */
    public static String template(String pattern, Map<String, Object> map) {
        if (map == null || map.size() == 0) {
            return pattern;
        }

        String result = pattern;

        for (String key : map.keySet()) {
            String value = String.valueOf(map.get(key));
            result = result.replaceAll("\\$\\{" + key + "?}", value);
        }

        return result;
    }

    /**
     * StringHelper.template("Hello {0}!", "Edison") = "Hello Edison!";
     * @param pattern 模板
     * @param args 参数
     * @return 模板填充结果
     */
    public static String template(String pattern, Object... args) {
        if (args == null || args.length == 0) {
            return pattern;
        }
        if (args.length == 1 && args[0] instanceof Map) {
            //noinspection unchecked
            return template(pattern, (Map<String, Object>) args[0]);
        }

        String result = pattern;
        for (int i = 0; i < args.length; i++) {
            String key = String.valueOf(i);
            String value = String.valueOf(args[i]);
            result = result.replaceAll("\\{" + key + "?}", value);
        }

        return result;
    }

    @Nonnull
    public static NullableStringComparator nullableComparator() {
        return new NullableStringComparator();
    }

    @Nonnull
    public static NullableIgnoreCaseStringComparator nullableIgnoreCaseComparator() {
        return new NullableIgnoreCaseStringComparator();
    }

    @Nonnull
    public static NullableIgnoreCaseStringComparator nullableIgnoreCaseComparator(@Nullable Locale locale) {
        return new NullableIgnoreCaseStringComparator(locale);
    }

    @Nonnull
    public static NonnullStringComparator nonnullComparator() {
        return new NonnullStringComparator();
    }

    @Nonnull
    public static NonnullIgnoreCaseStringComparator nonnullIgnoreCaseComparator() {
        return new NonnullIgnoreCaseStringComparator();
    }

    @Nonnull
    public static NonnullIgnoreCaseStringComparator nonnullIgnoreCaseComparator(@Nullable Locale locale) {
        return new NonnullIgnoreCaseStringComparator(locale);
    }

    public static class NullableStringComparator implements Comparator<String> {

        @Override
        public int compare(@Nullable String o1, @Nullable String o2) {
            return MatchHelper.compareObject(o1, o2);
        }
    }

    public static class NullableIgnoreCaseStringComparator implements Comparator<String> {

        private final Locale locale;

        public NullableIgnoreCaseStringComparator() {
            this(null);
        }

        public NullableIgnoreCaseStringComparator(Locale locale) {
            this.locale = locale;
        }

        private Collator getCollator() {
            return locale != null ? Collator.getInstance(locale) : Collator.getInstance();
        }

        @Override
        public int compare(@Nullable String o1, @Nullable String o2) {
            int res = MatchHelper.compareNull(o1, o2);
            if (res != COMPARE_RESULT_UNDEFINE) {
                return res;
            } else {
                return getCollator().compare(o1, o2);
            }
        }
    }

    public static class NonnullStringComparator implements Comparator<String> {

        @Override
        public int compare(@Nonnull String o1, @Nonnull String o2) {
            return o1.compareTo(o2);
        }
    }

    public static class NonnullIgnoreCaseStringComparator implements Comparator<String> {

        private final Locale locale;

        public NonnullIgnoreCaseStringComparator() {
            this(null);
        }

        public NonnullIgnoreCaseStringComparator(Locale locale) {
            this.locale = locale;
        }

        private Collator getCollator() {
            return locale != null ? Collator.getInstance(locale) : Collator.getInstance();
        }

        @Override
        public int compare(@Nonnull String o1, @Nonnull String o2) {
            return getCollator().compare(o1, o2);
        }
    }
}
