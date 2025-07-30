/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.bean;

import com.cxj.functional.TriConsumer;
import com.cxj.utility.CollectionHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author Administrator
 */
public class BeanViewTest {

    public static class TestObject {

        private String a, b, c, d, f;
        private final String e;

        public TestObject(String a, String b, String c, String d, String e, String f) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
            this.f = f;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getD() {
            return d;
        }

        public void setD(String d) {
            this.d = d;
        }

        public String getE() {
            return e;
        }

        public void setF(String f) {
            this.f = f;
        }

        public String readF() {
            return f;
        }

    }

    private static final String INT_ARRAY = "int array";
    private static final String OBJECT_ARRAY = "object array";
    private static final String LIST = "list";
    private static final String MAP = "map";
    private static final String OBJECT = "object";

    private BeanView toTest;

    private List<String> nativeNames;
    private List<String> mappedNames;
    private List<Object> values;
    private List<Class<?>> types;
    private Set<String> rNames, wNames;
    private Map<String, String> maps;
    private List<String> writeNativeList;
    private List<String> blackNativeList;
    private BeanViewInfo.FilterMode mode;
    private Map<String, BeanViewProperty> extendPropertiesMeta;
    private Map<String, Object> extendProperties;

    public BeanViewTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    public Map<String, String> getMaps() {
        if (maps == null) {
            maps = new HashMap<>();
        }
        return maps;
    }

    public void loadMaps() {
        assertThat(toTest, is(notNullValue()));
        assertThat(nativeNames, is(notNullValue()));
        toTest.getBeanInfo().addMaps(getMaps());
        if (extendPropertiesMeta != null && !extendPropertiesMeta.isEmpty()) {
            nativeNames = new ArrayList<>(nativeNames);
            values = new ArrayList<>(values);
            types = new ArrayList<>(types);
            rNames = new HashSet<>(rNames);
            wNames = new HashSet<>(wNames);
            for (Map.Entry<String, BeanViewProperty> property : extendPropertiesMeta.entrySet()) {
                nativeNames.add(property.getKey());
                values.add(extendProperties.get(property.getKey()));
                types.add(property.getValue().getType());
                if (property.getValue().isReadable()) {
                    rNames.add(property.getKey());
                }
                if (property.getValue().isWriteable()) {
                    wNames.add(property.getKey());
                }
                toTest.extendProperty(property.getKey(), (Class<Object>) property.getValue().getType(), property.getValue().getContentType(),
                        extendProperties.get(property.getKey()), property.getValue().isReadable(), property.getValue().isWriteable());
            }
        }
        if (mode == BeanViewInfo.FilterMode.WRITE_LIST) {
            blackNativeList = null;
            if (writeNativeList != null) {
                toTest.getBeanInfo().switchFilterModeToWriteList();
                toTest.getBeanInfo().addNativePropertyToWhiteList(writeNativeList);
            }
            List<String> nativeNamesT = new ArrayList<>();
            List<String> mappedNamesT = new ArrayList<>();
            List<Object> valuesT = new ArrayList<>();
            List<Class<?>> typesT = new ArrayList<>();
            for (int i = 0; i < nativeNames.size(); i++) {
                String nName = nativeNames.get(i);
                if (writeNativeList != null && writeNativeList.contains(nName)) {
                    nativeNamesT.add(nName);
                    mappedNamesT.add(getMaps().containsKey(nName) ? getMaps().get(nName) : nName);
                    valuesT.add(values.get(i));
                    typesT.add(types.get(i));
                }
            }
            nativeNames = nativeNamesT;
            mappedNames = mappedNamesT;
            values = valuesT;
            types = typesT;
        } else if (mode == BeanViewInfo.FilterMode.BLACK_LIST) {
            writeNativeList = null;
            if (blackNativeList != null) {
                toTest.getBeanInfo().switchFilterModeToBlackList();
                toTest.getBeanInfo().addNativePropertyToBlackList(blackNativeList);
            }
            List<String> nativeNamesT = new ArrayList<>();
            List<String> mappedNamesT = new ArrayList<>();
            List<Object> valuesT = new ArrayList<>();
            List<Class<?>> typesT = new ArrayList<>();
            for (int i = 0; i < nativeNames.size(); i++) {
                String nName = nativeNames.get(i);
                if (blackNativeList == null || !blackNativeList.contains(nName)) {
                    nativeNamesT.add(nName);
                    mappedNamesT.add(getMaps().containsKey(nName) ? getMaps().get(nName) : nName);
                    valuesT.add(values.get(i));
                    typesT.add(types.get(i));
                }
            }
            nativeNames = nativeNamesT;
            mappedNames = mappedNamesT;
            values = valuesT;
            types = typesT;
        } else {
            fail();
        }
    }

