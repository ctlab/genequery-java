package com.genequery.bootstrap.commands;

/**
 * Created by Arbuzov Ivan.
 */
public class BootstrapByRequestLengthCommand implements Command {
  @Override
  public void execute(String[] args) {
    System.out.println("Hello, world!" + args.length);
  }
}
