package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import org.purc.purcforms.client.model.DynamicOptionDef;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.model.QuestionDef;

/**
 * Grid Cell Editor for Combo and List boxes
 * @author dagmar@cell-life.org.za
 */
public class ListCellEditor extends CellEditor {
    
    SimpleComboBox<String> combo = null;
    TimeField timeFld = null;
    ListField<SimpleListData> list = null;
    CheckBoxGroup checkGroup = null;
    QuestionDef questionDef = null;
    DynamicOptionDef dynOptionsDef = null;
    List<ListCellEditor> dynamicChildren = new ArrayList<ListCellEditor>();
    
    /**
     * Creates the ComboBoxCellEditor
     * @param field SimpleComboBox<String> used by this CellEditor
     */
    @SuppressWarnings("unchecked")
    public ListCellEditor(Field<? extends Object> field) {
        super(field);
        if (field instanceof SimpleComboBox) {
            combo = (SimpleComboBox<String>) field;
            combo.setTriggerAction(TriggerAction.ALL);
            combo.setEditable(false); 
            // FIXME: had to make the combo box uneditable because the auto validate didn't work and i was pushed for time to write a custom validator
            combo.setAutoValidate(true);
            combo.setTypeAhead(true);
        } else if (field instanceof ListField) {
            list = (ListField<SimpleListData>) field;
            list.setStore(new ListStore<SimpleListData>());
            list.setDisplayField("value");
        } else if (field instanceof CheckBoxGroup) {
            checkGroup = (CheckBoxGroup) field;
            checkGroup.setInEditor(true);
            checkGroup.setResizeFields(true);
            checkGroup.setOrientation(Orientation.VERTICAL);
        } else if (field instanceof TimeField){
        	timeFld = (TimeField)field;
        	timeFld.setTriggerAction(TriggerAction.ALL);

        }
    }

    /**
     * Set the options in the ComboBox - retrieved from the Question Definition
     * @param questionDef QuestionDef
     */
    public void setOptions(QuestionDef questionDef) {
        this.questionDef = questionDef;
        List<OptionDef> options = questionDef.getOptions();
        setOptions(options);
    }

    /**
     * Sets the specified options in the ComboBox
     * @param options List<OptionDef>
     */
    private void setOptions(List<OptionDef> options) {
        if (options != null && options.size() > 0) {
            for (OptionDef optionDef : options) {
                //System.out.println("Option : " + optionDef.getVariableName() + " - " + optionDef.getText());
                if (combo != null) {
                    combo.add(optionDef.getVariableName());
                } else if (list != null) {
                    list.getStore().add(new SimpleListData(optionDef.getVariableName(), optionDef.getText()));
                } else if (checkGroup != null) {
                	// hack!!! see: http://dev.cell-life.org/jira/browse/EMITSERVER-9
                    CheckBox check = new CheckBox() {
	                    @Override
	                    protected void onResize(int width, int height) {
	                        super.onResize(width, height);
	                        alignElements();
	                    }
                    };
                    check.setBoxLabel(optionDef.getText());
                    check.setId(optionDef.getVariableName());
                    checkGroup.add(check);
                }
            }
        }
    }
    
    /**
     * Sets the options in the ComboBox
     * @param options List of Strings
     */
    public void setOptions(String...options) {
        for (String option : options) {
            if (combo != null) {
                combo.add(option);
            } else if (list != null) {
                list.getStore().add(new SimpleListData(option, option));
            } else if (checkGroup != null) {
                CheckBox check = new CheckBox();  
                check.setBoxLabel(option);
                check.setId(option);
                checkGroup.add(check);            	
            }
        }
    }
    
    /**
     * Sets the options in the ComboBox for dynamic comboboxes
     * @param questionDef
     */
    public void setDynamicOptions(QuestionDef questionDef) {
        this.questionDef = questionDef;
        FormDef formDef = questionDef.getParentFormDef();
        QuestionDef parentQuestionDef = formDef.getDynamicOptionsParent(questionDef.getId());
        if (parentQuestionDef != null) {
            dynOptionsDef = formDef.getDynamicOptions(parentQuestionDef.getId());
            if (dynOptionsDef != null) {
                //parent.addDynamicChild(this);
                List<OptionDef> options = dynOptionsDef.getOptions(); // FIXME: only select options depending on parent values...
                setOptions(options);
            }
        }
    }

