package com.genequery.rest.xmlmodels;

/**
 * Created by Arbuzov Ivan.
 */
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class NullIfMinValueDoubleAdapter extends XmlAdapter<Double, Double>{

  @Override
  public Double unmarshal(Double v) throws Exception {
    return v;
  }

  @Override
  public Double marshal(Double v) throws Exception {
    if(Double.NEGATIVE_INFINITY == v) {
      return -1e10;
    }
    return v;
  }

}