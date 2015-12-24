package com.genequery.bootstrap.commands;

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
    options.addOption(
      OptionBuilder
        .withLongOpt("threads")
        .withArgName("NUM")
        .hasArgs(1)
        .isRequired(false)
        .withDescription("Allowed thread count.")
        .create(BootstrapOptions.THREADS)
    );
    options.addOption(
      OptionBuilder
        .withLongOpt("species")
        .withArgName("mm or hs [or rt in future]")
        .hasArgs(1)
        .isRequired(true)
        .withDescription("Species.")
        .create(BootstrapOptions.SPECIES)
    );
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
        .withLongOpt("--out-file-name")
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

  protected Options getCustomOptions() {
    return new Options();
  }
}
