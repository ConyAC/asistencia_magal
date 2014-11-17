package cl.magal.asistencia.ui.users;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.services.UserHelper;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.util.Constants;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;

@VaadinView(value=UsersView.NAME)
@Scope("prototype")
@Component
public class UsersView extends HorizontalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8538300514577423280L;
	
	private transient Logger logger = LoggerFactory.getLogger(UsersView.class);
	
	public static final String NAME = "usuarios";
	
	BeanItemContainer<User> container = new BeanItemContainer<User>(User.class);
	
	@Autowired
	UserService service;
	
	VerticalLayout detalleUsuario;
	FilterTable usersTable;
	
	public UsersView(){
		
//		container.addNestedContainerProperty("role");

		setSizeFull();
		
		//dibula la sección de las obras
		VerticalLayout usuarios = drawUsuarios();
		
		addComponent(usuarios);
		setExpandRatio(usuarios, 0.2F);
		
		//dibuja la sección de detalles
		detalleUsuario = drawDetalleUsuario();
		
		addComponent(detalleUsuario);
		setExpandRatio(detalleUsuario, 0.8F);
	}
	
	private VerticalLayout drawDetalleUsuario() {
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.addComponent(new Label("Seleccione un usuario para ver su información"));
		
		return vl;
	}
	
	private void setUser(BeanItem<User> userItem){
		
		//obtiene el vertical Layout
		detalleUsuario.removeAllComponents();
		if(userItem == null){
			detalleUsuario.addComponent(new Label("Seleccione un usuario para ver su información"));
			return;
		}
		
		
		final BeanFieldGroup<User> fieldGroup = new BeanFieldGroup<User>(User.class);
		// We need an item data source before we create the fields to be able to
        // find the properties, otherwise we have to specify them by hand
        fieldGroup.setItemDataSource(userItem);

        //agrega un boton que hace el commit
        Button add = new Button(null,new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {
        		try {
        			fieldGroup.commit();
        			service.saveUser(fieldGroup.getItemDataSource().getBean());
        		} catch (CommitException e) {
        			logger.error("Error al guardar la información del usuario");
        			Notification.show("Error al guardar la información del usuario", Type.ERROR_MESSAGE);
        		}

        	}
        }){{
        	setIcon(FontAwesome.SAVE);
        }};
        detalleUsuario.addComponent(add);
        detalleUsuario.setComponentAlignment(add, Alignment.TOP_RIGHT);
        
        // Loop through the properties, build fields for them and add the fields
        // to this UI
        for (Object propertyId : fieldGroup.getUnboundPropertyIds()) {
        	if(propertyId.equals("role")||propertyId.equals("salt")||propertyId.equals("userId"))
        		;
        	else if(propertyId.equals("role.name")){
        		ComboBox cb = new ComboBox("Rol",Arrays.asList("ADM","SADM"));
        		detalleUsuario.addComponent(cb);
        		fieldGroup.bind(cb, propertyId);
        	}else if(propertyId.equals("password")){
        		PasswordField pf = new PasswordField("Password");
        		pf.setNullRepresentation("");
        		detalleUsuario.addComponent(pf);
        		fieldGroup.bind(pf, propertyId);
        	}else if(propertyId.equals("password2")){
        		PasswordField pf2 = new PasswordField("Confirmar Password");
        		pf2.setNullRepresentation("");
        		detalleUsuario.addComponent(pf2);
        		fieldGroup.bind(pf2, propertyId);
        	}else
        		detalleUsuario.addComponent(fieldGroup.buildAndBind(propertyId));
        	
        }
	}

	private FilterTable drawTablaUsuarios() {
		usersTable =  new FilterTable();
		container.addNestedContainerProperty("role.name");
		usersTable.setContainerDataSource(container);
		usersTable.setSizeFull();
		usersTable.setFilterBarVisible(true);
		usersTable.setVisibleColumns("role.name","firstname");
		usersTable.setSelectable(true);
		
		usersTable.addItemClickListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				setUser((BeanItem<User>)event.getItem());
			}
		});
		
		return usersTable;
	}

	private VerticalLayout drawUsuarios() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		
		//la tabla con su buscador buscador
		vl.addComponent(drawTablaUsuarios());
		//botones agrega y eliminar
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		vl.addComponent(hl);
		vl.setComponentAlignment(hl, Alignment.BOTTOM_CENTER );
		Button agregaObra = new Button(null,FontAwesome.PLUS);
		//agregando obras dummy
		agregaObra.addClickListener(new Button.ClickListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 3844920778615955739L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				User user = UserHelper.newUser();
				service.saveUser(user);
				container.addBean(user);
				
			}
		});
		hl.addComponent(agregaObra);
		Button borrarObra = new Button(null,FontAwesome.TRASH_O);
		borrarObra.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//recupera el elemento seleccionado
				User user = (User) usersTable.getValue();
				if(user == null){
					Notification.show("Debe seleccionar un usuario para eliminarlo");
					return;
				}
				//TODO dialogo de confirmación
				service.deleteUser(user.getUserId());
				container.removeItem(user);
				
				setUser(null);
			}
		});
		hl.addComponent(borrarObra);
		
		return vl;
	}

	@PostConstruct
	private void init(){
	}

	@Override
	public void enter(ViewChangeEvent event) {
		List<User> users = service.findAllUser();
		container.removeAllItems();
		container.addAll(users);
		
	}

}
