package com.genequery.commons.search;

import com.genequery.commons.dao.ModulesDAO;
import com.genequery.commons.dao.ModulesGmtDAO;
import com.genequery.commons.models.DataSet;
import com.genequery.commons.models.Species;
import com.genequery.commons.utils.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created by Arbuzov Ivan.
 */
public class FisherSearcherTest {

  private static DataSet mmDataSet;

  private static String resourcePath =
      Thread.currentThread().getContextClassLoader().getResource("").getPath();

  @BeforeClass
  public static void loadData() throws IOException {
    ModulesDAO dao = new ModulesGmtDAO(Species.MOUSE, Paths.get(resourcePath , "mm_modules.gmt"));
    mmDataSet = new DataSet(Species.MOUSE, dao.getAllModules());
  }

  @Test
  public void sanityTest() throws IOException {
    String requestLine = new String(Files.readAllBytes(Paths.get(resourcePath + "request.txt")));
    long[] query = StringUtils.parseEntrezGenes(requestLine, " ");
    Properties context = new Properties();
    context.put(FisherSearcher.DB_VERSION, 2013);
    context.put(FisherSearcher.USE_TRUE_GSE_SIZE, true);
    // TODO separate test for context
    List<SearchResult> results = FisherSearcher.search(mmDataSet, query, 0.01, context);
    Collections.sort(results);

    assertEquals(2, results.size());
    assertEquals("GSE4066", results.get(0).getGse());
    assertEquals("GSE4066", results.get(1).getGse());

    SearchResult GSE4066 = results.get(1);
    assertEquals(4, GSE4066.getModuleNumber());
    assertEquals(155, GSE4066.getIntersectionSize());
    assertEquals(654, GSE4066.getModuleSize());
    assertEquals(Double.NEGATIVE_INFINITY, GSE4066.getLogEmpiricalPvalue(), 0.01);
  }

}