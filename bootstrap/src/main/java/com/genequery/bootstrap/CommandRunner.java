package com.genequery.bootstrap;

import com.genequery.bootstrap.commands.Command;
import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Created by Arbuzov Ivan.
 */
public class CommandRunner {

  public static void main(String[] args) throws Exception {
    CommandLineParser parser = new GnuParser();
    Options options = new Options();

    Option help = new Option("h", "help", false, "Show help");
    options.addOption(help);
    HelpFormatter formatter = new HelpFormatter();

    if (args.length == 0) {
      formatter.printHelp("this", options );
      return;
    }
    CommandLine commandLine = parser.parse(options, Arrays.copyOfRange(args, 0, 1));

    if (commandLine.hasOption("h")) {
      formatter.printHelp("bootstrap.jar <command name>", options );
      return;
    }
    String className = "com.genequery.bootstrap.commands." + args[0] + "Command";
    Command cmd = (Command)Class.forName(className)
      .getConstructor()
      .newInstance();
    cmd.execute(Arrays.copyOfRange(args, 1, args.length));
  }
}
