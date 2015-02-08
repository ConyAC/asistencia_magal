package cl.magal.asistencia.util;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.ui.login.LoginView;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

public class SecurityHelper {
	
	static User testUser;
	
	private static User getUser(){
		//solo para termino de testing
		if(VaadinSession.getCurrent() != null )
			return (User) VaadinSession.getCurrent().getAttribute(Constants.SESSION_USUARIO);
		else
			return testUser;
	}
	
	public static void setUser(User user){
		testUser = user;
	}
	
	public static boolean isLogged(){
		return getUser() == null;
	}

	public static boolean hasConstructionSite(ConstructionSite cs){

		User usuario = getUser();
		//si el usuario el nulo, lo rederidige al login
		if(usuario == null ) {
			UI.getCurrent().getNavigator().navigateTo(LoginView.NAME);
			return false;
		}
		
		if(usuario.getCs()!= null && usuario.getCs().contains(cs) ){
			return true;
		}

		return false;
	}

	public static boolean hasPermission(Permission... permissions) {
		if(permissions == null)
			return true;

		User usuario = getUser();
		//si el usuario el nulo, lo rederidige al login
		if(usuario == null ) {
			UI.getCurrent().getNavigator().navigateTo(LoginView.NAME);
			return false;
		}
		
		if(usuario.getRole() == null || usuario.getRole().getPermission() == null ){
			return false;
		}
		for(Permission p : permissions){
			if(!usuario.getRole().getPermission().contains(p))
				return false;
		}

		return true;
	}

	public static boolean hasMenu(String text) {
		User usuario = getCredentials();
		//si el usuario el nulo, lo rederidige al login
		if(usuario == null ) 
			return true;
		
		if(text.equals(Constants.MENU_USERS) )
			return hasPermission(Permission.CREAR_USUARIO);
		return true;
	}

	public static User getCredentials() {
		User user = getUser();
		if(user == null ) {
			UI.getCurrent().getNavigator().navigateTo(LoginView.NAME);
			return null;
		}
		return user;
	}
}
