package com.cxj.utility;

import com.cxj.bean.Utils;
import com.cxj.functional.TriFunction;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * 类型转换工具类
 *
 * @author Administrator
 */
@SuppressWarnings({"WeakerAccess", "unchecked", "SimplifiableIfStatement"})
public class TypeUtils {

    private static List<TypeCaster> CASTERS;

    static {
        CASTERS = new ArrayList<>();
        ServiceLoader<TypeCaster> loaders = ServiceLoader.load(TypeCaster.class);
        for (TypeCaster caster : loaders) {
            CASTERS.add(caster);
        }
        CASTERS.sort((o1, o2) -> {
            int res = MatchHelper.compareNull(o1.getOutputType(), o2.getOutputType());
            if (res != MatchHelper.COMPARE_RESULT_UNDEFINE) {
                return -res;
            }
            if (o1.getOutputType().isAssignableFrom(o2.getOutputType())) {
                return 1;
            }
            if (o2.getOutputType().isAssignableFrom(o1.getOutputType())) {
                return -1;
            }
            if (Objects.equals(o1.getOutputType(), o2.getOutputType())) {
                if (o1.getInputType() == null && o2.getInputType() == null) {
                    return 0;
                }
                if (o1.getInputType() == null) {
                    return 1;
                }
                if (o2.getInputType() == null) {
                    return -1;
                }
                if (o1.getInputType().isAssignableFrom(o2.getInputType())) {
                    return 1;
                }
                if (o2.getInputType().isAssignableFrom(o1.getInputType())) {
                    return -1;
                }
            }
            return 0;
        });
    }

    private final static String[] DATE_FORMAT_DATE = new String[]{
            "MM/dd",
            "yyyy/MM/dd",
            "yy/MM/dd",
            "MM-dd",
            "yyyy-MM-dd",
            "yy-MM-dd",
            "MM'月'dd'日'",
            "yyyy'年'MM'月'dd'日'",
            "yy'年'MM'月'dd'日'",
            "MM','dd",
            "yyyy','MM','dd",
            "yy','MM','dd",
            "MM'.'dd",
            "yyyy'.'MM'.'dd",
            "yy'.'MM'.'dd",};
    private final static String[] DATE_FORMAT_TIME = new String[]{
            "HH:mm",
            "hh:mm a",
            "HH:mm:ss",
            "hh:mm:ss a",
            "HH:mm:ss.SSS",
            "hh:mm:ss.SSS a",
            "HH'时'mm'分'",
            "hh'时'mm'分' a",
            "HH'时'mm'分'ss'秒'",
            "hh'时'mm'分'ss'秒' a",
            "HH'时'mm'分'ss'秒'SSS'毫秒'",
            "hh'时'mm'分'ss'秒'SSS'毫秒' a",
            "HH'点'mm'分'",
            "hh'点'mm'分' a",
            "HH'点'mm'分'ss'秒'",
            "hh'点'mm'分'ss'秒' a",
            "HH'点'mm'分'ss'秒'SSS'毫秒'",
            "hh'点'mm'分'ss'秒'SSS'毫秒' a"};
    private final static String[] DATE_FORMAT_CONNECT = new String[]{
            " ",
            "'T'"
    };

    /**
     * 判断value是否为空;<br>
     * 如果value是Boolean类型,则返回false;<br>
     * 如果value是Number类型,则返回false;<br>
     * 如果value是null,则返回true;<br>
     * 如果value是CharSequence类型,长度大于0,则返回false,否则返回true;<br>
     * 如果value是Array,长度不为0,则返回false,否则返回true;<br>
     * 如果value是Enumeration,包含更多的元素,则返回false;<br>
     * 如果value是Iterable,如果还有元素,则返回flase,否则返回true;<br>
     * 如果value是Map且不为空,则返回false,否则返回true;<br>
     * 如果以上情况都不满足返回false;
     *
     * @param value
     * 支持的参数类型有：null/Boolean/Number/CharSequence/Object[]/Enumeration/Iterable/Iterator/Map;
     * @return 返回结果,若转换不了则返回null;
     *
     */
    public static boolean isEmpty(Object value) {
        return !(value instanceof Boolean) && !(value instanceof Number) && !test(value);
    }

    public static <T> List<T> toTypedList(@Nullable Object iterable, @Nonnull final Class<T> type) {
        return toTypedList(iterable, type, null);
    }

    /**
     * 将iterable转化为元素类型为type的list;<br>
     * 如果iterable为null,则返回空的list;<br>
     * 内部用{@link #toTypeOrNull(java.lang.Object, java.lang.Class)}
     * 来转换list的所有元素，转换不了的元素为null;<br>
     * 若iterable不支持轮询，则返回一个空的list;
     * (此方法返回一个新的list);
     *
     * @see #toTypeOrNull(java.lang.Object, java.lang.Class)
     * @param <T> 要转换的目标元素类型;
     * @param iterable 要转换的容器对象;
     * @param type 要转换的目标元素类型;
     * @return 返回结果;
     */
    @Nonnull
    public static <T> List<T> toTypedList(@Nullable Object iterable, @Nonnull final Class<T> type, final BiFunction<Object, Class, Object> customToType) {
        if (iterable == null) {
            return Collections.emptyList();
        }
        final List<T> list = new ArrayList<>();
        iterateByIndex(iterable, (v, i) -> list.add(toTypeOrNull(v, type, customToType)));
        return list;
    }

    @Nonnull
    public static <T> Set<T> toTypedSet(@Nullable Object iterable, @Nonnull final Class<T> type) {
        return toTypedSet(iterable, type, null);
    }

    @Nonnull
    public static <T> Set<T> toTypedSet(@Nullable Object iterable, @Nonnull final Class<T> type, final BiFunction<Object, Class, Object> customToType) {
        if (iterable == null) {
            return Collections.emptySet();
        }
        final Set<T> set = new HashSet<>();
        iterateByIndex(iterable, (v, i) -> set.add(toTypeOrNull(v, type, customToType)));
        return set;
    }

    public static <T> T[] toTypedArray(@Nullable Object iterable, @Nonnull final Class<T> type) {
        if (iterable == null) {
            return (T[]) new Object[0];
        }
        int size = getSize(iterable);
        if (size >= 0) {
            final T[] array = (T[]) Array.newInstance(type, size);
            iterateByIndex(iterable, (o, idx) -> array[idx] = toTypeOrNull(o, type));
            return array;
        } else {
            List<T> list = toTypedList(iterable, type);
            return (T[]) list.toArray();
        }
    }

