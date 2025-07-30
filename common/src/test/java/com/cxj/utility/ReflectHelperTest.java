package com.cxj.utility;

import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

/**
 * Created by vipcxj on 2018/1/9.
 */
public class ReflectHelperTest {

    interface IntA {}
    interface IntB extends IntA {}
    interface IntC {}
    static class Base implements IntB {}
    static class BaseB implements IntC {}
    @SuppressWarnings("unused")
    static class GBase<T> extends Base {}
    static class ChildA extends GBase<Integer> {}
    static class ChildB extends GBase<Long> {}
    static class ChildC extends BaseB implements IntB {}
    static class SubChild extends ChildA {}

    @Test
    public void testClassDistance() {
        Assert.assertEquals(0, ReflectHelper.classDistance(Base.class, Base.class));
        Assert.assertEquals(0, ReflectHelper.classDistance(GBase.class, GBase.class));
        Assert.assertEquals(0, ReflectHelper.classDistance(ChildA.class, ChildA.class));
        Assert.assertEquals(0, ReflectHelper.classDistance(ChildB.class, ChildB.class));
        Assert.assertEquals(0, ReflectHelper.classDistance(SubChild.class, SubChild.class));
        Assert.assertEquals(1, ReflectHelper.classDistance(Base.class, GBase.class));
        Assert.assertEquals(1, ReflectHelper.classDistance(GBase.class, Base.class));
        Assert.assertEquals(2, ReflectHelper.classDistance(Base.class, ChildA.class));
        Assert.assertEquals(2, ReflectHelper.classDistance(ChildA.class, Base.class));
        Assert.assertEquals(3, ReflectHelper.classDistance(Base.class, SubChild.class));
        Assert.assertEquals(3, ReflectHelper.classDistance(SubChild.class, Base.class));
        Assert.assertEquals(4, ReflectHelper.classDistance(SubChild.class, Object.class));
        Assert.assertEquals(4, ReflectHelper.classDistance(Object.class, SubChild.class));
        Assert.assertEquals(2, ReflectHelper.classDistance(Base.class, IntA.class));
        Assert.assertEquals(2, ReflectHelper.classDistance(IntA.class, Base.class));
        Assert.assertEquals(1, ReflectHelper.classDistance(IntA.class, IntB.class));
        Assert.assertEquals(1, ReflectHelper.classDistance(IntB.class, IntA.class));
        Assert.assertEquals(1, ReflectHelper.classDistance(IntA.class, Object.class));
        Assert.assertEquals(1, ReflectHelper.classDistance(Object.class, IntA.class));
        Assert.assertEquals(2, ReflectHelper.classDistance(ChildC.class, IntC.class));
        Assert.assertEquals(1, ReflectHelper.classDistance(ChildC.class, IntB.class));
        Assert.assertTrue(Objects.equals(ChildA.class.getSuperclass(), ChildB.class.getSuperclass()));

    }
}
