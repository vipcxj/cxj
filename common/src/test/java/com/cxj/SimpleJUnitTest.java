/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj;

import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Administrator
 */
public class SimpleJUnitTest {

    public SimpleJUnitTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void hello() {
        String txt = "xa/xa.js";

        String re1 = "(([^\\/\\\\]+))";	// Word 1
        String re2 = "(\\/)";	// Any Single Character 1
        String re3 = "(\\1)";	// Word 2
        String re4 = "(\\.js)";	// Any Single Character 2

        Pattern p = Pattern.compile("(([^\\/\\\\]+))([\\/\\\\])(\\1)(\\.js)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        java.util.regex.Matcher m = p.matcher(txt);
        if (m.find()) {
            String word1 = m.group(1);
            String c1 = m.group(2);
            String word2 = m.group(3);
            String c2 = m.group(4);
            System.out.print("(" + word1 + ")" + "(" + c1 + ")" + "(" + word2 + ")" + "(" + c2 + ")" + "\n");
        }
    }
}
