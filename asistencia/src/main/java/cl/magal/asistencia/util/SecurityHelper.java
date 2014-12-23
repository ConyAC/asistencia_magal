package cl.magal.asistencia.util;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.ui.login.LoginView;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

public class SecurityHelper {

	public static boolean hasConstructionSite(ConstructionSite cs){

		User usuario = (User) VaadinSession.getCurrent().getAttribute(
				"usuario");
		if(usuario.getCs()!= null && usuario.getCs().contains(cs) ){
			return true;
		}

		return false;
	}

	public static boolean hastPermission(Permission... permissions) {
		if(permissions == null)
			return true;

		User usuario = (User) VaadinSession.getCurrent().getAttribute(Constants.SESSION_USUARIO);
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
		User usuario = (User) VaadinSession.getCurrent().getAttribute(Constants.SESSION_USUARIO);
		//si el usuario el nulo, lo rederidige al login
		if(usuario == null ) 
			return true;
		
		if(text.equals(Constants.MENU_USERS) )
			return hastPermission(Permission.CREAR_USUARIO);
		return true;
	}
}
