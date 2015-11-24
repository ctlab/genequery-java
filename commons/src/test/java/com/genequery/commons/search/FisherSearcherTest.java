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
    List<SearchResult> results = FisherSearcher.search(mmDataSet, query, 0.01);
    Collections.sort(results);

    assertEquals(4, results.size());
    assertEquals("GSE46356", results.get(0).getGse());
    assertEquals("GSE23508", results.get(1).getGse());

    SearchResult GSE4066 = results.get(2);
    assertEquals("GSE4066", GSE4066.getGse());
    assertEquals(18, GSE4066.getModuleNumber());
    assertEquals(19, GSE4066.getIntersectionSize());
    assertEquals(55, GSE4066.getModuleSize());
    assertEquals(-157.22d, GSE4066.getLogEmpiricalPvalue(), 0.01);
  }

}