package cl.magal.asistencia.ui.obras;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.Obra;
import cl.magal.asistencia.services.ObrasService;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@VaadinView(value=Obras.NAME)
@Scope("prototype")
@Component
public class Obras extends VerticalLayout implements View {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8616320162970295709L;
	
	private transient Logger logger = LoggerFactory.getLogger(Obras.class);
	
	public static final String NAME = "obras";
	
	BeanItemContainer<Obra> container = new BeanItemContainer<Obra>(Obra.class);
	
	public Obras(){
		
		logger.debug("obras");
		
		setSizeFull();
		
		Table table = new Table();
		table.setContainerDataSource(container);
		table.setSizeFull();
		addComponent(table);
		
	}
	
	@Autowired
	private transient ObrasService service;
	
	@PostConstruct
	private void init(){
		
		Page<Obra> page = service.findAllObra(new PageRequest(0, 20));
		container.addAll(page.getContent());
	}

	@Override
	public void enter(ViewChangeEvent event) {
		logger.debug("enter");
		

	}

}