    /**
     * 将inputArray转化为元素类型为type类型的Array;<br>
     * 如果inputArray为null,则返回null;<br>
     * 内部用{@link #toTypeOrNull(java.lang.Object, java.lang.Class)
     * }来转换inputArray的所有元素，转换不了的元素为null;
     *(没有修改inputArray)
     * @see #toTypeOrNull(java.lang.Object, java.lang.Class)
     * @param <E> 待转换数组的元素类型
     * @param <T> 目标类型
     * @param inputArray 数组
     * @param type 待转换数组的元素类型
     * @return 转换结果
     */
    public static <E, T> T[] toTypedArray(@Nullable E[] inputArray, @Nonnull Class<T> type) {
        if (inputArray == null) {
            return null;
        }
        int size = inputArray.length;
        T[] array = (T[]) new Object[size];
        for (int i = 0; i < size; i++) {
            array[i] = toTypeOrNull(inputArray[i], type);
        }
        return array;
    }

    /**
     * 将Collection转化为元素类型为Type类型的array;<br>
     * 如果collection为null,则返回null;<br>
     * 内部用{@link #toTypeOrNull(java.lang.Object, java.lang.Class)}来转换collection的所有元素，转换不了的元素为null;
     * @see #toTypeOrNull(java.lang.Object, java.lang.Class)
     * @param <T> 待转换数组的元素类型
     * @param collection 集合
     * @param type 待转换数组的元素类型
     * @return 转换结果
     */
    public static <T> T[] toTypedArray(@Nullable Collection collection, @Nonnull Class<T> type) {
        if (collection == null) {
            return null;
        }
        int size = collection.size();
        T[] array = (T[]) new Object[size];
        int i = 0;
        for (Object element : collection) {
            array[i++] = toTypeOrNull(element, type);
        }
        return array;
    }

    public static <T> T toType(@Nullable Object value, @Nonnull Class<T> type) {
        return toType(value, type, null);
    }

    /**
     * 转换为指定类型;<br>
     * 如果转换失败则抛出IllegalArgumentException异常;
     *
     * @see #toTypeOrNull(java.lang.Object, java.lang.Class)
     * @param <T> 要转换的目标类型
     * @param value 对象
     * @param type 要转换的目标类型
     * @return 转换结果
     * @throws IllegalArgumentException 无法转换时抛出此异常
     */
    public static <T> T toType(@Nullable Object value, @Nonnull Class<T> type, BiFunction<Object, Class, Object> customToType) {
        T result = toTypeOrNull(value, type, customToType);
        if (!isSuccess(value, result)) {
            throw new IllegalArgumentException("Unable to transform the value from type " + value.getClass() + " to type " + type);
        }
        return result;
    }

    public static boolean isSuccess(Object value, Object result) {
        if (result == null && value != null) {
            return value instanceof CharSequence && isNullString((CharSequence) value);
        } else {
            return true;
        }
    }

    public static <T> T toTypeOrNull(@Nullable Object value, @Nonnull Class<T> type) {
        return toTypeOrNull(value, type, null);
    }

    /**
     * 转换为type指定类型;<br>
     * 内部使用以下方法转换:<br> {@link #toBoolean(java.lang.Object)};<br>
     * {@link #toLongOrNull(java.lang.Object) };
     * <br>{@link #toIntegerOrNull(java.lang.Object) };<br>
     * {@link #toDoubleOrNull(java.lang.Object) };<br>
     * {@link #toFloatOrNull(java.lang.Object) };<br>
     * {@link #toDateOrNull(java.lang.Object) };<br>
     * {@link #toCalendarOrNull(java.lang.Object) };<br>
     * {@link #toBigDecimalOrNull(java.lang.Object) };<br>
     * {@link #toBigInteger(java.lang.Object) };<br>
     * {@link #toString(java.lang.Object) };<br>
     * {@link #toByteOrNull(java.lang.Object) };<br>
     * {@link #toShortOrNull(java.lang.Object) }; <br>
     * {@link #toCharOrNull(java.lang.Object) };
     *
     * @see #toBoolean(java.lang.Object)
     * @see #toLongOrNull(java.lang.Object)
     * @see #toIntegerOrNull(java.lang.Object)
     * @see #toDoubleOrNull(java.lang.Object)
     * @see #toFloatOrNull(java.lang.Object)
     * @see #toDateOrNull(java.lang.Object)
     * @see #toCalendarOrNull(java.lang.Object)
     * @see #toBigDecimalOrNull(java.lang.Object)
     * @see #toBigInteger(java.lang.Object)
     * @see #toString(java.lang.Object)
     * @see #toByteOrNull(java.lang.Object)
     * @see #toShortOrNull(java.lang.Object)
     * @see #toCharOrNull(java.lang.Object)
     * @param <T> 要转换的目标类型
     * @param value 对象
     * @param type 要转换的目标类型
     * 参数支持Boolean类型/Long类型/Integer类型/Double类型/Float类型/Date类型/Calendar类型/BigDecimal类型或空/String类型/Byte类型/Character类型
     * @return 转换结果，当不能转化时返回null
     */
    public static <T> T toTypeOrNull(@Nullable Object value, @Nonnull Class<T> type, BiFunction<Object, Class, Object> customToType) {
        if (customToType != null) {
            Object result = customToType.apply(value, type);
            if (isSuccess(value, result)) {
                return (T) result;
            }
        }
        if (value == null) {
            return null;
        }
        if (type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        if (CASTERS != null && !CASTERS.isEmpty()) {
            for (TypeCaster caster : CASTERS) {
                if (type.isAssignableFrom(caster.getOutputType()) && (caster.getInputType() == null || caster.getInputType().isAssignableFrom(value.getClass()))) {
                    Object output = caster.cast(value);
                    if (output != null || caster.isTerminal()) {
                        return (T) output;
                    }
                }
            }
        }
        if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            return (T) (Boolean) toBoolean(value);
        } else if (Long.class.equals(type) || long.class.equals(type)) {
            return (T) toLongOrNull(value);
        } else if (Integer.class.equals(type) || int.class.equals(type)) {
            return (T) toIntegerOrNull(value);
        } else if (Double.class.equals(type) || double.class.equals(type)) {
            return (T) toDoubleOrNull(value);
        } else if (Float.class.equals(type) || float.class.equals(type)) {
            return (T) toFloatOrNull(value);
        } else if (Number.class.equals(type)) {
            if (value instanceof Boolean) {
                return (T) (Integer) (((Boolean) value) ? 1 : 0);
            } else if (value instanceof Date || value instanceof Calendar) {
                return (T) toLongOrNull(value);
            } else {
                return (T) toDoubleOrNull(value);
            }
        } else if (Date.class.equals(type)) {
            return (T) toDateOrNull(value);
        } else if (java.sql.Date.class.equals(type)) {
            Date date = toDateOrNull(value);
            return (T) (date != null ? new java.sql.Date(date.getTime()) : null);
        } else if (java.sql.Time.class.equals(type)) {
            Date date = toDateOrNull(value);
            return (T) (date != null ? new java.sql.Time(date.getTime()) : null);
        } else if (java.sql.Timestamp.class.equals(type)) {
            Date date = toDateOrNull(value);
            return (T) (date != null ? new java.sql.Timestamp(date.getTime()) : null);
        } else if (Calendar.class.equals(type)) {
            return (T) toCalendarOrNull(value);
        } else if (BigDecimal.class.equals(type)) {
            return (T) toBigDecimalOrNull(value);
        } else if (BigInteger.class.equals(type)) {
            return (T) toBigInteger(value);
        } else if (String.class.equals(type)) {
            return (T) toString(value);
        } else if (Byte.class.equals(type) || byte.class.equals(type)) {
            return (T) toByteOrNull(value);
        } else if (Short.class.equals(type) || short.class.equals(type)) {
            return (T) toShortOrNull(value);
        } else if (Character.class.equals(type) || char.class.equals(type)) {
            return (T) toCharOrNull(value);
        } else if (byte[].class.equals(type)) {
            return (T) toByteArrayOrNull(value);
        } else if (Byte[].class.equals(type)) {
            return (T) toObjectByteArrayOrNull(value);
        } else if (value instanceof Map) {
            return toObjectOrNull((Map<String, Object>) value, type);
        } else {
            return null;
        }
    }

