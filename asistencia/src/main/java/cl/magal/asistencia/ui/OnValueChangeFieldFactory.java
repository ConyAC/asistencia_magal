package cl.magal.asistencia.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;

public class OnValueChangeFieldFactory extends DefaultFieldFactory {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5555662194225120210L;

	transient Logger logger = LoggerFactory.getLogger(OnValueChangeFieldFactory.class);

	int tipo = 0;
	public OnValueChangeFieldFactory(int tipo){
		this.tipo = tipo;
	}

	public Field<?> createField(final Container container,
			final Object itemId,
			Object propertyId,
			com.vaadin.ui.Component uiContext) {
		final Field<?> field = super.createField(container, itemId, propertyId, uiContext);
		if(field instanceof TextField){
			if(!((TextField)field).isReadOnly()){
				((TextField)field).setImmediate(true);
			}
		}

		if(field instanceof DateField){
			if(!((DateField)field).isReadOnly()){
				((DateField)field).setImmediate(true);
			}
		}
		if(!field.isReadOnly()){
			final Property.ValueChangeListener listener = new Property.ValueChangeListener() {

				/**
				 * 
				 */
				 private static final long serialVersionUID = 1L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					try{
						doOnChanged(itemId);
					}catch(Exception e){
						Notification.show("Error al guardar el elemento",Type.ERROR_MESSAGE);
						logger.error("Error al guardar el elemento de impuesto",e);
					}

				}
			};
			field.addValueChangeListener(listener);
		}
		((AbstractField<?>)field).setImmediate(true);

		return field;
	}

	List<OnValueChangeListener> listeners = new ArrayList<OnValueChangeListener>();

	public void addListener(OnValueChangeListener listener) {
		listeners.add(listener);
	}

	public void removeListener(OnValueChangeListener listener) {
		listeners.remove(listener);
	}

	public interface OnValueChangeListener extends Serializable {
		public void onValueChange(Object itemId);
	}

	private void doOnChanged(Object itemId){
		for(OnValueChangeListener listener : listeners ){
			listener.onValueChange(itemId);
		}
	}

}
