package com.genequery.commons.dao;

import com.genequery.commons.models.Module;
import com.genequery.commons.models.Species;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Arbuzov Ivan.
 */
public class ModulesGmtDAOTest {

  private static String resourcePath =
      Thread.currentThread().getContextClassLoader().getResource("").getPath();

  @Test
  public void testGetAllModulesBasic() throws Exception {
    List<Module> modules = new ModulesGmtDAO(Species.HUMAN, Paths.get(resourcePath , "test.gmt")).getAllModules();
    assertEquals(modules.get(0).getName().full(), "GSE4748_GPL570#0");
    assertEquals(modules.get(0).getGenes().length, 10);
    assertEquals(modules.get(0).getSpecies(), Species.HUMAN);
    assertEquals(modules.size(), 3);
  }
}