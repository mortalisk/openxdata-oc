package org.openxdata.server.admin.model.paging;

import java.io.Serializable;
import java.util.List;

public class PagingLoadResult<Data> implements Serializable {

    private static final long serialVersionUID = -6035172737476394396L;
    
	int offset = 0;
	int length = 0;
	int totalLength = 0;
	
	List<Data> data;
	
	public PagingLoadResult() {
		// default constructor
	}
	
	/**
	 * Constructor used to create a new instance of <tt>PagingLoadResult</tt>.
	 *
	 * @param data Data returned from the server.
	 * @param offset The index at which to start returning results from.
	 * @param length Size of Data that has been returned.
	 */
	public PagingLoadResult(List<Data> data, int offset, int length) {
		this.data = data;
		this.offset = offset;
		this.length = length;
	}
	
	/**
	 * Constructor used to create a new instance of <tt>PagingLoadResult</tt>.
	 *
	 * @param data Data returned from the server.
	 * @param offset The index at which to start returning results from.
	 * @param length Total number of items returned in a particular page.
	 * @param totalLength Total number of all items fetched.
	 */
	public PagingLoadResult(List<Data> data, int offset, int length, int totalLength) {
		this(data, offset, length);
		this.totalLength = totalLength;
	}

	public int getOffset() {
    	return offset;
    }

	public void setOffset(int offset) {
    	this.offset = offset;
    }

	public int getLength() {
    	return length;
    }

	public void setLength(int length) {
    	this.length = length;
    }

	public List<Data> getData() {
    	return data;
    }

	public void setData(List<Data> data) {
    	this.data = data;
    }

	public int getTotalLength() {
    	return totalLength;
    }

	public void setTotalLength(int totalLength) {
    	this.totalLength = totalLength;
    }
}
