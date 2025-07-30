/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import com.cxj.error.Assert;
import com.google.common.base.Strings;
import com.google.common.collect.AbstractIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * @author Administrator
 */
public class PathNameHelper {

    /**
     * 计算两个路径的差别，比如：<br>
     * calcDifference("/a/b", "/a/b") == ""<br>
     * calcDifference("/a/b/c", "/a/b") == "c"<br>
     * calcDifference("/a/b/c", "/a/b/c/d") == ".."<br>
     * calcDifference("/a/b/c", "/a/b/d") == "../c"<br>
     * calcDifference("/a/./b", "/a/c/../b") == ""<br>
     * calcDifference("/a/c/../b/c", "/a/./b") == "c"<br>
     * calcDifference("/a/c/../b/c", "/a/./f/../b/c/d/.") == ".."<br>
     * calcDifference("/a/c/../b/c", "/a/c/../b/d") == "../c"<br>
     *
     * @param refPath 必须绝对路径
     * @param targetPath 必须绝对路径
     * @return
     */
    public static String calcDifference(String refPath, String targetPath) {
        refPath = standardAbsolutePath(refPath);
        targetPath = standardAbsolutePath(targetPath);
        if (Objects.equals(refPath, targetPath)) {
            return "";
        }
        String commonPath = Strings.commonPrefix(refPath, targetPath);
        String refLeft = refPath.substring(commonPath.length());
        if (refLeft.startsWith(getRootWildcard())) {
            refLeft = refLeft.substring(1);
        }
        String targetLeft = targetPath.substring(commonPath.length());
        if (targetLeft.startsWith(getRootWildcard())) {
            targetLeft = targetLeft.substring(1);
        }
        if (targetLeft.isEmpty()) {
            return refLeft;
        }
        StringBuilder dif = new StringBuilder();
        int from = -1;
        do {
            from = targetLeft.indexOf('/', from + 1);
            dif.append("/..");
        } while (from != -1);
        if (refLeft.isEmpty()) {
            return dif.substring(1);
        } else {
            return dif.append("/").append(refLeft).substring(1);
        }
    }

    /**
     * 判断是否子路径，例如：<br>
     * /a/b/c就是/a/b的子路径。<br>
     * 注：两者相等，同样返回ture, 若parent是空字符串，同样返回false。
     *
     * @param path 待判断的路径
     * @param parent 父路径
     * @return 是否子路径
     */
    public static boolean isChildPath(String path, String parent) {
        if (isRootWildcard(parent)) {
            return path.matches("(/[^/^.]+)*");
        } else {
            return path.matches(StringHelper.toRegex(parent) + "(/[^/^.]+)*");
        }
    }

    public static boolean isDirectChildPath(String path, String parent) {
        if (isRootWildcard(parent)) {
            return path.matches("/[^/^.]+");
        } else {
            return path.matches(StringHelper.toRegex(parent) + "/[^/^.]+");
        }
    }

    public static boolean isAbsolute(String path) {
        return path.startsWith("/");
    }

    public static String removeDotPath(String path) {
        while (path.contains("/./")) {
            path = path.replaceAll("/\\./", "/");
        }
        path = path.replaceAll("([\\s\\S])/\\.$", "$1");
        path = path.replaceAll("^/\\.$", "/");
        return path;
    }

    public static String leafPath(String path) {
        assert (path != null && !path.isEmpty());
        int pos = path.lastIndexOf('/');
        if (pos != -1) {
            return path.substring(pos + 1, path.length());
        } else {
            return path;
        }
    }

    public static String parentAbsolutePath(String path) {
        return standardAbsolutePath(combine(path, getParentWildcard()));
    }

    public static String standardAbsolutePath(String path) {
        Assert.isTrue(isAbsolute(path));
        path = removeDotPath(path);
        PathIterator it = new PathIterator(path);
        while (it.hasNext()) {
            String node = it.next();
            if (isParentWildcard(node)) {
                it.remove();
                if (it.hasPrevious()) {
                    String pt = it.previous();
                    if (!isRootWildcard(pt)) {
                        it.remove();
                    }
                }
            }
        }
        return it.getContent();
    }

