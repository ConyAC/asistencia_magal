package cl.magal.asistencia.ui;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

/**
 * Label con ancho indefinido y immediate true
 * @author Pablo
 *
 */
public class UndefinedWidthLabel extends Label {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1430020945283198794L;
	
	public UndefinedWidthLabel(){
		super();
		init();
	}
	
	
	public UndefinedWidthLabel(String value) {
		super(value);
		init();
	}


	public UndefinedWidthLabel(String value, ContentMode contentMode) {
		super(value,contentMode);
		init();
	}


	private void init(){
		setWidthUndefined();
		setImmediate(true);
	}

}
