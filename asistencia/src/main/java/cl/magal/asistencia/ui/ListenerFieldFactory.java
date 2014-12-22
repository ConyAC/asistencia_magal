package cl.magal.asistencia.ui;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

public class ListenerFieldFactory extends DefaultFieldFactory {
	
	Property.ValueChangeListener listener = null;
	public ListenerFieldFactory(Property.ValueChangeListener listener){
		this.listener = listener;
	}
	
    public Field createField(Container container,
                             Object itemId,
                             Object propertyId,
                             com.vaadin.ui.Component uiContext) {
        Field field = super.createField(container, itemId, propertyId, uiContext);
        if(field instanceof TextField){
			if(!((TextField)field).isReadOnly()){
				((TextField)field).addValueChangeListener(listener);
				((TextField)field).setImmediate(true);
			}
		}
        
        return field;
    }
}