    public static String combine(String base, String path) {
        return combine(base, path, false);
    }

    public static String combine(String base, String path, boolean toStandard) {
        Assert.isTrue(!path.startsWith("/"), "The path must be relative path!");
        if (toStandard) {
            Assert.isTrue(isAbsolute(base), "Only absolute path support standardization! So base path must be absolute path.");
        }
        if (base.endsWith("/")) {
            return toStandard ? standardAbsolutePath(base + path) : base + path;
        } else {
            return toStandard ? standardAbsolutePath(base + "/" + path) : base + "/" + path;
        }
    }

    public static String toStandardPath(String path) {
        assert (path != null);
        if (path.isEmpty()) {
            return getCurrentWildcard();
        } else if (path.startsWith("/")) {
            return path;
        } else if (path.startsWith("..")) {
            return "./" + path;
        } else if (path.startsWith(".")) {
            return path;
        } else {
            return getCurrentWildcard() + "/" + path;
        }
    }

    /**
     * 获取除根外的路径树，即本身和除根外所有祖先路径的路径全名，顺序从顶层路径开始，例如： getPathTree("/a/b/c") ==
     * {"/a", "/a/b", "/a/b/c"}
     *
     * @param path
     * @return
     */
    public static List<String> getPathTreeWithoutRoot(String path) {
        path = toStandardPath(path);
        List<String> pList = new ArrayList<>();
        if (path != null && !path.isEmpty()) {
            int pos = path.indexOf('/', 0);
            if (pos == -1) {
                return pList;
            }
            do {
                pos = path.indexOf('/', pos + 1);
                pList.add(path.substring(0, pos == -1 ? path.length() : pos));
            } while (pos != -1);
        }
        return pList;
    }

    /**
     * 获取完整路径树，即本身和所有祖先路径的路径全名，顺序从根路径开始，例如： getPathTree("/a/b/c") == {"/", "/a",
     * "/a/b", "/a/b/c"}
     *
     * @param path
     * @return
     */
    public static List<String> getPathTreeWithRoot(String path) {
        path = toStandardPath(path);
        List<String> pList = new ArrayList<>();
        pList.add(getRootPath(path));
        pList.addAll(getPathTreeWithoutRoot(path));
        return pList;
    }

    public static boolean isRootWildcard(String path) {
        return Objects.equals(path, "/");
    }

    public static boolean isCurrentWildcard(String path) {
        return Objects.equals(path, ".");
    }

    public static boolean isParentWildcard(String path) {
        return Objects.equals(path, "..");
    }

    public static String getRootWildcard() {
        return "/";
    }

    public static String getCurrentWildcard() {
        return ".";
    }

    public static String getParentWildcard() {
        return "..";
    }

    /**
     * 返回根路径，例如：<br>
     * <code>
     * getRootPath("a/b/c") == ".";
     * getRootPath("/a/b/c") == "/";
     * getRootPath("./a/b/c") == ".";
     * getRootPath("../a/b/c") == ".";
     * </code>
     *
     * @param path
     * @return
     */
    public static String getRootPath(String path) {
        assert (path != null);
        if (path.isEmpty()) {
            return getCurrentWildcard();
        } else if (path.startsWith("/")) {
            return "/";
        } else if (path.startsWith("..")) {
            return ".";
        } else if (path.startsWith(".")) {
            return ".";
        } else {
            return ".";
        }
    }

    /**
     * 分割路径 plitPath("/a/b/c") == {"/","a", "b", "c"}; plitPath("a/b/c") ==
     * {".", "a", "b", "c"}; plitPath("./a/b/c") == {".", "a", "b", "c"};
     *
     * @param path
     * @return
     */
    public static List<String> splitPath(String path) {
        List<String> pList = new ArrayList<>();
        if (isAbsolute(path)) {
            pList.add("/");
            List<String> asList = Arrays.asList(toStandardPath(path).split("/"));
            if (asList.size() > 1) {
                pList.addAll(asList.subList(1, asList.size()));
            }
        } else {
            pList.addAll(Arrays.asList(toStandardPath(path).split("/")));
        }
        return pList;
    }

