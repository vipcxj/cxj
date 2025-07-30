package com.cxj.utility;

import com.google.common.collect.AbstractIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 所有方法都不返回null，若输入不合法，直接抛异常。
 *
 * @author Administrator
 */
public class QualifiedNameHelper {

    public QualifiedNameHelper() {
        // TODO Auto-generated constructor stub
    }

    public static int comparator(String pathA, String pathB) {
        int levelA = QualifiedNameHelper.getPathLevel(pathA);
        int levelB = QualifiedNameHelper.getPathLevel(pathB);
        return levelA == levelB ? pathA.compareTo(pathB) : (levelA > levelB ? 1 : -1);
    }

    public static int anticomparator(String pathA, String pathB) {
        int ret = comparator(pathA, pathB);
        return ret == 0 ? ret : -ret;
    }

    /**
     * 分割路径，例如：<br>
     * splitPath("a.b.c", "a.b.c.d.e") == {"d", "e"}<br>
     * splitPath("a.b.c.d.e", "a.b.c.d.e") == {}<br>
     * splitPath("", "a.b.c.d.e") == {"a", "b", "c", "d", "e"}<br>
     *
     * @param from
     * @param path
     * @return
     */
    public static List<String> splitPath(String from, String path) {
        if (from != null && !from.isEmpty()) {
            assert (isChildPath(path, from));
            List<String> fromList = splitPath(from);
            List<String> pathList = splitPath(path);
            return pathList.subList(fromList.size(), pathList.size());
        } else {
            return splitPath(path);
        }
    }

    /**
     * 分割路径
     *
     * @param path
     * @return
     */
    public static List<String> splitPath(String path) {
        return Arrays.asList(path.split("\\."));
    }

    public static String combinePath(String... pathList) {
        return combinePath(Arrays.asList(pathList));
    }

    /**
     * 将节点序列组成路径
     *
     * @param pathList
     * @return
     */
    public static String combinePath(List<String> pathList) {
        assert (pathList != null && !pathList.isEmpty());
        return fullPath(pathList, pathList.size());
    }

    /**
     * 类似{@link #fullPath(java.lang.String, int) fullPath(String path, int index)}。
     *
     * @param pathList
     * @param index
     * @return
     */
    public static String fullPath(List<String> pathList, int index) {
        assert (index >= 0 && index <= pathList.size());
        if (index == 0) {
            return "";
        }
        return String.join(".", pathList.subList(0, index));
    }

    /**
     * getSubPath("a.b.c", 0) == "a"<br>
     * getSubPath("a.b.c", 1) == "b"<br>
     * getSubPath("a.b.c", 2) == "c"<br>
     *
     * @param path
     * @param index
     * @return
     */
    public static String getSubPath(String path, int index) {
        assert (index >= 0);
        int prePos = -1;
        int pos = -1;
        int idx = 0;
        do {
            prePos = pos + 1;
            pos = path.indexOf('.', prePos);
            idx++;
        } while (pos != -1 && idx <= index);
        return pos == -1 ? path.substring(prePos, path.length()) : path.substring(prePos, pos);
    }

    /**
     * 返回特定路径，例如：<br>
     * <code>
     * fullPath("a.b.c", 0) == "";<br>
     * fullPath("a.b.c", 1) == "a";<br>
     * fullPath("a.b.c", 2) == "a.b";<br>
     * fullPath("a.b.c", 3) == "a.b.c";<br>
     * </code>
     *
     * @param path
     * @param index
     * @return
     */
    public static String fullPath(String path, int index) {
        assert (index >= 0);
        if (index == 0) {
            return "";
        }
        int pos = -1;
        int idx = 0;
        do {
            pos = path.indexOf('.', pos + 1);
            idx++;
        } while (pos != -1 && idx < index);
        return pos == -1 ? path : path.substring(0, pos);
    }

    /**
     * 返回根路径，例如：<br>
     * <code>
     * getRootPath("a.b.c") == "a";
     * </code>
     *
     * @param path
     * @return
     */
    public static String getRootPath(String path) {
        assert (path != null && !path.isEmpty());
        int pos = path.indexOf('.');
        if (pos != -1) {
            return path.substring(0, pos);
        } else {
            return path;
        }
    }

    /**
     * 设置根路径，例如：<br>
     * <code>
     * setRootPath("a.b.c", "d") == "d.a.b";
     * </code>
     *
     * @param path
     * @param root
     * @return 修改后的路径
     */
    public static String setRootPath(String path, String root) {
        assert (path != null && !path.isEmpty());
        int pos = path.indexOf('.');
        if (pos != -1) {
            return root + path.substring(pos);
        } else {
            return root;
        }
    }

    /**
     * 返回叶父路径，例如：<br>
     * <code>
     * getParentPath("a.b.c") == "a.b";
     * getParentPath("a") == "";
     * </code>
     *
     * @param path
     * @return
     */
    public static String getParentPath(String path) {
        assert (path != null && !path.isEmpty());
        int pos = path.lastIndexOf('.');
        if (pos != -1) {
            return path.substring(0, pos);
        } else {
            return "";
        }
    }