    private void prepareIntArray(boolean reset) {
        nativeNames = Arrays.asList("[0]", "[1]", "[2]", "[3]");
        values = Arrays.asList(0, 1, 2, 3);
        types = Arrays.asList(int.class, int.class, int.class, int.class);
        rNames = ImmutableSet.of("[0]", "[1]", "[2]", "[3]");
        wNames = ImmutableSet.of("[0]", "[1]", "[2]", "[3]");
        if (reset) {
            toTest = new BeanView(new int[]{0, 0, 0, 0});
        } else {
            toTest = new BeanView(new int[]{0, 1, 2, 3});
        }
    }

    private void prepareObjectArray(boolean reset) {
        nativeNames = Arrays.asList("[0]", "[1]", "[2]", "[3]");
        values = Arrays.asList("aa", "bb", "cc", null);
        types = Arrays.asList(String.class, String.class, String.class, String.class);
        rNames = ImmutableSet.of("[0]", "[1]", "[2]", "[3]");
        wNames = ImmutableSet.of("[0]", "[1]", "[2]", "[3]");
        if (reset) {
            toTest = new BeanView(Arrays.asList(null, null, null, null));
        } else {
            toTest = new BeanView(values.toArray(new String[4]));
        }
    }

    private void prepareList(boolean reset) {
        nativeNames = Arrays.asList("[0]", "[1]", "[2]", "[3]");
        values = Arrays.asList("aa", "bb", "cc", null);
        types = Arrays.asList(Object.class, Object.class, Object.class, Object.class);
        rNames = ImmutableSet.of("[0]", "[1]", "[2]", "[3]");
        wNames = ImmutableSet.of("[0]", "[1]", "[2]", "[3]");
        if (reset) {
            toTest = new BeanView(Arrays.asList(null, null, null, null));
        } else {
            toTest = new BeanView(values);
        }
    }

    private void prepareMap(boolean reset) {
        nativeNames = Arrays.asList("a", "b", "c", "d");
        values = Arrays.asList("aa", "bb", "cc", null);
        types = Arrays.asList(String.class, String.class, String.class, Object.class);
        rNames = ImmutableSet.of("a", "b", "c", "d");
        wNames = ImmutableSet.of("a", "b", "c", "d");
        if (reset) {
            toTest = new BeanView(CollectionHelper.mapFrom(nativeNames, Arrays.asList(null, null, null, null)));
        } else {
            toTest = new BeanView(CollectionHelper.mapFrom(nativeNames, values));
        }
    }

    private void prepareObject(boolean reset) {
        nativeNames = Arrays.asList("a", "b", "c", "d", "e", "f");
        values = Arrays.asList("aa", "bb", "cc", null, "ee", "ff");
        types = Arrays.asList(String.class, String.class, String.class, String.class, String.class, String.class);
        rNames = ImmutableSet.of("a", "b", "c", "d", "e");
        wNames = ImmutableSet.of("a", "b", "c", "d", "f");
        if (reset) {
            toTest = new BeanView(new TestObject(null, null, null, null, null, null));
        } else {
            toTest = new BeanView(new TestObject("aa", "bb", "cc", null, "ee", "ff"));
        }
    }

    /**
     * 测试无映射
     */
    private void mapType0() {
        maps = Collections.EMPTY_MAP;
    }

    /**
     * 测试2属性互换映射<br>
     * a -> b <br>
     * b -> a <br>
     * [1] -> [2] <br>
     * [2] -> [1] <br>
     */
    private void mapType1() {
        maps = ImmutableMap.of("a", "b", "b", "a", "[1]", "[2]", "[2]", "[1]");
    }

    /**
     * 测试3属性互换映射<br>
     * a -> b <br>
     * b -> c <br>
     * c -> a <br>
     * [1] -> [2] <br>
     * [2] -> [3] <br>
     * [3] -> [1] <br>
     */
    private void mapType2() {
        maps = ImmutableMap.<String, String>builder().put("a", "b").put("b", "c").put("c", "a").put("[1]", "[2]").put("[2]", "[3]").put("[3]", "[1]").build();
    }

    private void writeListAll() {
        mode = BeanViewInfo.FilterMode.WRITE_LIST;
        writeNativeList = nativeNames;
    }

    private void writeListSome() {
        mode = BeanViewInfo.FilterMode.WRITE_LIST;
        writeNativeList = nativeNames.stream().filter(name -> Math.random() < 0.5).collect(Collectors.toList());
    }

    private void blackListNone() {
        mode = BeanViewInfo.FilterMode.BLACK_LIST;
        blackNativeList = Collections.EMPTY_LIST;
    }

    private void blackListSome() {
        mode = BeanViewInfo.FilterMode.BLACK_LIST;
        blackNativeList = nativeNames.stream().filter(name -> Math.random() < 0.5).collect(Collectors.toList());
    }

    private void extendProperties0() {
        extendPropertiesMeta = null;
    }

    private void extendProperties1() {
        extendPropertiesMeta = new HashMap<>();
        extendPropertiesMeta.put("g", BeanViewProperty.createReadOnlyProperty("g", Integer.class));
        extendPropertiesMeta.put("h", BeanViewProperty.createWriteOnlyProperty("h", String.class));
        extendProperties = new HashMap<>();
        extendProperties.put("g", 1);
        extendProperties.put("h", "hh");
    }

