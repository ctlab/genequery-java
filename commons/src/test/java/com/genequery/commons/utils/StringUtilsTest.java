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
    StringUtils.fmt(null, 1, 2, 3);
  }

  @Test
  public void testMatchNullException2() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.fmt("", null);
  }

  @Test
  public void testMatchExceptionTooFewArgs1() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.fmt("{}a{}b{}", 1, 2);
  }

  @Test
  public void testMatchExceptionTooFewArgs2() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.fmt("a{}b{}", 1);
  }

  @Test
  public void testMatchExceptionTooFewArgs3() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.fmt("a{}b");
  }

  @Test
  public void testMatchExceptionTooManyArgs1() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.fmt("{}", 1, 2);
  }

  @Test
  public void testMatchExceptionTooManyArgs2() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.fmt("a{}", 1, 2);
  }

  @Test
  public void testMatchExceptionTooManyArgs3() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.fmt("a{}b", 1, 2);
  }

  @Test
  public void testMatchExceptionTooManyArgs4() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.fmt("{}a{}b", 1, 2, 3);
  }

  @Test
  public void testMatchExceptionTooManyArgs5() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.fmt("{}a{} b\t{}", 1, 2, 3, 4);
  }

  @Test
  public void testMatchExceptionTooManyArgs6() throws Exception {
    exception.expect(IllegalArgumentException.class);
    StringUtils.fmt("{}{}", 1, 2, 3);
  }

  @Test
  public void testMatchNoArgs1() throws Exception {
    assertEquals("Hello!", StringUtils.fmt("Hello!"));
  }

  @Test
  public void testMatchNoArgs2() throws Exception {
    assertEquals("", StringUtils.fmt(""));
  }

  @Test
  public void testMatchEdge1() throws Exception {
    assertEquals("1 a 2", StringUtils.fmt("{} a {}", 1, 2));
  }

  @Test
  public void testMatchEdge2() throws Exception {
    assertEquals("a 2", StringUtils.fmt("a {}", 2));
  }

  @Test
  public void testMatchEdge3() throws Exception {
    assertEquals("1 a", StringUtils.fmt("{} a", 1));
  }

  @Test
  public void testMatchEdge4() throws Exception {
    assertEquals("1", StringUtils.fmt("{}", "1"));
  }

  @Test
  public void testMatchCommon1() throws Exception {
    assertEquals("Hello, World!", StringUtils.fmt("Hello, {}!", "World"));
  }

  @Test
  public void testMatchCommon2() throws Exception {
    assertEquals("1 Hello, World!", StringUtils.fmt("{} Hello, {}!", 1, "World"));
  }

  @Test
  public void testMatchCommon3() throws Exception {
    assertEquals("Hello, Mr World!", StringUtils.fmt("Hello, {} {}!", "Mr", "World"));
  }
}