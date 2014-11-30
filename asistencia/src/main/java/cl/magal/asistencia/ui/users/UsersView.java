package cl.magal.asistencia.ui.users;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Status;
import cl.magal.asistencia.entities.enums.UserStatus;
import cl.magal.asistencia.services.UserService;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TwinColSelect;
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
	
	private TwinColSelect tcsObras;
	
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
			detalleUsuario.setEnabled(false);
			detalleUsuario.addComponent(new Label("Seleccione un usuario para ver su información"));
			return;
		}
		detalleUsuario.setEnabled(true); //VER
		
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
        	if(propertyId.equals("role")||propertyId.equals("salt")||propertyId.equals("userId")||propertyId.equals("deleted"))
        		;
        	else if(propertyId.equals("role.name")){
        		ComboBox cb = new ComboBox("Rol",Arrays.asList("AADMO","ADMC","ADMO","SADM"));
        		detalleUsuario.addComponent(cb);
//        		fieldGroup.bind(cb, propertyId);
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
        	}else if(propertyId.equals("status")){
        		ComboBox statusField = new ComboBox("Estado");
        		statusField.setNullSelectionAllowed(false);
        		for(UserStatus us : UserStatus.values()){
        			statusField.addItem(us);
        		}
        		detalleUsuario.addComponent(statusField);
        		fieldGroup.bind(statusField, "status");
        	}else if(propertyId.equals("")){
        		tcsObras = new TwinColSelect("Asignar Obra");
                // Set the column captions (optional)
                tcsObras.setLeftColumnCaption("Obras");
                tcsObras.setRightColumnCaption("Obras Seleccionadas");
                
        		tcsObras.setWidth("70%");
        		tcsObras.setNullSelectionAllowed(true);
        		tcsObras.setItemCaptionPropertyId("nombre");
        		tcsObras.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        		tcsObras.setImmediate(true);
        		tcsObras.setRows(2);
        		
        		detalleUsuario.addComponent(tcsObras);
        		//detalleUsuario.setComponentAlignment(tcsObras, Alignment.MIDDLE_RIGHT);
        	}else
        		detalleUsuario.addComponent(fieldGroup.buildAndBind(propertyId)); 			
        }
        
        //prueba
		tcsObras = new TwinColSelect("Asignar Obras");
        // Set the column captions (optional)
       // tcsObras.setLeftColumnCaption("Obras");
       // tcsObras.setRightColumnCaption("Obras Seleccionadas");        
		tcsObras.setWidth("70%");
		tcsObras.setNullSelectionAllowed(true);
		tcsObras.setItemCaptionPropertyId("nombre");
		tcsObras.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		tcsObras.setImmediate(true);
		tcsObras.setRows(2);
		
		detalleUsuario.addComponent(tcsObras);
		//detalleUsuario.setComponentAlignment(tcsObras, Alignment.TOP_RIGHT);
	}

	private FilterTable drawTablaUsuarios() {
		usersTable =  new FilterTable();
		container.addNestedContainerProperty("role.name");
		usersTable.setContainerDataSource(container);
		usersTable.setSizeFull();
		usersTable.setFilterBarVisible(true);
		usersTable.setVisibleColumns("role.name","firstname");
		usersTable.setColumnHeaders("Perfil", "Nombre");
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
		Button agregaUsuario = new Button(null,FontAwesome.PLUS);
		//agregando obras dummy
		agregaUsuario.addClickListener(new Button.ClickListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 3844920778615955739L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				User user = new User();
				user.setFirstname("Nuevo Usuario");
				user.setLastname("");
				user.setEmail("");
				user.setStatus(UserStatus.ACTIVE);
				user.setRut("");
				service.saveUser(user);
				BeanItem<User> item = container.addBean(user);
				setUser(item);
				
			}
		});
		hl.addComponent(agregaUsuario);
		Button borrarUsuario = new Button(null,FontAwesome.TRASH_O);
		borrarUsuario.addClickListener(new Button.ClickListener() {
			
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
		hl.addComponent(borrarUsuario);
		
		return vl;
	}

	@PostConstruct
	private void init(){
	}

	@Override
	public void enter(ViewChangeEvent event) {
		reloaData();		
	}
	
	public void reloaData(){
		Page<User> page = service.findAllUser(new PageRequest(0, 20));
		//List<User> users = service.findAllUser();
		container.removeAllItems();
		container.addAll(page.getContent());
		
		setUser( container.getItem( container.firstItemId() ));
		usersTable.select(container.firstItemId());
	}

}