    @Override  
    public Object preProcessValue(Object value) {
        // find the actual value you want to return (i.e. variable name instead of display text)
    	if(timeFld != null){
    		if (value != null) {
    			java.sql.Time time = (java.sql.Time)value;
    			return timeFld.findModel(time);
    		}
    	}else if (combo != null) {
            if (value != null) {
                return combo.findModel(value.toString());
            }
        } else if (list != null) {
            if (value != null) {
                List<SimpleListData> selectedModels = new ArrayList<SimpleListData>();
                String[] values = ((String)value).split(" ");
                List<SimpleListData> models = list.getStore().getModels();
                for (String v : values) {
                    for (SimpleListData m : models) {
                        if (m.getKey().equals(v)) {
                            selectedModels.add(m);
                            break;
                        }
                    }
                }
                list.setSelection(selectedModels);
                list.getListView().getSelectionModel().setSelection(selectedModels);
                return list.getValue();
            } else {
                list.clear();
                // this is surely a bug, but it works to ensure that if there is no value, the previous selection is not re-used
                list.getListView().getSelectionModel().setSelection(new ArrayList<SimpleListData>());
            }
        } else if (checkGroup != null) {
        	if (value != null) {
        		for (Field<?> checkBox : checkGroup.getAll()) {
        			if (checkBox instanceof CheckBox) {
        				if ( ((String)value).contains(checkBox.getId()) ) {
        					((CheckBox) checkBox).setValue(true);
        				} else {
        					((CheckBox) checkBox).setValue(false);
        				}
        			}
        		}
        		return checkGroup.getValue();
        	} else {
        		checkGroup.clear();
        		for (Field<?> checkBox : checkGroup.getAll()) {
        			if (checkBox instanceof CheckBox) {
        				((CheckBox) checkBox).setValue(false);
        			}
        		}
        	}
        }
        return super.preProcessValue(value);
     }

     @Override  
     public Object postProcessValue(Object value) {
         if (timeFld != null) {
             if (value != null) {
            	 Time t = (Time) value;
            	 java.sql.Time sqlT = new java.sql.Time(t.getDate().getTime()); 
     			return sqlT;
             }
         } else if (combo != null) {
             if (value != null) {
                 return ((ModelData) value).get("value");
             }
         } else if (list != null) {
             StringBuilder sb = new StringBuilder();
             List<SimpleListData> selection = list.getSelection();
             if (selection != null) {
                 for (SimpleListData s : selection) {
                     sb.append(s.getKey());
                     sb.append(" ");
                 }
             }
             return sb.toString().trim();
         } else if (checkGroup != null) {
        	 StringBuilder sb = new StringBuilder();
             List<CheckBox> selection = checkGroup.getValues();
             if (selection != null) {
                 for (CheckBox s : selection) {
                     sb.append(s.getId());
                     sb.append(" ");
                 }
             }
             return sb.toString().trim();
         }
         return super.postProcessValue(value);
     }

    @Override
    public void completeEdit() {
        for (ListCellEditor child : dynamicChildren) {
            child.updateOptions(this);
        }
        super.completeEdit();
    }

    /**
     * Gets the QuestionDef backing this ComboBox CellEditor
     * @return QuestionDef, can be null
     */
    public QuestionDef getQuestionDef() {
        return questionDef;
    }
    
    /**
     * Register the specified dynamic list as a child of this
     * @param child ComboBoxCellEditor
     */
    public void addDynamicChild(ListCellEditor child) {
        dynamicChildren.add(child);
    }
    
    /**
     * Notifies the child dynamic list that it should get new option values
     * because the selected parent value has been changed.
     * @param parent ComboBoxCellEditor
     */
    public void updateOptions(ListCellEditor parent) {
        if (dynOptionsDef != null) {
            List<OptionDef> options = dynOptionsDef.getOptionList(parent.getQuestionDef().getId());
            setOptions(options);
        }
    }
}

/**
 * Stupid class to allow us to add Strings to a ListField Store
 * @author dagmar@cell-life.org.za
 */
class SimpleListData extends BaseModel { 
    private static final long serialVersionUID = 1570978281257316919L;
    public SimpleListData(String key, String data) {
        this.set("key", key);
        this.set("value", data);
    }
    public String getData() {
        return this.get("value");
    }
    public String getKey() {
        return this.get("key");
    }
    @Override
	public String toString() {
        return this.get("value");
    }
}