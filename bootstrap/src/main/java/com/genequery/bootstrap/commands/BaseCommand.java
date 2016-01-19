package com.genequery.bootstrap.commands;

import com.genequery.bootstrap.BootstrapOptions;
import com.genequery.commons.models.Species;
import org.apache.commons.cli.*;

import java.util.Arrays;

/**
 * Created by Arbuzov Ivan.
 */
public abstract class BaseCommand implements Command {
  @Override
  public void execute(String[] args) throws Exception {
    if (args.length == 0) {
      printHelp();
      return;
    }

    CommandLine commandLine;
    try {
      commandLine = getCommandLine(args);
    } catch (ParseException e) {
      printHelp();
      if (!"-h".equals(args[0]) && !"--help".equals(args[0])) {
        e.printStackTrace(System.out);
      }
      return;
    }
    if (commandLine.hasOption("h")) {
      printHelp();
      return;
    }

    _execute(commandLine);
  }

  protected abstract void _execute(CommandLine commandLine) throws Exception;

  private CommandLine getCommandLine(String[] args) throws ParseException {
    CommandLineParser parser = new GnuParser();
    return parser.parse(getAllOptions(), args);
  }

  private Options getAllOptions() {
    Options options = getCustomOptions();

    options.addOption("h", "help", false, "Show help.");
    options.addOption(BootstrapOptions.species);
    options.addOption(
      OptionBuilder
        .withArgName("PATH")
        .withLongOpt("data-path")
        .hasArgs(1)
        .isRequired(true)
        .withDescription("Path to folder with files for the species: <species>.modules.gmt, <species>.entrez.txt, <species>.freq.entrez.txt.")
        .create(BootstrapOptions.DATA_PATH)
    );
    options.addOption(
      OptionBuilder
        .withArgName("STRING")
        .withLongOpt("out-file-name")
        .hasArgs(1)
        .isRequired(true)
        .withDescription("Output file name.")
        .create(BootstrapOptions.OUTPUT_FILE)
    );
    options.addOption(
      OptionBuilder
        .withLongOpt("fit")
        .withArgName("NUM,...,NUM")
        .hasArgs()
        .withValueSeparator(',')
        .isRequired(true)
        .withDescription("Fit parameters.")
        .create(BootstrapOptions.FIT)
    );

    options.addOption(
      OptionBuilder
        .withLongOpt("first-n-modules")
        .withArgName("NUM")
        .hasArg()
        .isRequired(true)
        .withDescription("Number of modules to be taken from the beginning (smallest p-value) for p-value.")
        .create(BootstrapOptions.FIRST_N_MODULES)
    );

    options.addOption(BootstrapOptions.useTrueGseSize);
    options.addOption(BootstrapOptions.threads);
    options.addOption(BootstrapOptions.partition);
    options.addOption(BootstrapOptions.withPvalues);
    options.addOption(BootstrapOptions.freqBounds);

    return options;
  }

  private void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(this.getClass().getSimpleName(), getAllOptions());
  }

  protected int getThreadCount(CommandLine commandLine) {
    return Integer.parseInt(commandLine.getOptionValue(BootstrapOptions.THREADS, "4"));
  }

  protected int[] getFits(CommandLine commandLine) {
    return Arrays.stream(commandLine.getOptionValues(BootstrapOptions.FIT)).mapToInt(Integer::parseInt).toArray();
  }

  protected Species getSpecies(CommandLine commandLine) {
    return Species.fromString(commandLine.getOptionValue(BootstrapOptions.SPECIES));
  }

  protected String getPathToData(CommandLine commandLine) {
    return commandLine.getOptionValue(BootstrapOptions.DATA_PATH);
  }

  protected String getOutFileName(CommandLine commandLine) {
    return commandLine.getOptionValue(BootstrapOptions.OUTPUT_FILE);
  }

  protected int getFirstN(CommandLine commandLine) {
    return Integer.parseInt(commandLine.getOptionValue(BootstrapOptions.FIRST_N_MODULES));
  }

  protected boolean useTrueGseSize(CommandLine commandLine) {
    return commandLine.hasOption(BootstrapOptions.TRUE_GSE_SIZE);
  }

  protected Options getCustomOptions() {
    return new Options();
  }
}
