package cl.magal.asistencia.ui.constructionsite;
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;

public class EnhancedFieldGroupFieldFactory implements FieldGroupFieldFactory {
	 
	 /**
	 * 
	 */
	private static final long serialVersionUID = -1759943044170971743L;
	private FieldGroupFieldFactory fieldFactory = DefaultFieldGroupFieldFactory.get();
	 
	    @SuppressWarnings({ "rawtypes", "unchecked" })
	    @Override
	    public <T extends Field> T createField(Class<?> dataType, Class<T> fieldType) {
	 
	    	Field<?> field = fieldFactory.createField(dataType, fieldType);
	    	if(field instanceof AbstractTextField){
	    	    ((AbstractTextField)field).setNullRepresentation("");
	    	}
	    	return (T) field;
	    }
}
