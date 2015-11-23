package com.genequery.commons.search;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arbuzov Ivan.
 */
@XmlRootElement(name = "processing_result")
public class SearchResultList {

  @NotNull
  @XmlElementWrapper(name = "results")
  @XmlElement(name = "result")
  private List<SearchResult> resultList;

  public SearchResultList() {
    resultList = new ArrayList<>();
  }

  public SearchResultList(@NotNull List<SearchResult> resultList) {
    this.resultList = resultList;
  }

  @NotNull
  public List<SearchResult> getResultList() {
    return resultList;
  }

  public void setResultList(@NotNull List<SearchResult> resultList) {
    this.resultList = resultList;
  }

  public void addResult(@NotNull SearchResult searchResult) {
    resultList.add(searchResult);
  }

  @NotNull
  public SearchResult getResult(int index) {
    return resultList.get(index);
  }
}
