package com.genequery.commons.dao;

import com.genequery.commons.models.Module;
import com.genequery.commons.models.ModuleName;
import com.genequery.commons.models.Species;
import com.genequery.commons.utils.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Here GMT (Gene Matrix Transposed) is file format such that each row has the following format:
 *
 * <GSE_GPL#moduleNumberIsTheGSE> entrezID_1,entrezID_2,entrezID_3,...
 *
 * where first token (gse_gpl#moduleNumberIsTheGSE) is separated from the rest of the line with the tab symbol,
 * and entrez IDs are comma-separated.
 *
 * E.g.:
 * GSE30678_GPL570#11 100507507,280636,92241,81566,55667,55619,54842,51411,50515,26511,26092,23608,10962,10955
 *
 *
 * Created by Arbuzov Ivan.
 */
public class ModulesGmtDAO implements ModulesDAO {

  private Path path;
  private Species species;

  public ModulesGmtDAO(Species species, String path) {
    this.path = Paths.get(path);
    this.species = species;
  }

  private Module buildModuleByGMTLine(String line) {
    String[] moduleAndGenes = line.split("\\t");
    long[] resGenes = StringUtils.parseEntrezGenes(moduleAndGenes[1], ",");
    Arrays.sort(resGenes);
    return new Module(new ModuleName(moduleAndGenes[0]), species, resGenes);
  }

  @Override
  public List<Module> getAllModules() throws Exception {
    try (Stream<String> lines = Files.lines(path)) {
      return lines.map(this::buildModuleByGMTLine).collect(Collectors.toList());
    }
  }
}