    /**
     * 返回叶子路径，例如：<br>
     * <code>
     * getLeafPath("a.b.c") == "c";
     * </code>
     *
     * @param path
     * @return
     */
    public static String getLeafPath(String path) {
        assert (path != null && !path.isEmpty());
        int pos = path.lastIndexOf('.');
        if (pos != -1) {
            return path.substring(pos + 1, path.length());
        } else {
            return path;
        }
    }

    /**
     * 返回剩余路径，例如：<br>
     * <code>
     * getLeftPath("a.b.c", "a.b") == "c";
     * </code>
     *
     * @param path
     * @param parent
     * @return
     */
    public static String getLeftPath(String path, String parent) {
        if (parent.isEmpty()) {
            return path;
        }
        if (isChildPath(path, parent)) {
            if (Objects.equals(path, parent)) {
                return "";
            } else {
                return path.substring(parent.length() + 1);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 返回剩余路径，例如：<br>
     * <code>
     * getLeftPath("a.b.c", 0) == "a.b.c"; <br>
     * getLeftPath("a.b.c", 1) == "b.c"; <br>
     * getLeftPath("a.b.c", 2) == "c"; <br>
     * getLeftPath("a.b.c", 3) == ""; <br>
     * </code>
     * <code>getLeftPath("a.b.c", -1)</code>或<code>getLeftPath("a.b.c", 4)</code>抛IllegalArgumentException异常。
     *
     * @param path
     * @param index
     * @return
     */
    public static String getLeftPath(String path, int index) {
        if (index == 0) {
            return path;
        } else {
            int pos = -1;
            int idx = 0;
            do {
                pos = path.indexOf('.', pos + 1);
                idx++;
            } while (pos != -1 && idx < index);
            if (idx == index && pos != -1) {
                return path.substring(pos + 1);
            } else if (idx == index && pos == -1) {
                return "";
            } else {
                throw new IllegalArgumentException();
            }
        }

    }

    /**
     * 获取路径树，即本身和所有祖先路径的路径全名，顺序从根路径开始，例如： getPathTree("a.b.c") == {"a", "a.b",
     * "a.b.c"}
     *
     * @param path
     * @return
     */
    public static List<String> getPathTree(String path) {
        List<String> pList = new ArrayList<>();
        if (path != null && !path.isEmpty()) {
            int pos = -1;
            do {
                pos = path.indexOf('.', pos + 1);
                pList.add(path.substring(0, pos == -1 ? path.length() : pos));
            } while (pos != -1);
        }
        return pList;
    }

    /**
     * 判断是否子路径，例如：<br>
     * a.b.c就是a.b的子路径。<br>
     * 注：两者相等，同样返回ture, 若parent是空字符串，同样返回false。
     *
     * @param path 待判断的路径
     * @param parent 父路径
     * @return 是否子路径
     */
    public static boolean isChildPath(String path, String parent) {
        return path.matches(StringHelper.toRegex(parent) + "(\\.[^\\.\\s]+)*");
    }

    public static boolean isDirectChildPath(String path, String parent) {
        return getPathLevel(path) - getPathLevel(parent) == 1 && isChildPath(path, parent);
    }

    public static boolean isOneLayerPath(String path) {
        return path.indexOf('.') == -1;
    }

    /**
     * 判断路径级别，例如：<br>
     * a.b.c的级别为3，a.b的基本为2，若<code>path</code>为<code>null</code>或空字符串，则级别为0
     *
     * @param path
     * @return 级别
     */
    public static int getPathLevel(String path) {
        if (path == null || path.isEmpty()) {
            return 0;
        } else {
            int pos = -1;
            int idx = 0;
            do {
                pos = path.indexOf('.', pos + 1);
                ++idx;
            } while (pos != -1);
            return idx;
        }
    }

    public static String getMaxSharedAncestor(String pathL, String pathR) {
        if (pathL.isEmpty() || pathR.isEmpty()) {
            return "";
        } else {
            int posL = -1, posR = -1;
            String ancestor = "";
            do {
                posL = pathL.indexOf('.', posL + 1);
                posR = pathR.indexOf('.', posR + 1);
                String ancestorL = pathL.substring(0, posL == -1 ? pathL.length() : posL);
                if (ancestorL.equals(pathR.substring(0, posR == -1 ? pathR.length() : posR))) {
                    ancestor = ancestorL;
                } else {
                    break;
                }
            } while (posL != -1 && posR != -1);
            return ancestor;
        }
    }

    public static String getMaxSharedAncestor(List<String> pathes) {
        return pathes.stream().reduce((a, b) -> getMaxSharedAncestor(a, b)).get();
    }
    
    public static QNameIterator qNameIterator(String qName) {
        return new QNameIterator(qName);
    }

    public static class QNameIterator extends AbstractIterator<String> {

        private final String qName;
        private final int level;
        private int idx;

        QNameIterator(String qName) {
            this.qName = qName;
            this.level = getPathLevel(qName);
            this.idx = 0;
        }

        @Override
        protected String computeNext() {
            if (idx < level) {
                return getSubPath(qName, idx++);
            } else {
                return endOfData();
            }
        }

    }
}
