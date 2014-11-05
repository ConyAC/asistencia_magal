package cl.magal.asistencia.ui.fichaobra;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.Obra;
import cl.magal.asistencia.services.FichaService;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@VaadinView(value=FichaObra.NAME)
@Scope("prototype")
@Component
public class FichaObra extends VerticalLayout implements View {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8616320162970295709L;
	
	public static final String NAME = "ficha_obra";
	
	BeanItemContainer<Obra> container = new BeanItemContainer<Obra>(Obra.class);
	
	public FichaObra(){
		
		Table table = new Table();
		table.setContainerDataSource(container);
		table.setSizeFull();
		addComponent(table);
		
	}
	
	@Autowired
	FichaService service;
	
	@PostConstruct
	private void init(){
		
		Page<Obra> page = service.findAllObra(new PageRequest(0, 20));
		container.addAll(page.getContent());
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
		

	}

}
