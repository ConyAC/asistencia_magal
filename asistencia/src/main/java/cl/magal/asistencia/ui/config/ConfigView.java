package cl.magal.asistencia.ui.config;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

@VaadinView(value=ConfigView.NAME)
@Scope("prototype")
@Component
public class ConfigView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8538300514577423280L;
	
	public static final String NAME = "configuraciones";

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
