package com.genequery.rest.endpoints;

/**
 * Created by Arbuzov Ivan.
 */

import com.genequery.commons.models.Species;
import com.genequery.commons.utils.StringUtils;
import com.genequery.rest.DataSetHolder;
import com.genequery.rest.FisherSearcher;
import com.genequery.rest.SearchResult;
import com.genequery.rest.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Path("/fisher")
public class FisherSearcherEndPoint {

  private static final Logger LOG = LoggerFactory.getLogger(FisherSearcherEndPoint.class);

  @GET
  @Path("search_sorted")
  @Produces(MediaType.TEXT_PLAIN)
  public Response test(
      @QueryParam("species") String species,
      @QueryParam("genes") String genes) {
    // TODO normal response error messages
    LOG.info("Request accepted: {}, {}", species, genes);
    Species sp;
    long[] query;
    try {
      if (species == null) throw new IllegalArgumentException("Species isn't passed");
      if (genes == null) throw new IllegalArgumentException("Genes ain't passed");
      sp = Species.fromString(species);
      String[] stringGenes = genes.split(" ");
      query = new long[stringGenes.length];
      for (int i = 0; i < stringGenes.length; i++) {
        query[i] = Long.parseLong(stringGenes[i]);
      }
    } catch (Exception e) {
      LOG.error("Illegal params.", e);
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }

    String result;
    try {
      long startTime = System.currentTimeMillis();

      List<SearchResult> results = FisherSearcher.search(
          DataSetHolder.getDataSet(sp),
          query,
          ServerProperties.maxEmpiricalPvalue()
      );
      Collections.sort(results);
      List<String> best = results.stream().map(SearchResult::getDataToStringLine).collect(Collectors.toList());
      result = StringUtils.join(best, "\n");
      LOG.info("Calculation time: {} ms", System.currentTimeMillis() - startTime);
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
    return Response.status(Response.Status.OK).entity(result).build();
  }
}