    /**
     * 以深度优先算法遍历一路径集合。输入的路径集合要求必须来自一颗树，有唯一的共同根路径，此遍历方式保证所有父路径先于子路径被访问
     */
    public static class PathSetWalker extends AbstractIterator<String> implements Iterator<String> {

        private final Set<String> pathSet;
        private final String root;
        private Iterator<PathSetWalker> subIterator = null;
        private PathSetWalker currentWalker = null;

        public PathSetWalker(Set<String> pathSet) {
            this.pathSet = new HashSet<>(pathSet);
            this.root = Collections.min(pathSet, (a1, a2) -> {
                return Integer.compare(a1.length(), a2.length());
            });
            check();
        }

        private void check() {
            if (pathSet.isEmpty()) {
                throw new IllegalArgumentException("The input path set must not be empty!");
            }
            if (root.isEmpty()) {
                throw new IllegalArgumentException("The input path set is not valid! Root path must not be empty!");
            }
            if (!pathSet.stream().allMatch(path -> isChildPath(path, root))) {
                throw new IllegalArgumentException("The input path set is not valid! All the path must have the same root!");
            }
        }

        private void computeChildren() {
            Set<String> children = pathSet.stream().filter(path -> isDirectChildPath(path, root)).collect(Collectors.toCollection(() -> new TreeSet<>()));
            if (children.isEmpty()) {
                subIterator = Collections.EMPTY_LIST.iterator();
            } else {
                List<PathSetWalker> childrenWalker = new ArrayList<>();
                children.stream().forEach((child) -> {
                    childrenWalker.add(new PathSetWalker(pathSet.stream().filter(path -> isChildPath(path, child)).collect(Collectors.toSet())));
                });
                subIterator = childrenWalker.iterator();
            }
        }

        @Override
        protected String computeNext() {
            if (subIterator == null) {
                computeChildren();
                return root;
            } else {
                if (currentWalker == null) {
                    if (subIterator.hasNext()) {
                        currentWalker = subIterator.next();
                        return computeNext();
                    } else {
                        return endOfData();
                    }
                } else {
                    if (currentWalker.hasNext()) {
                        return currentWalker.next();
                    } else {
                        currentWalker = null;
                        return computeNext();
                    }
                }
            }
        }

    }

    public static class PathIterator implements ListIterator<String> {

        private String content;
        private final List<String> nodes;
        private boolean valid;
        private int cursor;
        private int lastRet;

        public PathIterator(String content) {
            this.content = content;
            this.nodes = splitPath(content);
            this.valid = true;
            this.cursor = 0;
            this.lastRet = -1;
        }

        public String getContent() {
            if (!valid) {
                content = calcContent(0);
                valid = true;
            }
            return content;
        }

        public String leftPath() {
            return calcContent(cursor);
        }

        private String calcContent(int idx) {
            if (idx < nodes.size()) {
                if (Objects.equals(nodes.get(idx), "/")) {
                    return nodes.size() - idx == 1 ? nodes.get(idx) : "/" + String.join("/", nodes.subList(idx + 1, nodes.size()));
                } else {
                    return String.join("/", nodes.subList(idx, nodes.size()));
                }
            } else {
                return "";
            }
        }

        @Override
        public boolean hasNext() {
            return cursor != nodes.size();
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public String next() {
            try {
                int i = cursor;
                String next = nodes.get(i);
                lastRet = i;
                cursor = i + 1;
                return next;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        @Override
        public String previous() {
            try {
                int i = cursor - 1;
                String previous = nodes.get(i);
                lastRet = cursor = i;
                return previous;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            nodes.remove(lastRet);
            if (lastRet < cursor) {
                cursor--;
            }
            lastRet = -1;
            valid = false;
        }

        @Override
        public void set(String e) {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            nodes.set(lastRet, e);
            valid = false;
        }

        @Override
        public void add(String e) {
            int i = cursor;
            nodes.add(i, e);
            lastRet = -1;
            cursor = i + 1;
            valid = false;
        }

    }
}
