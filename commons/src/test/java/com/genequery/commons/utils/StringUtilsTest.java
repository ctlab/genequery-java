package com.genequery.commons.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;

/**
 * Created by Arbuzov Ivan.
 */
public class StringUtilsTest extends org.junit.Assert {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void testJoin() throws Exception {
    assertEquals(StringUtils.join(Arrays.asList(1, 2, 3), " "), "1 2 3");
  }

  @Test
  public void testMatchNullException1() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.match(null, 1, 2, 3);
  }

  @Test
  public void testMatchNullException2() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.match("", null);
  }

  @Test
  public void testMatchExceptionTooFewArgs1() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.match("{}a{}b{}", 1, 2);
  }

  @Test
  public void testMatchExceptionTooFewArgs2() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.match("a{}b{}", 1);
  }

  @Test
  public void testMatchExceptionTooFewArgs3() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.match("a{}b");
  }

  @Test
  public void testMatchExceptionTooManyArgs1() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.match("{}", 1, 2);
  }

  @Test
  public void testMatchExceptionTooManyArgs2() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.match("a{}", 1, 2);
  }

  @Test
  public void testMatchExceptionTooManyArgs3() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.match("a{}b", 1, 2);
  }

  @Test
  public void testMatchExceptionTooManyArgs4() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.match("{}a{}b", 1, 2, 3);
  }

  @Test
  public void testMatchExceptionTooManyArgs5() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.match("{}a{} b\t{}", 1, 2, 3, 4);
  }

  @Test
  public void testMatchExceptionTooManyArgs6() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.match("{}{}", 1, 2, 3);
  }

  @Test
  public void testMatchNoArgs1() throws Exception {
    assertEquals("Hello!", StringUtils.match("Hello!"));
  }

  @Test
  public void testMatchNoArgs2() throws Exception {
    assertEquals("", StringUtils.match(""));
  }

  @Test
  public void testMatchEdge1() throws Exception {
    assertEquals("1 a 2", StringUtils.match("{} a {}", 1, 2));
  }

  @Test
  public void testMatchEdge2() throws Exception {
    assertEquals("a 2", StringUtils.match("a {}", 2));
  }

  @Test
  public void testMatchEdge3() throws Exception {
    assertEquals("1 a", StringUtils.match("{} a", 1));
  }

  @Test
  public void testMatchEdge4() throws Exception {
    assertEquals("1", StringUtils.match("{}", "1"));
  }

  @Test
  public void testMatchCommon1() throws Exception {
    assertEquals("Hello, World!", StringUtils.match("Hello, {}!", "World"));
  }

  @Test
  public void testMatchCommon2() throws Exception {
    assertEquals("1 Hello, World!", StringUtils.match("{} Hello, {}!", 1, "World"));
  }

  @Test
  public void testMatchCommon3() throws Exception {
    assertEquals("Hello, Mr World!", StringUtils.match("Hello, {} {}!", "Mr", "World"));
  }
}