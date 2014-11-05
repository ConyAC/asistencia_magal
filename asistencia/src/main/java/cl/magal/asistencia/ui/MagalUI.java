package cl.magal.asistencia.ui;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import ru.xpoft.vaadin.DiscoveryNavigator;
import ru.xpoft.vaadin.SpringVaadinServlet;
import cl.magal.asistencia.entities.Obra;
import cl.magal.asistencia.services.FichaService;
import cl.magal.asistencia.ui.fichaobra.FichaObra;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component
@Scope("prototype")
@SuppressWarnings("serial")
@Title("Asistencia Magal")
@Theme("valo")
public class MagalUI extends UI implements ErrorHandler {
	
	private transient Logger logger = LoggerFactory.getLogger(MagalUI.class);

	@WebServlet(value = "/*", asyncSupported = true, 
			initParams = { 
			@WebInitParam(name = "beanName", value = "magalUI")})
	@VaadinServletConfiguration(productionMode = false, ui = MagalUI.class, widgetset = "cl.magal.asistencia.ui.AppWidgetSet")
	public static class Servlet extends SpringVaadinServlet {
	}
	
	@Autowired
	private transient FichaService service;

	@Autowired
    private transient ApplicationContext applicationContext;
	
	public static final String PERSISTENCE_UNIT = "persistenceUnit";
	
	CssLayout root,menu;
	VerticalLayout menuLayout;
	HorizontalLayout bodyLayout;
	DiscoveryNavigator navigator = null ;

	@Override
	protected void init(final VaadinRequest request) {
		
		logger.debug("");
		
		VaadinSession.getCurrent().setErrorHandler(this);
		
		Obra obra = new Obra();
		obra.setNombre("Obra1");
		obra.setDireccion("Dire");
		
		service.saveObra(obra);
		
		root = new CssLayout();
		setContent(root);
        root.addStyleName("root");
        root.setSizeFull();
        
        bodyLayout = new HorizontalLayout();
		bodyLayout.setSizeFull();
		
		CssLayout content = new CssLayout();
		content.setSizeFull();
		content.addStyleName("view-content");
		
		navigator = new DiscoveryNavigator(UI.getCurrent(), content);
		
		Obra dbobra = service.findObra(obra.getId());
		
		content.addComponent(new Label(dbobra.getId()+" --------- "+dbobra.getNombre()));
		root.addComponent(content);
		
		navigator.navigateTo(FichaObra.NAME);
	}

	public void error(com.vaadin.server.ErrorEvent event) {
		// TODO Auto-generated method stub
		
	}

}
