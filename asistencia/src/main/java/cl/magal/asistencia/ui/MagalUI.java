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
import cl.magal.asistencia.ui.fichas.Fichas;
import cl.magal.asistencia.ui.login.Login;
import cl.magal.asistencia.ui.obras.Obras;
import cl.magal.asistencia.ui.usuarios.Usuarios;
import cl.magal.asistencia.util.Constantes;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
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
    private transient ApplicationContext applicationContext;
	
	public static final String PERSISTENCE_UNIT = "persistenceUnit";
	
	VerticalLayout root;
	MenuBar menuLayout;
	DiscoveryNavigator navigator = null ;

	@Override
	protected void init(final VaadinRequest request) {
		
		logger.debug("magalui init");
		
		VaadinSession.getCurrent().setErrorHandler(this);
		//raiz
//		root = new CssLayout();
		root = new VerticalLayout();
		setContent(root);
        root.addStyleName("root");
        root.setSizeFull();
        //menu
        drawMenu();
		
		CssLayout content = new CssLayout();
		content.setSizeFull();
		content.addStyleName("view-content");
		
		navigator = new DiscoveryNavigator(UI.getCurrent(), content);
		
		root.addComponent(content);
		root.setExpandRatio(content, 1.0F);
		
		
		
	}

	public void error(com.vaadin.server.ErrorEvent event) {
		
	}
	
	private MenuBar drawMenu() {
		
		menuLayout = new MenuBar();
		
		menuLayout.addItem("Obras", new Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				navigator.navigateTo(Obras.NAME);
				
			}
		});
		
		menuLayout.addItem("Fichas", new Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				navigator.navigateTo(Fichas.NAME);
				
			}
		});
		
		menuLayout.addItem("Usuarios", new Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				navigator.navigateTo(Usuarios.NAME);
				
			}
		});
		
		menuLayout.addItem("Salir", new Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				logOut();
				
			}
		});
		
		root.addComponent(menuLayout);
		root.setComponentAlignment(menuLayout,Alignment.TOP_RIGHT);
		
		return menuLayout;
	}
	
	public void logOut(){
//		SecurityContext context = (SecurityContext) VaadinSession.getCurrent().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
//		if( context != null ){
//			
//			Authentication authentication = (Authentication) context.getAuthentication();
//			logoutHandler.logout(VaadinRequestHolder.getRequest(), null , authentication);
//		}
//    	//deslogea
//    	VaadinSession.getCurrent().
//    	setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,SecurityContextHolder.createEmptyContext());
    	VaadinSession.getCurrent().setAttribute(Constantes.SESSION_USUARIO, null);
    	navigator.navigateTo(Login.NAME);
	}
		
	public Component getMenuLayout() {
		return menuLayout;
	}
}
