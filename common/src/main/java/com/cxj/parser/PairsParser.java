package com.cxj.parser;

import com.cxj.utility.StringHelper;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Created by vipcxj on 2018/2/11.
 */
public class PairsParser {

    private final Map<String, PairParser> parsers;
    private String[] toSearches;

    PairsParser(@Nonnull Iterable<PairParser> parsers) {
        this.parsers = new TreeMap<>(PairParser.COMPARATOR);
        for (PairParser parser : parsers) {
            checkSubParser(parser);
            this.parsers.put(parser.getLeft(), parser);
        }
    }

    public static PairsParser from(@Nonnull Iterable<PairParser> parsers) {
        return new PairsParser(parsers);
    }

    public static PairsParser from(PairParser... parsers) {
        return new PairsParser(Arrays.asList(parsers));
    }

    private String[] getToSearches() {
        if (toSearches == null) {
            toSearches = new String[parsers.size()];
            int i = 0;
            for (String key : parsers.keySet()) {
                toSearches[i++] = key;
            }

        }
        return toSearches;
    }

    private void checkSubParser(PairParser parser) {
        if (parser.isEmptyLeft()) {
            throw new IllegalArgumentException("Invalid sub parser. The sub parser's left token should not be null or empty.");
        }
        if (parser.isEmptyRight()) {
            throw new IllegalArgumentException("Invalid sub parser. The sub parser's right token should not be null or empty.");
        }
    }

    public Iterator<Block> blockIterator(@Nonnull String input) {
        return new BlockIterator(input);
    }

    public StringBuilder replaceBuilder(@Nonnull String input, @Nonnull BiFunction<Block, String, ?> replacer) {
        Iterator<Block> iterator = blockIterator(input);
        StringBuilder sb = new StringBuilder();
        int start = 0;
        while (iterator.hasNext()) {
            Block block = iterator.next();
            sb.append(input, start, block.left);
            sb.append(replacer.apply(block, block.getContent(input, true)));
            start = block.getRightEnd();
        }
        if (start < input.length()) {
            sb.append(input, start, input.length());
        }
        return sb;
    }

    public String replace(@Nonnull String input, @Nonnull BiFunction<Block, String, ?> replacer) {
        return replaceBuilder(input, replacer).toString();
    }

    public String replace(@Nonnull String input, @Nonnull Map<String, ?> replacer) {
        return replace(input, (block, content) -> replacer.get(content));
    }

    public void foreach(@Nonnull String input, @Nonnull BiConsumer<Block, String> processor) {
        Iterator<Block> iterator = blockIterator(input);
        while (iterator.hasNext()) {
            Block block = iterator.next();
            processor.accept(block, block.getContent(input, true));
        }
    }

    public int findLeft(String input, int offset) {
        int size = input.length();
        Set<String> toSearches = parsers.keySet();
        for (int i = offset; i < size; i++) {
            int matched = StringHelper.startWith(input, i, toSearches);
            if (matched != -1) {
                return i;
            }
        }
        return -1;
    }

    public static class Block {
        private PairParser parser;
        private int left;
        private int right;

        public PairParser getParser() {
            return parser;
        }

        public int getLeft() {
            return left;
        }

        public int getLeftEnd() {
            return left + Objects.requireNonNull(parser.getLeft()).length();
        }

        public int getRight() {
            return right;
        }

        public int getRightEnd() {
            return right + Objects.requireNonNull(parser.getRight()).length();
        }

        public String getContent(String input, boolean unescape) {
            if (unescape) {
                return parser.unescape(input, left, right);
            } else {
                return input.substring(getLeftEnd(), right);
            }
        }
    }

    public class BlockIterator implements Iterator<Block> {

        private final String input;
        private Block block = null;
        private Block nextBlock = null;
        private boolean hasNext = true;

        public BlockIterator(String input) {
            this.input = input;
        }

        private boolean findNextBlock(boolean test) {
            if (!hasNext) {
                return false;
            }
            if (nextBlock != null) {
                if (!test) {
                    block = nextBlock;
                    nextBlock = null;
                }
                return true;
            }
            int size = input.length();
            String[] toSearches = getToSearches();
            int offset = block != null ? block.getRightEnd() : 0;
            for (int i = offset; i < size; i++) {
                int matched = StringHelper.startWith(input, i, toSearches);
                if (matched != -1) {
                    PairParser parser = parsers.get(toSearches[matched]);
                    int right = parser.findRight(input, i);
                    if (right != -1) {
                        if (test) {
                            nextBlock = new Block();
                            nextBlock.parser = parser;
                            nextBlock.left = i;
                            nextBlock.right = parser.findRight(input, i);
                        } else {
                            block = new Block();
                            block.parser = parser;
                            block.left = i;
                            block.right = parser.findRight(input, i);
                        }
                        return true;
                    }
                }
            }
            hasNext = false;
            return false;
        }

        @Override
        public boolean hasNext() {
            return findNextBlock(true);
        }

        @Override
        public Block next() {
            if (!findNextBlock(false)) {
                throw new NoSuchElementException();
            }
            return block;
        }
    }
}