    /**
     * 如果对象不为空,用{@link Object#toString() }方法转换为字符串;<br>
     * 否则返回null;
     *
     * @param value 要转换的对象
     * @return 返回结果，若转化不了则返回null
     */
    public static String toString(Object value) {
        return value != null ? value.toString() : null;
    }

    private static int subStringNum(String content, int offset, int subString) {
        int index = content.indexOf(subString, offset);
        int num = 0;
        while (index != -1) {
            num++;
            index = content.indexOf(subString, index + 1);
        }
        return num;
    }

    private static String decideDateConnect(String stObj) {
        //System.out.println(DATE_FORMAT_CONNECT[0]);
        return stObj.indexOf('T') != -1 ? DATE_FORMAT_CONNECT[1] : DATE_FORMAT_CONNECT[0];
    }

    private static String decideDateFormat(String strObj) {
        int index;
        index = strObj.indexOf('/');
        if (index != -1) {
            if (index == 2 || index == 1) {
                int flagNum = subStringNum(strObj, index, '/');
                if (flagNum == 1) {
                    return DATE_FORMAT_DATE[0];
                } else if (flagNum == 2) {
                    return DATE_FORMAT_DATE[2];
                } else {
                    return null;
                }
            } else if (index == 4) {
                return DATE_FORMAT_DATE[1];
            } else {
                return null;
            }
        }
        index = strObj.indexOf('.');
        if (index != -1 && index <= 7) {
            if (index == 2 || index == 1) {
                int flagNum = subStringNum(strObj, index, '.');
                if (flagNum == 1) {
                    return DATE_FORMAT_DATE[12];
                } else if (flagNum == 2) {
                    if (strObj.lastIndexOf('.') > 7) {
                        return DATE_FORMAT_DATE[12];
                    } else {
                        return DATE_FORMAT_DATE[14];
                    }
                } else {
                    return null;
                }
            } else if (index == 4) {
                return DATE_FORMAT_DATE[13];
            } else {
                return null;
            }
        }
        index = strObj.indexOf('-');
        if (index != -1) {
            if (index == 2 || index == 1) {
                int flagNum = subStringNum(strObj, index, '-');
                if (flagNum == 1) {
                    return DATE_FORMAT_DATE[3];
                } else if (flagNum == 2) {
                    return DATE_FORMAT_DATE[5];
                } else {
                    return null;
                }
            } else if (index == 4) {
                return DATE_FORMAT_DATE[4];
            } else {
                return null;
            }
        }
        index = strObj.indexOf('年');
        if (index != -1) {
            if (index == 2) {
                return DATE_FORMAT_DATE[8];
            } else if (index == 4) {
                return DATE_FORMAT_DATE[7];
            } else {
                return null;
            }
        }
        index = strObj.indexOf('月');
        if (index != -1) {
            return DATE_FORMAT_DATE[6];
        }
        index = strObj.indexOf(',');
        if (index != -1) {
            if (index == 2 || index == 1) {
                int flagNum = subStringNum(strObj, index, ',');
                if (flagNum == 1) {
                    return DATE_FORMAT_DATE[9];
                } else if (flagNum == 2) {
                    return DATE_FORMAT_DATE[11];
                } else {
                    return null;
                }
            } else if (index == 4) {
                return DATE_FORMAT_DATE[10];
            } else {
                return null;
            }
        }
        return null;
    }

    private static String decideTimeFormat(String strObj) {
        int index;
        if ((index = strObj.indexOf(':')) != -1) {
            int flagNum = subStringNum(strObj, index, ':');
            if (flagNum == 2) {
                if (strObj.indexOf('.') != -1 && strObj.lastIndexOf('.') > 8) {
                    if (strObj.indexOf('m') != -1 || strObj.indexOf('M') != -1 || strObj.indexOf('午') != -1) {
                        return DATE_FORMAT_TIME[5];
                    } else {
                        return DATE_FORMAT_TIME[4];
                    }
                } else {
                    if (strObj.indexOf('m') != -1 || strObj.indexOf('M') != -1 || strObj.indexOf('午') != -1) {
                        return DATE_FORMAT_TIME[3];
                    } else {
                        return DATE_FORMAT_TIME[2];
                    }
                }
            } else {
                if (strObj.indexOf('m') != -1 || strObj.indexOf('M') != -1 || strObj.indexOf('午') != -1) {
                    return DATE_FORMAT_TIME[1];
                } else {
                    return DATE_FORMAT_TIME[0];
                }
            }
        } else if (strObj.indexOf('时') != -1) {
            if (strObj.indexOf('秒') != -1) {
                if (strObj.indexOf('毫') != -1) {
                    if (strObj.indexOf('m') != -1 || strObj.indexOf('M') != -1 || strObj.indexOf('午') != -1) {
                        return DATE_FORMAT_TIME[11];
                    } else {
                        return DATE_FORMAT_TIME[10];
                    }
                } else {
                    if (strObj.indexOf('m') != -1 || strObj.indexOf('M') != -1 || strObj.indexOf('午') != -1) {
                        return DATE_FORMAT_TIME[9];
                    } else {
                        return DATE_FORMAT_TIME[8];
                    }
                }
            } else {
                if (strObj.indexOf('m') != -1 || strObj.indexOf('M') != -1 || strObj.indexOf('午') != -1) {
                    return DATE_FORMAT_TIME[7];
                } else {
                    return DATE_FORMAT_TIME[6];
                }
            }
        } else if (strObj.indexOf('点') != -1) {
            if (strObj.indexOf('秒') != -1) {
                if (strObj.indexOf('毫') != -1) {
                    if (strObj.indexOf('m') != -1 || strObj.indexOf('M') != -1 || strObj.indexOf('午') != -1) {
                        return DATE_FORMAT_TIME[17];
                    } else {
                        return DATE_FORMAT_TIME[16];
                    }
                } else {
                    if (strObj.indexOf('m') != -1 || strObj.indexOf('M') != -1 || strObj.indexOf('午') != -1) {
                        return DATE_FORMAT_TIME[15];
                    } else {
                        return DATE_FORMAT_TIME[14];
                    }
                }
            } else {
                if (strObj.indexOf('m') != -1 || strObj.indexOf('M') != -1 || strObj.indexOf('午') != -1) {
                    return DATE_FORMAT_TIME[13];
                } else {
                    return DATE_FORMAT_TIME[12];
                }
            }
        }
        return null;
    }

