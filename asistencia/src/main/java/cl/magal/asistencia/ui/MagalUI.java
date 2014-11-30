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
import cl.magal.asistencia.ui.config.ConfigView;
import cl.magal.asistencia.ui.constructionsite.ConstructionSiteView;
import cl.magal.asistencia.ui.login.LoginView;
import cl.magal.asistencia.ui.users.UsersView;
import cl.magal.asistencia.ui.workerfile.WorkerFileView;
import cl.magal.asistencia.util.Constants;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component
@Scope("prototype")
@SuppressWarnings("serial")
@Title("Asistencia Magal")
@Theme("magaltheme")
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
		
		VaadinSession.getCurrent().setErrorHandler(this);
		//raiz
		root = new VerticalLayout();
		setContent(root);
		root.setSpacing(true);
        root.addStyleName("root");
        root.setSizeFull();
        //menu
        drawMenu();
		
        Panel content = new Panel();
		content.setSizeFull();
		content.addStyleName("view-content");
		
		navigator = new DiscoveryNavigator(UI.getCurrent(), content);
		
		root.addComponent(content);
		root.setExpandRatio(content, 1.0F);
		
	}

	public void error(com.vaadin.server.ErrorEvent event) {
		DefaultErrorHandler.doDefault(event);
	}
	
	public String getUrl(String menuName){
		if(menuName == null )
			throw new RuntimeException("Nombre de menu si clase conocida.");
		
		if(menuName.equals("Obras"))
			return ConstructionSiteView.NAME;
		else if(menuName.equals("Fichas"))
			return WorkerFileView.NAME;
		else if(menuName.equals("Usuarios"))
			return UsersView.NAME;
		else if(menuName.equals("Usuarios"))
			return UsersView.NAME;
		else if(menuName.equals("Configuraciones"))
			return ConfigView.NAME;
		else if(menuName.equals("Salir"))
			return null;
		else
			throw new RuntimeException("Nombre de menu si clase conocida.");
	}
	
	
	MenuItem previous = null;
	
	private MenuBar drawMenu() {
		
		menuLayout = new MenuBar();
		menuLayout.addStyleName("mybarmenu");
		
		MenuBar.Command mycommand = new MenuBar.Command() {
			
		    public void menuSelected(MenuItem selectedItem) {
		       
		    	navigator.navigateTo(getUrl(selectedItem.getText()));
		        if (previous != null)
		            previous.setStyleName(null);
		        selectedItem.setStyleName("highlight");
		        previous = selectedItem;
		    } 
		};
		
		MenuItem item = menuLayout.addItem("Obras",mycommand);
		item.setStyleName("highlight");
		previous=item; 
		
		item.setIcon(FontAwesome.BUILDING);
		
		item = menuLayout.addItem("Fichas",mycommand);
		
		item.setIcon(FontAwesome.BOOK);
		
		item = menuLayout.addItem("Usuarios", mycommand);
		
		item.setIcon(FontAwesome.USERS);
		
		item = menuLayout.addItem("Configuraciones",mycommand);
		
		item.setIcon(FontAwesome.GEAR);
		
		item = menuLayout.addItem("Salir", new Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				logOut();
				selectedItem.setVisible(false);
			}
		});
		
		item.setIcon(FontAwesome.POWER_OFF);
		
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
    	VaadinSession.getCurrent().setAttribute(Constants.SESSION_USUARIO, null);
    	navigator.navigateTo(LoginView.NAME);
	}
		
	public Component getMenuLayout() {
		return menuLayout;
	}
}
