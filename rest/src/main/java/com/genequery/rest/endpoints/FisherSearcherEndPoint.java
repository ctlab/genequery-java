package com.genequery.rest.endpoints;

/**
 * Created by Arbuzov Ivan.
 */

import com.genequery.commons.models.Species;
import com.genequery.rest.DataSetHolder;
import com.genequery.commons.search.FisherSearcher;
import com.genequery.commons.search.SearchResult;
import com.genequery.rest.ServerProperties;
import com.genequery.rest.xmlmodels.SearchResultXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Path("/fisher")
public class FisherSearcherEndPoint {

  private static final Logger LOG = LoggerFactory.getLogger(FisherSearcherEndPoint.class);

  @GET
  @Path("search_sorted")
  @Produces(MediaType.APPLICATION_JSON)
  public List<SearchResultXML> process(
      @QueryParam("species") String species,
      @QueryParam("genes") String genes) {

    LOG.info("Request accepted: {}, {}", species, genes);

    Species sp;
    long[] query;

    if (species == null || "".equals(species)) throw new BadRequestException("Species isn't passed");
    if (genes == null || "".equals(genes)) throw new BadRequestException("Genes aren't passed");

    try {
      sp = Species.fromString(species);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Incorrect species is specified: " + species);
    }

    String[] stringGenes = genes.split(" ");
    query = new long[stringGenes.length];
    for (int i = 0; i < stringGenes.length; i++) {
      try {
        query[i] = Long.parseLong(stringGenes[i]);
      } catch (NumberFormatException e) {
        throw new BadRequestException("Can't parse entrez ID: " + stringGenes[i]);
      }
    }

    Properties context = new Properties();
    context.put(FisherSearcher.USE_TRUE_GSE_SIZE, true);

    List<SearchResult> results;
    long startTime = System.currentTimeMillis();

    results = FisherSearcher.search(
      DataSetHolder.getDataSet(sp),
      query,
      ServerProperties.maxEmpiricalPvalue(),
      context
    );
    Collections.sort(results);

    LOG.info("Calculation time: {} ms, {} results was found.", System.currentTimeMillis() - startTime, results.size());
    return results.stream().map(SearchResultXML::createFromSearchResult).collect(Collectors.toList());
  }
}