    private static Locale decideLocale(String strObj) {
        if (strObj.indexOf('午') != -1) {
            return Locale.CHINA;
        } else {
            return Locale.ENGLISH;
        }
    }

    private static String makeFormat(String formatDate, String formatTime, String formatConnect) {
        StringBuilder sb = new StringBuilder();
        if (formatDate != null && formatTime != null) {
            if (formatConnect != null) {
                sb.append(formatDate).append(formatConnect).append(formatTime);
            } else {
                sb.append(formatDate).append(formatTime);
            }
            return sb.toString();
        } else if (formatDate != null) {
            return formatDate;
        } else {
            return formatTime;
        }
    }

    /**
     * 把表示日期/时间的不同方式转化为Date类型;<br>
     * 如果obj是null,则返回null;<br>
     * 如果obj是Date类型,则返回本身;<br>
     * 如果obj是Calendar类型,则返回的是从1970年1月1日午夜（GMT 时间）至现在所经过的 UTC 毫秒数;<br>
     * 如果obj是CharSequence类型,支持将以下日期样式、连接符样式以及时间样式进行排列组合后转换为date类型（例如：<br>
     * "MM/ddHH:mm"、"MM/ddTHH:mm"、"MM/dd HH:mm a"、"MM/dd HH:mm:ss"、"MM/dd
     * HH:mm:ss.SSS"）<br>
     * 日期样式：<br>
     * "MM/dd";"yyyy/MM/dd";"yy/MM/dd";"MM-dd";"yyyy-MM-dd";"yy-MM-dd";"MM'月'dd'日'";"yyyy'年'MM'月'dd'日'";<br>
     * "yy'年'MM'月'dd'日'";"MM','dd";"yyyy','MM','dd";"yy','MM','dd";"MM'.'dd";"yyyy'.'MM'.'dd";"yy'.'MM'.'dd";<br>
     *
     * 连接符样式： " ","'T'";<br>
     *
     * 时间样式：<br>
     * "HH:mm", "hh:mm a", "HH:mm:ss", "hh:mm:ss a", "HH:mm:ss.SSS";"hh:mm:ss.SSS a", "HH'时'mm'分'", "hh'时'mm'分' a";<br>
     * "HH'时'mm'分'ss'秒'","hh'时'mm'分'ss'秒' a", "HH'时'mm'分'ss'秒'SSS'毫秒'", "hh'时'mm'分'ss'秒'SSS'毫秒' a","HH'点'mm'分'";<br>
     * "hh'点'mm'分' a","HH'点'mm'分'ss'秒'","hh'点'mm'分'ss'秒' a","HH'点'mm'分'ss'秒'SSS'毫秒'", "hh'点'mm'分'ss'秒'SSS'毫秒' a";<br>
     * 如果obj是Number,转换为长整型精度之后,返回Date类型;<br>
     * 如果以上情况都不满足,则返回null;
     *
     * @param obj null/Date/Calendar/CharSequence/Number
     * @return 返回Date类型，若转化不了则返回null
     */
    public static Date toDateOrNull(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date) obj;
        }
        if (obj instanceof Calendar) {
            return ((Calendar) obj).getTime();
        }
        if (obj instanceof CharSequence) {
            if (isNullString((CharSequence) obj)) {
                return null;
            }
            String strObj = obj.toString().trim();
            if (NumberUtils.isCreatable((strObj))) {
                Long mm = NumberUtils.createLong(strObj);
                return mm != null ? new Date(mm) : null;
            }
            String formatDate, formatTime, formatConnect;
            formatDate = decideDateFormat(strObj);
            formatTime = decideTimeFormat(strObj);
            formatConnect = decideDateConnect(strObj);
            String format = makeFormat(formatDate, formatTime, formatConnect);
            if (format != null) {
                try {
                    return new SimpleDateFormatEx(format, decideLocale(strObj)).parse(strObj);
                } catch (ParseException ex) {
                    return null;
                }
            }
            if (org.apache.commons.lang3.StringUtils.isNumeric(strObj)) {
                return new Date(Long.parseLong(strObj));
            }
            return null;
        }
        if (obj instanceof Number) {
            return new Date(((Number) obj).longValue());
        }
        return null;
    }

    /**
     *
     * 如果成功将对象转化为Date类型,那么再转化为Calendar;<br>
     * 否则返回null;
     *
     * @param obj 要转换的对象
     * @see #toDateOrNull(java.lang.Object)
     * @return 返回结果，若转化不了则返回null
     */
    public static Calendar toCalendarOrNull(Object obj) {
        Date date = toDateOrNull(obj);
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }
        return null;
    }

    public static <T> T toObjectOrNull(Map<String, Object> map, Class<T> type) {
        if (type == null) {
            throw new NullPointerException("Invalid type: null");
        }
        try {
            T res = type.newInstance();
            Utils.copyProperties(res, map);
            return res;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Unable to create a instance of type: " + type.getName() + ".");
        }
    }

    /**
     * 转化为任意精度的浮点数;<br>
     *
     * 如果obj为null,则返回null;<br>
     * 如果obj是BigDecimal,则返回本身;<br>
     * 如果obj是Number,则返回Double精度的Number值;<br>
     * 如果obj是可以转换为数值的字符串（"+5"或"-3"或"0.123"或"1.23E+10"）则转换为数值（+5或-3或0.123或1.23E+10）;<br>
     * 如果obj是Date,则返回obj指定日期距1970年1月1日午夜（GMT 时间）的毫秒数;<br>
     * 如果obj是Calendar类型,同Date的转换方式;<br>
     * 如果以上情况都不满足,则返回null;
     *
     * @param obj 参数支持null/BigDecimal/Number/CharSequence/Date/Calendar
     * @return 返回结果若转化不了则返回null；
     */
    public static BigDecimal toBigDecimalOrNull(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        if (obj instanceof Number) {
            if (obj instanceof Byte || obj instanceof Short || obj instanceof Integer) {
                return new BigDecimal(((Number) obj).intValue());
            }
            if (obj instanceof Long) {
                return new BigDecimal(((Number) obj).longValue());
            }
            if (obj instanceof BigInteger) {
                return new BigDecimal((BigInteger) obj);
            }
            return BigDecimal.valueOf(((Number) obj).doubleValue());
        }
        if (obj instanceof CharSequence) {
            String strObj = obj.toString();
            return isNullString(strObj) ? null : new BigDecimal(strObj);
        }
        if (obj instanceof Date) {
            return BigDecimal.valueOf(((Date) obj).getTime());
        }
        if (obj instanceof Calendar) {
            return BigDecimal.valueOf(((Calendar) obj).getTimeInMillis());
        }
        return null;
    }

    /**
     * 转化为任意大的整数;<br>
     *
     * 如果obj是null,则返回null;<br>
     * 如果obj是BigInteger,则返回本身;<br>
     * 如果obj是Number,转化为Long精度后返回;<br>
     * 如果obj是可以转换为数值的字符串（"+5"或"-3"或"0.123"或"1.23E+10"）则转换为数值（+5或-3或0或1.23E+10）;<br>
     * 如果obj是Date,则返回obj指定日期距1970年1月1日午夜（GMT 时间）的毫秒数;<br>
     * 如果obj是Calendar类型,同Date的转换方式;<br>
     * 如果以上情况都不满足,则返回null;
     *
     * @param obj 参数支持null/BigInteger/Number/CharSequence/Date/Calendar
     * @return 返回结果，若转化不了返回null
     *
     */
    public static BigInteger toBigInteger(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof BigInteger) {
            return (BigInteger) obj;
        }
        if (obj instanceof Number) {
            return BigInteger.valueOf(((Number) obj).longValue());
        }
        if (obj instanceof CharSequence) {
            String strObj = obj.toString();
            if (isNullString(strObj)) {
                return null;
            } else if (strObj.indexOf('.') != -1) {
                float f = Float.parseFloat(strObj);
                return BigInteger.valueOf((long) f);
            } else {
                return new BigInteger(strObj);
            }
        }
        if (obj instanceof Date) {
            return BigInteger.valueOf(((Date) obj).getTime());
        }
        if (obj instanceof Calendar) {
            return BigInteger.valueOf(((Calendar) obj).getTimeInMillis());
        }
        return null;
    }

    /**
     * 将指定类型转化为长整型;<br>
     *
     * 如果obj为null,则返回null;<br>
     * 如果obj为Long类型,则返回本身;<br>
     * 如果obj为Number,转化为Long精度后返回;<br>
     * 如果obj为Date,转化为毫秒数后返回;<br>
     * 如果obj为Calendar,转化为毫秒数后返回;<br>
     * 如果是CharSequence,则通过{@link java.lang.Long#parseLong(java.lang.String)
     * }转换为Long类型;<br>
     * 如果以上情况都不满足,则返回null;
     *
     * @param obj 参数支持null/Long/Number/Date/Calendar/CharSequence
     * @return 返回结果，若转化不了则返回null;
     */
    public static Long toLongOrNull(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Long) {
            return (Long) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        if (obj instanceof Date) {
            return ((Date) obj).getTime();
        }
        if (obj instanceof Calendar) {
            return ((Calendar) obj).getTimeInMillis();
        }
        if (obj instanceof CharSequence) {
            String strObj = obj.toString();
            return isNullString(strObj) ? null : Long.parseLong(strObj);
        }
        return null;
    }

    /**
     * 将指定类型转化为整型;<br>
     *
     * 如果obj为null,则返回null;<br>
     * 如果obj为Integer类型,则返回本身;<br>
     * 如果obj为Number,转化为Integer精度后返回;<br>
     * 如果obj是CharSequence,则通过{@link java.lang.Integer#parseInt(java.lang.String)
     * }转换为Integer类型;<br>
     * 如果以上情况都不满足,则返回null;
     *
     * @param obj 参数支持null/Integer/Number/CharSequence
     * @return 返回结果，若转化不了则返回null；
     *
     */
    public static Integer toIntegerOrNull(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        if (obj instanceof CharSequence) {
            String strObj = obj.toString();
            return isNullString(strObj) ? null : Integer.parseInt(strObj);
        }
        return null;
    }

    /**
     * 将指定类型转化为短整型;<br>
     *
     * 如果obj为null,则返回null;<br>
     * 如果obj为Short类型,则返回本身;<br>
     * 如果obj为Number,转化为Short精度后返回;<br>
     * 如果是CharSequence,则通过{@link java.lang.Short#parseShort(java.lang.String)
     * }转换为Short类型;<br>
     * 如果以上情况都不满足,则返回null;
     *
     * @param obj 参数支持null/Short/Number/CharSequence
     * @return 返回结果，若转化不了则返回null；
     *
     */
    public static Short toShortOrNull(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Short) {
            return (Short) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).shortValue();
        }
        if (obj instanceof CharSequence) {
            String strObj = obj.toString();
            return isNullString(strObj) ? null : Short.parseShort(strObj);
        }
        return null;
    }

    /**
     * 将指定类型转化为单精度浮点型;<br>
     *
     * 如果obj为null,则返回null;<br>
     * 如果obj为Float类型,则返回本身;<br>
     * 如果obj为Number,转化为Float精度后返回;<br>
     * 如果obj是CharSequence,则通过{@link java.lang.Float#parseFloat(java.lang.String)
     * }转换为Float类型;<br>
     * 如果以上情况都不满足,则返回null;
     *
     * @param obj 参数支持null/Float/Number/CharSequence
     * @return 返回结果，若转化不了则返回null
     *
     */
    public static Float toFloatOrNull(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Float) {
            return (Float) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).floatValue();
        }
        if (obj instanceof CharSequence) {
            String strObj = obj.toString();
            return isNullString(strObj) ? null : Float.parseFloat(strObj);
        }
        return null;
    }

    /**
     * 将指定类型转化为双精度浮点型;<br>
     *
     * 如果obj为null,则返回null;<br>
     * 如果obj为Double类型,则返回本身;<br>
     * 如果obj为Number,转化为Double精度后返回;<br>
     * 如果obj是CharSequence,则通过{@link java.lang.Double#parseDouble(java.lang.String)
     * }转换为Double类型;<br>
     * 如果以上情况都不满足,则返回null;
     *
     * @param obj 参数支持null/Double/Number/CharSequence
     * @return 返回结果，若转化不了返回null
     *
     */
    public static Double toDoubleOrNull(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Double) {
            return (Double) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        if (obj instanceof CharSequence) {
            String strObj = obj.toString();
            return isNullString(strObj) ? null : Double.parseDouble(strObj);
        }
        return null;
    }

    /**
     * 将指定类型转化为字节;<br>
     * 如果obj是null,则返回null;<br>
     * 如果obj是Byte类型,则返回本身;<br>
     * 如果obj是Number,转化为Byte精度后返回本身;<br>
     * 如果obj是CharSequence, 则通过{@link java.lang.Byte#parseByte(java.lang.String)
     * }转换为Byte;<br>
     * 如果以上情况都不满足,则返回null;
     *
     * @param obj 支持的参数类型有null/Number/Byte/CharSequence
     * @return 返回结果，若转化不了返回null
     *
     */
    public static Byte toByteOrNull(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Byte) {
            return (Byte) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).byteValue();
        }
        if (obj instanceof CharSequence) {
            String strObj = obj.toString();
            return isNullString(strObj) ? null : Byte.parseByte(strObj);
        }
        return null;
    }

    /**
     * 将指定类型转化为字符;<br>
     * 如果obj是null,则返回null;<br>
     * 如果obj是Character,则返回本身;<br>
     * 如果obj是Number,转化为short精度后返回本身;<br>
     * 如果obj是CharSequence,则返回字符串的第一个字符;<br>
     * 如果以上情况都不满足,则返回null;
     *
     * @param obj 支持的参数类型有null/Character/Number/CharSequence
     * @return 转换结果，若转换不了返回null
     */
    public static Character toCharOrNull(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Character) {
            return (Character) obj;
        }
        if (obj instanceof Number) {
            return (char) ((Number) obj).shortValue();
        }
        if (obj instanceof CharSequence) {
            CharSequence strObj = (CharSequence) obj;
            return isNullString(strObj) ? null : strObj.charAt(0);
        }
        return null;
    }

    /**
     * 判断是否为空字符串;<br>
     *
     * 如果value长度等于0,则返回true;<br>
     * 如果value为"null",则返回true;<br>
     * 如果value为"",则返回true;<br>
     * 如果found不存在,则返回false;
     *
     * @param value 支持的参数类型有CharSequence
     * @return 返回结果
     */
    private static boolean isNullString(CharSequence value) {
        int length = value.length();
        if (length == 0) {
            return true;
        }
        boolean found = false;
        int i = 0;
        for (; i < length; i++) {
            char word = value.charAt(i);
            if (Character.isSpaceChar(word)) {
                continue;
            }
            if (word != 'n') {
                break;
            } else {
                found = true;
                break;
            }
        }
        if (!found) {
            return false;
        }
        if (i + 3 >= length) {
            return false;
        }
        return value.charAt(i + 1) == 'u' && value.charAt(i + 2) == 'l' && value.charAt(i + 3) == 'l';
    }

    /**
     * 如果obj是CharSequence类型,当且仅当obj忽略大小写等于"true"时，返回true，否则false;<br>
     * 其它情况同{@link #test(java.lang.Object) };
     *
     * @param obj 要转换的obj
     * 支持的参数类型有null/Boolean/Number/CharSequence/Array/Enumeration/Iterable/Iterator/Map
     * @return 返回结果，若转化不了返回null
     */
    public static boolean toBoolean(Object obj) {
        if (obj instanceof CharSequence) {
            return "true".equalsIgnoreCase(obj.toString());
        }
        return test(obj);
    }

    /**
     * 如果obj是null,则返回false;<br>
     * 如果obj是Boolean类型,返回值是本身;<br>
     * 如果obj是Number类型,先转换为Double精度类型,是0则返回false,否则返回true;<br>
     * 如果obj是CharSequence,长度大于0,则返回true,否则返回false;<br>
     * 如果obj是Array,长度大于0,则返回true,否则返回false;<br>
     * 如果obj是Enumeration,包含更多的元素,则返回true;<br>
     * 如果obj是Iterable,如果还有元素,则返回true,否则返回false;<br>
     * 如果obj是Map不为空则返回true,否则返回false;<br>
     * 如果以上情况都不满足,则返回true;
     *
     * @param obj 要判断的obj
     * 支持的参数类型有null/Boolean/Number/CharSequence/Array/Enumeration/Iterable/Iterator/Map
     * @return 判断结果
     */
    public static boolean test(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue() != 0;
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() > 0;
        }
        if (obj.getClass().isArray()) {
            if (obj instanceof Object[]) {
                return ((Object[]) obj).length > 0;
            }
            if (obj instanceof byte[]) {
                return ((byte[]) obj).length > 0;
            }
            if (obj instanceof int[]) {
                return ((int[]) obj).length > 0;
            }
            if (obj instanceof short[]) {
                return ((short[]) obj).length > 0;
            }
            if (obj instanceof long[]) {
                return ((long[]) obj).length > 0;
            }
            if (obj instanceof float[]) {
                return ((float[]) obj).length > 0;
            }
            if (obj instanceof double[]) {
                return ((double[]) obj).length > 0;
            }
            if (obj instanceof char[]) {
                return ((char[]) obj).length > 0;
            }
            if (obj instanceof boolean[]) {
                return ((boolean[]) obj).length > 0;
            }
        }
        if (obj instanceof Enumeration) {
            return ((Enumeration) obj).hasMoreElements();
        }
        if (obj instanceof Iterable) {
            return ((Iterable) obj).iterator().hasNext();
        }
        if (obj instanceof Iterator) {
            return ((Iterator) obj).hasNext();
        }
        if (obj instanceof Map) {
            return !((Map) obj).isEmpty();
        }
        return true;
    }

    public static byte[] toByteArrayOrNull(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            return (byte[]) obj;
        }
        if (isIndexedContainer(obj)) {
            int len = getSize(obj);
            byte[] out = new byte[len];
            for (int i = 0; i < len; ++i) {
                Byte v = toByteOrNull(getByIndex(obj, i));
                if (v == null) {
                    return null;
                }
                out[i] = v;
            }
            return out;
        }
        if (obj instanceof CharSequence) {
            return decodeBase64((CharSequence) obj);
        }
        return null;
    }

    public static Byte[] toObjectByteArrayOrNull(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Byte[]) {
            return (Byte[]) obj;
        }
        if (isIndexedContainer(obj)) {
            int len = getSize(obj);
            Byte[] out = new Byte[len];
            for (int i = 0; i < len; ++i) {
                Byte v = toByteOrNull(getByIndex(obj, i));
                if (v == null) {
                    return null;
                }
                out[i] = v;
            }
            return out;
        }
        if (obj instanceof CharSequence) {
            byte[] bytes = decodeBase64((CharSequence) obj);
            return toObjectByteArrayOrNull(bytes);
        }
        return null;
    }

    public static final char[] CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    public static final int[]  IA = new int[256];
    static {
        Arrays.fill(IA, -1);
        for (int i = 0, iS = CA.length; i < iS; i++)
            IA[CA[i]] = i;
        IA['='] = 0;
    }

    public static byte[] decodeBase64(CharSequence s) {
        // Check special case
        int sLen = s.length();
        if (sLen == 0) {
            return new byte[0];
        }

        int sIx = 0, eIx = sLen - 1; // Start and end index after trimming.

        // Trim illegal chars from start
        while (sIx < eIx && IA[s.charAt(sIx) & 0xff] < 0)
            sIx++;

        // Trim illegal chars from end
        while (eIx > 0 && IA[s.charAt(eIx) & 0xff] < 0)
            eIx--;

        // get the padding count (=) (0, 1 or 2)
        int pad = s.charAt(eIx) == '=' ? (s.charAt(eIx - 1) == '=' ? 2 : 1) : 0; // Count '=' at end.
        int cCnt = eIx - sIx + 1; // Content count including possible separators
        int sepCnt = sLen > 76 ? (s.charAt(76) == '\r' ? cCnt / 78 : 0) << 1 : 0;

        int len = ((cCnt - sepCnt) * 6 >> 3) - pad; // The number of decoded bytes
        byte[] dArr = new byte[len]; // Preallocate byte[] of exact length

        // Decode all but the last 0 - 2 bytes.
        int d = 0;
        for (int cc = 0, eLen = (len / 3) * 3; d < eLen;) {
            // Assemble three bytes into an int from four "valid" characters.
            int i = IA[s.charAt(sIx++)] << 18 | IA[s.charAt(sIx++)] << 12 | IA[s.charAt(sIx++)] << 6
                    | IA[s.charAt(sIx++)];

            // Add the bytes
            dArr[d++] = (byte) (i >> 16);
            dArr[d++] = (byte) (i >> 8);
            dArr[d++] = (byte) i;

            // If line separator, jump over it.
            if (sepCnt > 0 && ++cc == 19) {
                sIx += 2;
                cc = 0;
            }
        }

        if (d < len) {
            // Decode last 1-3 bytes (incl '=') into 1-3 bytes
            int i = 0;
            for (int j = 0; sIx <= eIx - pad; j++)
                i |= IA[s.charAt(sIx++)] << (18 - j * 6);

            for (int r = 16; d < len; r -= 8)
                dArr[d++] = (byte) (i >> r);
        }

        return dArr;
    }

    public static boolean isIterable(Object obj) {
        if (obj == null) {
            return false;
        }
        return obj.getClass().isArray() || obj instanceof Enumeration || obj instanceof Iterable || obj instanceof Iterator || obj instanceof Map;
    }

    public static boolean isContainer(Object obj) {
        return isIterable(obj) && !(obj instanceof CharSequence);
    }

    public static boolean isIndexedIterable(Object obj) {
        return isIterable(obj) && !(obj instanceof Map);
    }

    public static boolean isIndexedContainer(Object obj) {
        return isContainer(obj) && !(obj instanceof Map);
    }

    public static boolean isIterableType(Class cls) {
        if (cls == null) {
            return false;
        }
        return cls.isArray() || Iterable.class.isAssignableFrom(cls) || Map.class.isAssignableFrom(cls) || Iterator.class.isAssignableFrom(cls) || Enumeration.class.isAssignableFrom(cls);
    }

    public static boolean isContainerType(Class cls) {
        return isIterableType(cls) && !CharSequence.class.isAssignableFrom(cls);
    }

    public static boolean isIndexedIterableType(Class cls) {
        return isIterableType(cls) && !Map.class.isAssignableFrom(cls);
    }

    public static boolean isIndexedContainerType(Class cls) {
        return isContainerType(cls) && !Map.class.isAssignableFrom(cls);
    }

    public static int getSize(Object iterable) {
        if (iterable == null) {
            return -1;
        }
        if (iterable.getClass().isArray()) {
            if (iterable instanceof Object[]) {
                Object[] array = (Object[]) iterable;
                return array.length;
            }
            if (iterable instanceof byte[]) {
                byte[] array = (byte[]) iterable;
                return array.length;
            }
            if (iterable instanceof int[]) {
                int[] array = (int[]) iterable;
                return array.length;
            }
            if (iterable instanceof short[]) {
                short[] array = (short[]) iterable;
                return array.length;
            }
            if (iterable instanceof long[]) {
                long[] array = (long[]) iterable;
                return array.length;
            }
            if (iterable instanceof float[]) {
                float[] array = (float[]) iterable;
                return array.length;
            }
            if (iterable instanceof double[]) {
                double[] array = (double[]) iterable;
                return array.length;
            }
            if (iterable instanceof char[]) {
                char[] array = (char[]) iterable;
                return array.length;
            }
            if (iterable instanceof boolean[]) {
                boolean[] array = (boolean[]) iterable;
                return array.length;
            }
            return -1;
        } else if (iterable instanceof Collection) {
            return ((Collection) iterable).size();
        } else if (iterable instanceof Enumeration) {
            Enumeration enumeration = (Enumeration) iterable;
            int i = 0;
            while (enumeration.hasMoreElements()) {
                ++i;
            }
            return i;
        } else if (iterable instanceof Iterable) {
            Iterable _iterable = (Iterable) iterable;
            int i = 0;
            for (Object el : _iterable){
                ++i;
            }
            return i;
        } else if ((iterable instanceof Map)) {
            return ((Map) iterable).size();
        } else {
            return -1;
        }
    }

    public static Object getByIndex(Object iterable, int index) {
        if (iterable == null) {
            throw new NullPointerException();
        }
        if (iterable.getClass().isArray()) {
            if (iterable instanceof Object[]) {
                Object[] array = (Object[]) iterable;
                return array[index];
            }
            if (iterable instanceof byte[]) {
                byte[] array = (byte[]) iterable;
                return array[index];
            }
            if (iterable instanceof int[]) {
                int[] array = (int[]) iterable;
                return array[index];
            }
            if (iterable instanceof short[]) {
                short[] array = (short[]) iterable;
                return array[index];
            }
            if (iterable instanceof long[]) {
                long[] array = (long[]) iterable;
                return array[index];
            }
            if (iterable instanceof float[]) {
                float[] array = (float[]) iterable;
                return array[index];
            }
            if (iterable instanceof double[]) {
                double[] array = (double[]) iterable;
                return array[index];
            }
            if (iterable instanceof char[]) {
                char[] array = (char[]) iterable;
                return array[index];
            }
            if (iterable instanceof boolean[]) {
                boolean[] array = (boolean[]) iterable;
                return array[index];
            }
            throw new IllegalArgumentException("This is impossible! The type of iterable is " + iterable.getClass().getName() + ".");
        } else if (iterable instanceof List) {
            return ((List) iterable).get(index);
        } else if (iterable instanceof Enumeration) {
            Enumeration enumeration = (Enumeration) iterable;
            int i = 0;
            while (enumeration.hasMoreElements()) {
                if (i == index) {
                    return enumeration.nextElement();
                } else {
                    enumeration.nextElement();
                }
                ++i;
            }
            throw new IndexOutOfBoundsException("Index: " + index +". However the size is " + i + ".");
        } else if (iterable instanceof Iterable) {
            Iterable _iterable = (Iterable) iterable;
            int i = 0;
            for (Object el : _iterable){
                if (i == index) {
                    return el;
                }
                ++i;
            }
            throw new IndexOutOfBoundsException("Index: " + index +". However the size is " + i + ".");
        } else if ((iterable instanceof Map)) {
            return getByIndex(((Map) iterable).entrySet(), index);
        } else {
            throw new IllegalArgumentException("Invalid iterable type. The input type is " + iterable.getClass().getName() + ".");
        }
    }

    public static <E> void iterateByIndex(Object iterable, BiConsumer<E, Integer> walker) {
        if (iterable == null) {
            return;
        }
        if (iterable.getClass().isArray()) {
            if (iterable instanceof Object[]) {
                Object[] array = (Object[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    walker.accept((E) array[i], i);
                }
            }
            if (iterable instanceof byte[]) {
                byte[] array = (byte[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    walker.accept((E)(Byte) array[i], i);
                }
            }
            if (iterable instanceof int[]) {
                int[] array = (int[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    walker.accept((E)(Integer) array[i], i);
                }
            }
            if (iterable instanceof short[]) {
                short[] array = (short[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    walker.accept((E)(Short) array[i], i);
                }
            }
            if (iterable instanceof long[]) {
                long[] array = (long[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    walker.accept((E)(Long) array[i], i);
                }
            }
            if (iterable instanceof float[]) {
                float[] array = (float[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    walker.accept((E)(Float) array[i], i);
                }
            }
            if (iterable instanceof double[]) {
                double[] array = (double[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    walker.accept((E)(Double) array[i], i);
                }
            }
            if (iterable instanceof char[]) {
                char[] array = (char[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    walker.accept((E)(Character) array[i], i);
                }
            }
            if (iterable instanceof boolean[]) {
                boolean[] array = (boolean[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    walker.accept((E)(Boolean) array[i], i);
                }
            }
        } else if (iterable instanceof Enumeration) {
            Enumeration enumeration = (Enumeration) iterable;
            int i = 0;
            while (enumeration.hasMoreElements()) {
                Object next = enumeration.nextElement();
                walker.accept((E) next, i++);
            }
        } else if (iterable instanceof Iterable) {
            Iterable _iterable = (Iterable) iterable;
            int i = 0;
            for (Object el : _iterable){
                walker.accept((E) el, i++);
            }
        } else if (iterable instanceof Iterator) {
            Iterator iterator = (Iterator) iterable;
            int i = 0;
            while (iterator.hasNext()) {
                walker.accept((E) iterator.next(), i++);
            }
        } else if ((iterable instanceof Map)) {
            Iterable values = ((Map) iterable).values();
            int i = 0;
            for (Object el : values){
                walker.accept((E) el, i++);
            }
        }
    }

    public static <E, T> void iterateWithIterator(Object iterable, T initialIterator, TriFunction<E, Integer, T, T> walker) {
        if (iterable == null) {
            return;
        }
        T iter = initialIterator;
        if (iterable.getClass().isArray()) {
            if (iterable instanceof Object[]) {
                Object[] array = (Object[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    iter = walker.apply((E) array[i], i, iter);
                }
            }
            if (iterable instanceof byte[]) {
                byte[] array = (byte[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    iter = walker.apply((E)(Byte) array[i], i, iter);
                }
            }
            if (iterable instanceof int[]) {
                int[] array = (int[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    iter = walker.apply((E)(Integer) array[i], i, iter);
                }
            }
            if (iterable instanceof short[]) {
                short[] array = (short[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    iter = walker.apply((E)(Short) array[i], i, iter);
                }
            }
            if (iterable instanceof long[]) {
                long[] array = (long[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    iter = walker.apply((E)(Long) array[i], i, iter);
                }
            }
            if (iterable instanceof float[]) {
                float[] array = (float[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    iter = walker.apply((E)(Float) array[i], i, iter);
                }
            }
            if (iterable instanceof double[]) {
                double[] array = (double[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    iter = walker.apply((E)(Double) array[i], i, iter);
                }
            }
            if (iterable instanceof char[]) {
                char[] array = (char[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    iter = walker.apply((E)(Character) array[i], i, iter);
                }
            }
            if (iterable instanceof boolean[]) {
                boolean[] array = (boolean[]) iterable;
                for (int i = 0; i < array.length; ++i) {
                    iter = walker.apply((E)(Boolean) array[i], i, iter);
                }
            }
        } else if (iterable instanceof Enumeration) {
            Enumeration enumeration = (Enumeration) iterable;
            int i = 0;
            while (enumeration.hasMoreElements()) {
                Object next = enumeration.nextElement();
                iter = walker.apply((E) next, i++, iter);
            }
        } else if (iterable instanceof Iterable) {
            Iterable _iterable = (Iterable) iterable;
            int i = 0;
            for (Object el : _iterable){
                iter = walker.apply((E) el, i++, iter);
            }
        } else if (iterable instanceof Iterator) {
            Iterator iterator = (Iterator) iterable;
            int i = 0;
            while (iterator.hasNext()) {
                iter = walker.apply((E) iterator.next(), i++, iter);
            }
        } else if ((iterable instanceof Map)) {
            Iterable values = ((Map) iterable).values();
            int i = 0;
            for (Object el : values){
                iter = walker.apply((E) el, i++, iter);
            }
        }
    }
}