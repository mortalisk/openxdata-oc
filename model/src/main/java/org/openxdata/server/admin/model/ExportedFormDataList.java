package org.openxdata.server.admin.model;

import java.util.ArrayList;
import java.util.List;

import net.sf.gilead.pojo.gwt.LightEntity;

public class ExportedFormDataList extends LightEntity {

    private static final long serialVersionUID = -1839643565661087763L;

    private Integer fromIndex;
    private Integer toIndex;
    private Integer totalSize;

    private List<ExportedFormData> exportedFormDataList = new ArrayList<ExportedFormData>();

    public ExportedFormDataList() {
    }

    public Integer getFromIndex() {
        return fromIndex;
    }

    public void setFromIndex(Integer fromIndex) {
        this.fromIndex = fromIndex;
    }

    public Integer getToIndex() {
        return toIndex;
    }

    public void setToIndex(Integer toIndex) {
        this.toIndex = toIndex;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }

    public List<ExportedFormData> getExportedFormData() {
        return exportedFormDataList;
    }

    public void setExportedFormData(List<ExportedFormData> exportedFormData) {
        this.exportedFormDataList = exportedFormData;
    }
    
    public void addExportedFormData(ExportedFormData exportedFormData) {
        this.exportedFormDataList.add(exportedFormData);
    }
}
