package com.cxj.utility;

import org.junit.Assert;
import org.junit.Test;

import static com.cxj.utility.StringHelper.findPairEndIndex;
import static com.cxj.utility.StringHelper.unescape;

/**
 * Created by vipcxj on 2018/2/1.
 */
public class StringHelperTest {

    @Test
    public void testFindPairEndIndex() {
        Assert.assertEquals(-1, findPairEndIndex("[]", 0, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\"));
        Assert.assertEquals(1, findPairEndIndex("[]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\"));
        Assert.assertEquals(2, findPairEndIndex("[a]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\"));
        Assert.assertEquals(-1, findPairEndIndex("[[a]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\"));
        Assert.assertEquals(4, findPairEndIndex("[[a]]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\"));
        Assert.assertEquals(5, findPairEndIndex("[\"[a\"]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\"));
        Assert.assertEquals(-1, findPairEndIndex("[\"[a]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\"));
        Assert.assertEquals(7, findPairEndIndex("[\"[a\\\"\"]", 1, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\"));
        Assert.assertEquals(16, findPairEndIndex("2[\"[a\\\"\"uig\"]]]\"]]", 2, "[", "]", null, "\"", "\"", "\\", "\'", "\'", "\\"));
    }

    @Test
    public void testUnescape() {
        Assert.assertEquals("04", unescape("04", "\\"));
        Assert.assertEquals("04", unescape("\\04", "\\"));
        Assert.assertEquals("\t4", unescape("\\\t4", "\\"));
        Assert.assertEquals("44\\444", unescape("44\\\\444", "\\"));
        Assert.assertEquals("5\\04sjj", unescape("5\\\\\\04s\\jj", "\\"));
    }
}
