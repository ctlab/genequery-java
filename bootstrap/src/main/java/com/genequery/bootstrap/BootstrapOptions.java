package com.genequery.bootstrap;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * Created by Arbuzov Ivan.
 */
public class BootstrapOptions {
  public static String WITH_P_VALUES = "wpv";
  public static String TRUE_GSE_SIZE = "tgs";
  public static String PARTITION = "p";
  public static String FREQUENCY_BOUNDS = "b";
  public static String FIT = "f";
  public static String FIRST_N_MODULES = "n";
  public static String OUTPUT_FILE = "o";
  public static String DATA_PATH = "dp";
  public static String SPECIES = "s";
  public static String THREADS = "t";

  public static Option partition = OptionBuilder
    .withArgName("PATH")
    .withLongOpt("partition")
    .hasArgs(1)
    .isRequired(true)
    .withDescription("Path to partition file.")
    .create(BootstrapOptions.PARTITION);

  public static Option withPvalues = OptionBuilder
    .withLongOpt("with-p-values")
    .hasArg(false)
    .isRequired(false)
    .withDescription("Print p-values to output.")
    .create(BootstrapOptions.WITH_P_VALUES);

  public static Option freqBounds = OptionBuilder
    .withLongOpt("freq-bounds")
    .withArgName("bound_1,...,bound_n")
    .hasArgs()
    .withValueSeparator(',')
    .isRequired(false)
    .withDescription("Use only genes which frequency lay in [low, high]. Must be comma-separated.")
    .create(BootstrapOptions.FREQUENCY_BOUNDS);

  public static Option useTrueGseSize = OptionBuilder
    .withLongOpt("true-gse-size")
    .withDescription("Use true gse size instead of 6k.")
    .create(BootstrapOptions.TRUE_GSE_SIZE);

  public static Option threads = OptionBuilder
    .withLongOpt("threads")
    .withArgName("NUM")
    .hasArgs(1)
    .isRequired(false)
    .withDescription("Allowed thread count.")
    .create(BootstrapOptions.THREADS);

  public static Option species = OptionBuilder
    .withLongOpt("species")
    .withArgName("mm or hs [or rt in future]")
    .hasArgs(1)
    .isRequired(true)
    .withDescription("Species.")
    .create(BootstrapOptions.SPECIES);


}
