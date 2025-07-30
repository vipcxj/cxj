package com.cxj.utility;

import com.cxj.parser.PairParser;
import com.cxj.parser.PairsParser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by vipcxj on 2018/2/2.
 */
public class PairParserTest {

    @Test
    public void testFindContent() {
        PairParser parser1 = PairParser.from("#[", "]");
        PairParser parser2 = PairParser.from("#{", "}");
        PairParser parser3 = PairParser.from("[", "]");
        PairParser parser4 = PairParser.from("{", "}");
        PairParser parser5 = PairParser.from("(", ")");
        PairParser parser6 = PairParser.symToken("\"", "\\");
        PairParser parser7 = PairParser.symToken("'", "\\");
        PairsParser parsers = PairsParser.from(parser1, parser2);
        parser1.addSubContext(parser2).addSubContext(parser3).addSubContext(parser4).addSubContext(parser5).addSubContext(parser6).addSubContext(parser7);
        parser2.addSubContext(parser1).addSubContext(parser3).addSubContext(parser4).addSubContext(parser5).addSubContext(parser6).addSubContext(parser7);
        parser3.addSubContext(parser1).addSubContext(parser2).addSubContext(parser4).addSubContext(parser5).addSubContext(parser6).addSubContext(parser7);
        parser4.addSubContext(parser1).addSubContext(parser2).addSubContext(parser3).addSubContext(parser5).addSubContext(parser6).addSubContext(parser7);
        parser5.addSubContext(parser1).addSubContext(parser2).addSubContext(parser3).addSubContext(parser4).addSubContext(parser6).addSubContext(parser7);
        Assert.assertEquals("", parser1.findContent("#[]", 0));
        Assert.assertEquals("() -> []", parser1.findContent("#[() -> []]", 0));
        Assert.assertEquals("() -> #[]", parser3.findContent("[() -> #[]]", 0));
        Assert.assertEquals("#[({#{#[]}})]", parser1.findContent("#[#[({#{#[]}})]]", 0));
        Assert.assertEquals("", parser3.findContent("[]", 0));
        Assert.assertEquals("4", parser3.findContent("[4], [5]", 0));
        Assert.assertEquals(null, parser3.findContent("[[]", 0));
        Assert.assertEquals("\"[[[][[[\"", parser3.findContent("[\"[[[][[[\"]", 0));
        Assert.assertEquals("\"[[[][[\\\"[\"", parser3.findContent("[\"[[[][[\\\"[\"]", 0));
        String testString = "re#{ {\"\"}({re\"4w][}{\\\"{}}\"})e#{}} #{ #[{\"ttt\"}]({re\"4w][}{\\\"{}}\"})e#{}}#[#{ee{}}]";
        Assert.assertEquals("{\"ttt\"}", parser1.findContent(testString, 0));
        Assert.assertEquals(" {\"\"}({re\"4w][}{\\\"{}}\"})e#{}", parser2.findContent(testString, 0));
        Assert.assertEquals("revar1 var2var3", parsers.replace(testString, CollectionHelper.mapFrom(
                " {\"\"}({re\"4w][}{\\\"{}}\"})e#{}", "var1",
                " #[{\"ttt\"}]({re\"4w][}{\\\"{}}\"})e#{}", "var2",
                "#{ee{}}", "var3"
        )));
    }

    @Test
    public void testReplace() {
        PairParser parser = PairParser.from("${", "}", null, false);
        Assert.assertEquals("hello world!", parser.replace("hello ${var}!", var -> "world"));
        Assert.assertEquals("hello world!", parser.replace("${var1} ${var2}!", var -> {
            if ("var1".equals(var)) {
                return "hello";
            } else if ("var2".equals(var)) {
                return "world";
            } else {
                throw new IllegalArgumentException("Invalid var: " + var);
            }
        }));
        Assert.assertEquals("hello world!", parser.replace("hello ${{var}!", var -> "world"));
        Assert.assertEquals("hello world}!", parser.replace("hello ${var}}!", var -> "world"));
        Assert.assertEquals("hello world}!", parser.replace("hello ${{var}}!", var -> "world"));
        parser = PairParser.from("{{", "}}", null, false);
        Assert.assertEquals("hello world!", parser.replace("hello {{var}}!", var -> "world"));
        Assert.assertEquals("hello world!",
                parser.replace("{{var1}} {{var2}}!", CollectionHelper.mapFrom(
                        "var1", "hello",
                        "var2", "world"
                )));
    }

    @Test
    public void testCopy() {
        PairParser parser = PairParser.PAR_BRACE.copy();
        parser.addSubContext(parser);
        Assert.assertEquals("{}", parser.findContent("{{}}", 0));
        Assert.assertEquals("{{a}}", parser.findContent("{{{a}}}", 0));
        Assert.assertEquals("{", PairParser.PAR_BRACE.findContent("{{}}", 0));
    }
}
