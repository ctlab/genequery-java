package com.genequery.bootstrap.commands;

/**
 * Created by Arbuzov Ivan.
 */
public interface Command {
  void execute(String[] args) throws Exception;
}