    private void testForAllType(@Nonnull Consumer<String> worker, boolean reset) {
        prepareIntArray(reset);
        blackListNone();
        mapType0();
        extendProperties1();
        loadMaps();
        worker.accept(INT_ARRAY);

        prepareList(reset);
        blackListSome();
        mapType1();
        extendProperties0();
        loadMaps();
        worker.accept(LIST);

        prepareMap(reset);
        writeListAll();
        mapType0();
        extendProperties1();
        loadMaps();
        worker.accept(MAP);

        prepareObject(reset);
        writeListSome();
        mapType2();
        extendProperties1();
        loadMaps();
        worker.accept(OBJECT);

        prepareObjectArray(reset);
        writeListAll();
        mapType1();
        extendProperties1();
        loadMaps();
        worker.accept(OBJECT_ARRAY);
    }

    private void testForAllProperties(@Nonnull TriConsumer<String, Object, Class<?>> worker, @Nonnull List<String> names, @Nonnull List<Object> values, @Nonnull List<Class<?>> types) {
        assertEquals(names.size(), values.size());
        assertEquals(names.size(), types.size());
        for (int i = 0; i < names.size(); i++) {
            worker.accept(names.get(i), values.get(i), types.get(i));
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getProperty method, of class BeanView.
     */
    @org.junit.Test
    public void testGetProperty() {
        System.out.println("getProperty");
        testForAllType(_type -> {
            testForAllProperties((name, value, type) -> {
                if (rNames.contains(name)) {
                    assertEquals(value, toTest.getProperty(name));
                }
            }, mappedNames, values, types);
        }, false);
    }

    /**
     * Test of setProperty method, of class BeanView.
     */
    @org.junit.Test
    public void testSetProperty() {
        System.out.println("setProperty");
        testForAllType(_type -> {
            testForAllProperties((name, value, type) -> {
                if (wNames.contains(name)) {
                    toTest.setProperty(name, value);
                    if (rNames.contains(name)) {
                        assertEquals(value, toTest.getProperty(name));
                    } else {
                        assertEquals(value, BeanViewUtils.getProperty(toTest, name, true));
                    }
                }
            }, mappedNames, values, types);
        }, true);
    }

    /**
     * Test of getPropertyType method, of class BeanView.
     */
    @org.junit.Test
    public void testGetPropertyType() {
        System.out.println("getPropertyType");
        testForAllType(_type -> {
            testForAllProperties((name, value, type) -> {
                assertEquals(type, toTest.getPropertyType(name));
            }, mappedNames, values, types);
        }, false);
    }

    /**
     * Test of getReadablePropertyNames method, of class BeanView.
     */
    @org.junit.Test
    public void testGetReadablePropertyNames() {
        System.out.println("getReadablePropertyNames");
        testForAllType(_type -> {
            assertThat(toTest.getReadablePropertyNames(), everyItem(isIn(rNames)));
        }, false);
    }

    /**
     * Test of getWriteablePropertyNames method, of class BeanView.
     */
    @org.junit.Test
    public void testGetWriteablePropertyNames() {
        System.out.println("getWriteablePropertyNames");
        testForAllType(_type -> {
            assertThat(toTest.getWriteablePropertyNames(), everyItem(isIn(wNames)));
        }, false);
    }

    /**
     * Test of getPropertyNum method, of class BeanView.
     */
    @org.junit.Test
    public void testGetPropertyNum() {
        System.out.println("getPropertyNum");
        testForAllType(_type -> {
            assertEquals(mappedNames.size(), toTest.getPropertyNum());
        }, false);
    }

    /**
     * Test of getPropertyNames method, of class BeanView.
     */
    @org.junit.Test
    public void testGetPropertyNames() {
        System.out.println("getPropertyNames");
        testForAllType(_type -> {
            assertThat(toTest.getPropertyNames(), allOf(everyItem(isIn(mappedNames)), containsInAnyOrder(mappedNames.toArray(new String[mappedNames.size()]))));
        }, false);
    }

    /**
     * Test of get method, of class BeanView.
     */
    @org.junit.Test
    public void testGet_String() {
        System.out.println("get");
        testForAllType(_type -> {
            testForAllProperties((name, value, type) -> {
                if (rNames.contains(name)) {
                    assertEquals(value, toTest.get(name));
                }
            }, mappedNames, values, types);
        }, false);
    }

    /**
     * Test of set method, of class BeanView.
     */
    @org.junit.Test
    public void testSet_String_Object() {
        System.out.println("set");
        testForAllType(_type -> {
            testForAllProperties((name, value, type) -> {
                if (wNames.contains(name)) {
                    toTest.set(name, value);
                    if (rNames.contains(name)) {
                        assertEquals(value, toTest.getProperty(name));
                    } else {
                        assertEquals(value, BeanViewUtils.getProperty(toTest, name, true));
                    }
                }
            }, mappedNames, values, types);
        }, true);
    }

}
