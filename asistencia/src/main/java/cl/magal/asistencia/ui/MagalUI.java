package cl.magal.asistencia.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import cl.magal.asistencia.ui.constructionsite.ConstructionSitesView;
import cl.magal.asistencia.ui.login.LoginView;
import cl.magal.asistencia.ui.users.UsersView;
import cl.magal.asistencia.ui.workerfile.WorkerFileView;
import cl.magal.asistencia.util.Constants;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.SpringContextHelper;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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
	HorizontalLayout top;	MenuBar menuLayout;
	DiscoveryNavigator navigator = null ;
	Button backBtn; 
	Label title;
	
	transient SpringContextHelper helper;
	
	public Object getSpringBean(final String beanRef){
		return helper.getBean(beanRef);
	}

	@Override
	protected void init(final VaadinRequest request) {
		
		VaadinSession.getCurrent().setErrorHandler(this);
		
		helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		
		Locale.setDefault(new Locale("es", "CL"));
		
		//raiz
		root = new VerticalLayout();
		setContent(root);
		root.setSpacing(true);
        root.addStyleName("root");
        root.setSizeFull();
        
        top = new HorizontalLayout();
        top.setMargin(true);
//        top.setSpacing(true);
        top.setWidth("100%");
        top.setHeight("75px");
        root.addComponent(top);
        
        backBtn = new Button(null,FontAwesome.ANGLE_DOUBLE_LEFT);
        top.addComponent(backBtn);
        top.setExpandRatio(backBtn, 0.05F);
        top.setComponentAlignment(backBtn,Alignment.TOP_LEFT);
        
        title = new Label("<h1>Título</h1>",ContentMode.HTML);
        title.setHeight("50px");
        title.setWidthUndefined();
        title.setStyleName("no-margin");
        
        top.addComponent(title);
        top.setComponentAlignment(title,Alignment.TOP_LEFT);
        top.setExpandRatio(title, 0.7F);
        
		
        Panel content = new Panel();
		content.setSizeFull();
		content.addStyleName("view-content");

		navigator = new DiscoveryNavigator(UI.getCurrent(), content);
		
		navigator.addViewChangeListener(new ViewChangeListener(){

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				//verifica que está logeado
				if(SecurityHelper.isLogged()){
					if(menuItems != null)
						// verifica los permisos para mostrar los menus
						for(MenuItem menu : menuItems){
							menu.setVisible(SecurityHelper.hasMenu(menu.getText()));
						}
					}
				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {
				// TODO Auto-generated method stub
				
			}
			
		});

		root.addComponent(content);
		root.setExpandRatio(content, 1.0F);
        //menu
        menuLayout = drawMenu();
        
        top.addComponent(menuLayout);
        top.setComponentAlignment(menuLayout,Alignment.TOP_RIGHT);
//        top.setExpandRatio(menuLayout, 1.0F);
		
		
	}

	public void error(com.vaadin.server.ErrorEvent event) {
		DefaultErrorHandler.doDefault(event);
	}
	
	public String getUrl(String menuName){
		if(menuName == null )
			throw new RuntimeException("Nombre de menu si clase conocida.");
		
		if(menuName.equals(Constants.MENU_CONSTRUCTIONSITE))
			return ConstructionSitesView.NAME;
		else if(menuName.equals(Constants.MENU_WORKERFILE))
			return WorkerFileView.NAME;
		else if(menuName.equals(Constants.MENU_USERS))
			return UsersView.NAME;
		else if(menuName.equals(Constants.MENU_CONFIGURATIONS))
			return ConfigView.NAME;
		else if(menuName.equals(Constants.MENU_LOGOUT))
			return "";
		else
			throw new RuntimeException("Nombre de menu sin clase conocida.");
	}
	
	List<MenuItem> menuItems = new ArrayList<MenuItem>(5);
	
	public void highlightMenuItem(String URL){
		for(MenuItem item : menuItems ){
			if(getUrl(item.getText()).equals(URL))
				item.setStyleName("highlight");
			else
				item.setStyleName(null);
		}
	}
	
	private MenuBar drawMenu() {
		
		MenuBar menuLayout = new MenuBar();
		menuLayout.addStyleName("mybarmenu");
		
		MenuBar.Command mycommand = new MenuBar.Command() {
			
		    public void menuSelected(MenuItem selectedItem) {
		       
		    	navigator.navigateTo(getUrl(selectedItem.getText()));
		    } 
		};
		
		MenuItem item = menuLayout.addItem(Constants.MENU_CONSTRUCTIONSITE,mycommand);
		menuItems.add(item);
		
		item.setIcon(FontAwesome.BUILDING);
		
		item = menuLayout.addItem(Constants.MENU_WORKERFILE,mycommand);
		menuItems.add(item);
		
		item.setIcon(FontAwesome.BOOK);
		
		item = menuLayout.addItem(Constants.MENU_USERS, mycommand);
		item.setIcon(FontAwesome.USERS);
		menuItems.add(item);
		
		item = menuLayout.addItem(Constants.MENU_CONFIGURATIONS,mycommand);
		menuItems.add(item);
		
		item.setIcon(FontAwesome.GEAR);
		
		item = menuLayout.addItem(Constants.MENU_LOGOUT, new Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				logOut();
			}
		});
		menuItems.add(item);
		
		item.setIcon(FontAwesome.POWER_OFF);
		
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
//    	VaadinSession.getCurrent().close();
    	navigator.navigateTo(LoginView.NAME);
	}
	
	/** GETTERS DE COMPONENTES DE LA PAGINA **/
		
	public Component getMenuLayout() {
		return menuLayout;
	}
	
	public HorizontalLayout getTop(){
		return top;
	}
	
	public Button getBackButton(){
		//quita todos los listeners antes de entregarlo
		for( Object listener : backBtn.getListeners(Button.ClickEvent.class))
			backBtn.removeClickListener((ClickListener) listener);
		return backBtn;
	}
	
	public Label getTitle(){
		return title;
	}
	
	/** UTILITARIOS PARA MOSTRAR O OCULTAR PARTES DE LA PAGINA **/
	public void setTopVisible(boolean visible){
		top.setVisible(visible);
	}
	
	public void setBackVisible(boolean visible){
		backBtn.setVisible(visible);
	}
	
	public void setTitleVisible(boolean visible){
		title.setVisible(visible);
	}
	
}
