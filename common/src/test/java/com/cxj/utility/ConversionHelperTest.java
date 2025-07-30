/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Administrator
 */
public class ConversionHelperTest {
    
    public ConversionHelperTest() {
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
    
    @After
    public void tearDown() {
    }

    /**
     * Test of bytesToHex method, of class ConversionHelper.
     */
    @Test
    public void test() {
        String hex = "0xf5a98c0143ed80a";
        byte[] bytes = ConversionHelper.hexToBytes(hex);
        String result = StringUtils.removePattern(ConversionHelper.bytesToHex(bytes, true), "^0x");
        result = StringUtils.removePattern(result, "^0*");
        String except = StringUtils.removePattern(hex, "^0x");
        except = StringUtils.removePattern(except, "^0*");
        assertEquals(except.toUpperCase(), result.toUpperCase());
    }
    
}
