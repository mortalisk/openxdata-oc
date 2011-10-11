package org.openxdata.client.util;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;

/**
 * A <code>CSVReader</code> implementation that reads CSV data using a
 * <code>ModelType</code> definition and returns a list load result instance.
 * 
 * @param <D> the type of list load result being returned by the reader
 */
public class CSVLoadResultReader<D extends ListLoadResult<? extends ModelData>> extends CSVReader<D> {
  /**
   * Creates a new reader.
   * 
   * @param modelType the model type definition
   */
  public CSVLoadResultReader(ModelType modelType) {
    super(modelType);
  }

  @Override
  protected Object createReturnData(Object loadConfig, List<ModelData> records, int totalCount) {
    return newLoadResult(loadConfig, records);
  }

  /**
   * Template method that provides load result.
   * 
   * @param models the models
   * @return the load result
   */
  protected ListLoadResult<ModelData> newLoadResult(Object loadConfig, List<ModelData> models) {
    return new BaseListLoadResult<ModelData>(models);
  }
}
