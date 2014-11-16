package cl.magal.asistencia.ui.workerfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.Obra;
import cl.magal.asistencia.services.ObrasService;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

@VaadinView(value=WorkerFileView.NAME)
@Scope("prototype")
@Component
public class WorkerFileView extends VerticalLayout implements View {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7438608047589376668L;
	public static final String NAME = "fichas";
	
	@Autowired
	private transient ObrasService service;

	
	@Override
	public void enter(ViewChangeEvent event) {
		
		Obra obra = new Obra();
		obra.setNombre("Obra1");
		obra.setDireccion("Dire");
		
		service.saveObra(obra);
		

	}
}
