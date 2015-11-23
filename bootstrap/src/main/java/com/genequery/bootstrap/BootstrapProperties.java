package com.genequery.bootstrap;

import com.genequery.commons.models.Species;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;

import static com.genequery.commons.utils.StringUtils.fmt;
import static com.genequery.commons.utils.Utils.checkNotNull;

/**
 * Created by Arbuzov Ivan.
 */
public class BootstrapProperties {

  public static final String THREAD_COUNT = "thread.count";
  public static final String SAMPLES_PER_QUERY_SIZE = "samples.per.query.size";
  public static final String OUTPUT_PATH = "output.path";
  public static final String ENTREZ_IDS_FILENAME = "entrez.ids";
  public static final String GMT_MODULES_FILENAME = "gmt.modules";
  public static final String SPECIES = "species";
  public static final String REQUEST_LENGTHS_PARTITION_PATH = "request.lengths.partition.path";

  public static int getThreadCount() {
    return Integer.parseInt(System.getProperty(THREAD_COUNT, "6"));
  }

  public static int getSamplesPerQuerySize() {
    return Integer.parseInt(System.getProperty(SAMPLES_PER_QUERY_SIZE, "200"));
  }

  @NotNull
  public static String getOutputPath() {
    return System.getProperty(OUTPUT_PATH, fmt("bootstrapping_{}.txt", (int)(System.currentTimeMillis() / 1e6)));
  }

  @Nullable
  public static String getEntrezIdsFilename() {
    return System.getProperty(ENTREZ_IDS_FILENAME);
  }

  @Nullable
  public static String getGmtModulesFilename() {
    return System.getProperty(GMT_MODULES_FILENAME);
  }

  @Nullable
  public static String getRequestLengthsPartitionPath() {
    return System.getProperty(REQUEST_LENGTHS_PARTITION_PATH);
  }

  @NotNull
  public static Species getSpecies() {
    return Species.fromString(checkNotNull(System.getProperty(SPECIES), "Species isn't specified"));
  }

  public static Properties asProperties() {
    Properties p = new Properties();

    p.setProperty(THREAD_COUNT, String.valueOf(getThreadCount()));
    p.setProperty(SAMPLES_PER_QUERY_SIZE, String.valueOf(getSamplesPerQuerySize()));
    p.setProperty(OUTPUT_PATH, String.valueOf(getOutputPath()));
    p.setProperty(ENTREZ_IDS_FILENAME, String.valueOf(getEntrezIdsFilename()));
    p.setProperty(GMT_MODULES_FILENAME, String.valueOf(getGmtModulesFilename()));
    p.setProperty(SPECIES, String.valueOf(getSpecies()));
    p.setProperty(REQUEST_LENGTHS_PARTITION_PATH, String.valueOf(getRequestLengthsPartitionPath()));

    return p;
  }
}
