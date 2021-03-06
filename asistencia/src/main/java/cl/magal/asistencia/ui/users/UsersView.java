package cl.magal.asistencia.ui.users;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Role;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.entities.enums.UserStatus;
import cl.magal.asistencia.services.UserService;
import cl.magal.asistencia.ui.MagalUI;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.Utils;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.fieldgroup.FieldGroup.FieldGroupInvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@VaadinView(value=UsersView.NAME)
@Scope("prototype")
@Component
public class UsersView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8538300514577423280L;
	
	private transient Logger logger = LoggerFactory.getLogger(UsersView.class);
	
	public static final String NAME = "usuarios";
	
	BeanItemContainer<User> userContainer = new BeanItemContainer<User>(User.class);
	BeanItemContainer<Role> rolesContainer = new BeanItemContainer<Role>(Role.class);
	BeanItemContainer<ConstructionSite> constructionContainer = new BeanItemContainer<ConstructionSite>(ConstructionSite.class);
	BeanItemContainer<Role> rolContainer = new BeanItemContainer<Role>(Role.class);
	
	private TwinColSelect tcsObras,tcsPermissions;
	
	@Autowired
	UserService service;
	
	BeanFieldGroup<User> fieldGroup = new BeanFieldGroup<User>(User.class);
	BeanFieldGroup<Role> roleFieldGroup = new BeanFieldGroup<Role>(Role.class);
	FormLayout detailLayout,roleDetailLayout;
	FilterTable usersTable,rolesTable;
	
	public UsersView(){
	}
	
	@PostConstruct
	private void init(){
		
		setSizeFull();
		
		TabSheet tab = new TabSheet();
		tab.setSizeFull();
		addComponent(tab);
		
		// panel de usuarios
		HorizontalSplitPanel hsp = new HorizontalSplitPanel();
		hsp.setSizeFull();
		tab.addTab(hsp,"Usuarios");
		
		//dibula la sección de las obras
		VerticalLayout usersListLayout = drawUsers();
		hsp.addComponent(usersListLayout);
		
		VerticalLayout usersDetailLayout = drawUserDetail();	
		hsp.addComponent(usersDetailLayout);
		
		//panel de roles
		hsp = new HorizontalSplitPanel();
		hsp.setSizeFull();
		tab.addTab(hsp,"Perfiles");
		
		//dibula la sección de las obras
		VerticalLayout roleListLayout = drawRoles();
		hsp.addComponent(roleListLayout);
		
		VerticalLayout roleDetailLayout = drawRoleDetail();	
		hsp.addComponent(roleDetailLayout);
		
	}
	
	private VerticalLayout drawRoleDetail() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();
		
        //agrega un boton que hace el commit
        Button btnSave = new Button("Guardar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {
        		try {
        			roleFieldGroup.commit();
        			Role role = roleFieldGroup.getItemDataSource().getBean();
        			boolean isNew = role.getId() == null;
        			service.saveRole(role);
        			
        			if(isNew){
	        			BeanItem<Role> userItem = rolesContainer.addBean(role);
	        			setRole(userItem);
        			}
        			
        			Notification.show("Role guardado correctamente",Type.TRAY_NOTIFICATION);
        		} catch (CommitException e) {
        			
        			Utils.catchCommitException(e);
        			
        		}

        	}
        }){{
        	setIcon(FontAwesome.SAVE);
        }};
        
        vl.addComponent(btnSave);
        vl.setComponentAlignment(btnSave, Alignment.TOP_LEFT);
		
		roleDetailLayout = new FormLayout();
		roleDetailLayout.setMargin(true);
		roleDetailLayout.setSpacing(true);
		
		Panel p = new Panel(roleDetailLayout);
		p.setSizeFull();
		vl.addComponent(p);
		vl.setExpandRatio(p, 1.0f);
        
        // Loop through the properties, build fields for them and add the fields
        // to this UI
        for (Object propertyId : new String[]{"name","description"}) {
        	if(propertyId.equals("salt")||propertyId.equals("roleId")||propertyId.equals("deleted")||propertyId.equals("cs")){
        		;
        	} else if(propertyId.equals("description")){
        		TextArea txArea = new TextArea("Descripción");
        		txArea.setWidth("100%");
        		txArea.setNullRepresentation("");
        		roleFieldGroup.bind(txArea, propertyId);
        		roleDetailLayout.addComponent(txArea);
        	}else{
        		Field<?> field = roleFieldGroup.buildAndBind(tradProperty(propertyId), propertyId);
        		field.setWidth("100%");
        		if(field instanceof AbstractTextField)
        			((AbstractTextField) field).setNullRepresentation("");
        		roleDetailLayout.addComponent(field);
        	}
        }
        
        tcsPermissions = new TwinColSelect("Asignar Permisos");
        for(Permission per : Permission.values())
        	tcsPermissions.addItem(per);
        tcsPermissions.setWidth("100%");
        tcsPermissions.setNullSelectionAllowed(true);
        tcsPermissions.setImmediate(true);
		roleFieldGroup.bind(tcsPermissions, "permission");
		
		roleDetailLayout.addComponent(tcsPermissions);
		
		return vl;
	}

	private VerticalLayout drawRoles() {
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();
		
		//botones agrega y eliminar
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		vl.addComponent(hl);
		vl.setComponentAlignment(hl, Alignment.BOTTOM_LEFT);
				
		Button btnAddRole = new Button("Agregar Perfil",FontAwesome.PLUS);
		//agregando obras dummy
		btnAddRole.addClickListener(new Button.ClickListener() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 3844920778615955739L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				Role role = new Role();
				role.setName("Nuevo Perfil");
				
		        roleFieldGroup.setItemDataSource(new BeanItem<Role>(role));
				
			}
		});
		hl.addComponent(btnAddRole);
		Button btnDeleteRole = new Button(null,FontAwesome.TRASH_O);
		btnDeleteRole.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//recupera el elemento seleccionado
				final Role role = (Role) rolesTable.getValue();
				if(role == null){
					Notification.show("Debe seleccionar un perfil para eliminarlo");
					return;
				}
				ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el perfil seleccionado?",
						"Eliminar", "Cancelar", new ConfirmDialog.Listener() {
					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							//si el usuario es nuevo solo lo quita de la lista
							if(role.getId() != null )
								service.deleteUser(role.getId());
							rolesContainer.removeItem(role);				
							setRole( rolesContainer.getItem( rolesContainer.firstItemId() ));
						}
					}
				});	
				
			}
		});
		hl.addComponent(btnDeleteRole);
		
		//la tabla con su buscador buscador
		rolesTable =  drawTableRoles();
		vl.addComponent(rolesTable);
		vl.setExpandRatio(rolesTable, 1.0f);
		
		return vl;
	}

	private VerticalLayout drawUserDetail() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();
		
        //agrega un boton que hace el commit
        Button btnSave = new Button("Guardar",new Button.ClickListener() {

        	@Override
        	public void buttonClick(ClickEvent event) {
        		try {
        			fieldGroup.commit();
        			User user = fieldGroup.getItemDataSource().getBean();
        			boolean isNew = user.getId() == null;
        			service.saveUser(user);
        			
        			if(isNew){
	        			BeanItem<User> userItem = userContainer.addBean(user);
	        			setUser(userItem);
        			}
        			
        			fieldGroup.getField("password").setValue(null);
        			fieldGroup.getField("password2").setValue(null);
        			Notification.show("Usuario guardado correctamente",Type.TRAY_NOTIFICATION);
        		} catch (CommitException e) {
        			
        			Utils.catchCommitException(e);
        			
        		}

        	}
        }){{
        	setIcon(FontAwesome.SAVE);
        }};
        
        vl.addComponent(btnSave);
        vl.setComponentAlignment(btnSave, Alignment.TOP_LEFT);
		
		detailLayout = new FormLayout();
		detailLayout.setMargin(true);
		detailLayout.setSpacing(true);
		
		Panel p = new Panel(detailLayout);
		p.setSizeFull();
		vl.addComponent(p);
		vl.setExpandRatio(p, 1.0f);
        
        // Loop through the properties, build fields for them and add the fields
        // to this UI
        for (Object propertyId : new String[]{"rut","firstname","lastname","email","status","role","password","password2"}) {
        	if(propertyId.equals("salt")||propertyId.equals("userId")||propertyId.equals("deleted")||propertyId.equals("cs"))
        		;
        	else if(propertyId.equals("role")){
        		ComboBox cb = new ComboBox("Perfil",rolContainer);
        		cb.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        		cb.setItemCaptionPropertyId("name");
        		cb.setWidth("100%");
        		fieldGroup.bind(cb, propertyId);
        		detailLayout.addComponent(cb);
        	}else if(propertyId.equals("password")){
        		PasswordField pf = new PasswordField("Password");
        		pf.setNullRepresentation("");
        		detailLayout.addComponent(pf);
        		fieldGroup.bind(pf, propertyId);
        		pf.setWidth("100%");
        		pf.setValue(null);
        	}else if(propertyId.equals("password2")){
        		PasswordField pf2 = new PasswordField("Confirmar Password");
        		pf2.setNullRepresentation("");
        		pf2.setWidth("100%");
        		detailLayout.addComponent(pf2);
        		fieldGroup.bind(pf2, propertyId);
        		pf2.setValue(null);
        	}else if(propertyId.equals("status")){
        		ComboBox statusField = new ComboBox("Estado");
        		statusField.setNullSelectionAllowed(false);
        		for(UserStatus us : UserStatus.values()){
        			statusField.addItem(us);
        		}
        		statusField.setWidth("100%");
        		detailLayout.addComponent(statusField);
        		fieldGroup.bind(statusField, "status");
        	}else{
        		Field<?> field = fieldGroup.buildAndBind(tradProperty(propertyId), propertyId);
        		field.setWidth("100%");
        		detailLayout.addComponent(field);
        	}
        }
        
		tcsObras = new TwinColSelect("Asignar Obras",constructionContainer);      
		tcsObras.setWidth("100%");
		tcsObras.setNullSelectionAllowed(true);
		tcsObras.setItemCaptionPropertyId("name");
		tcsObras.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		tcsObras.setImmediate(true);
		tcsObras.setConverter(new ListCsToSetConverter());
		fieldGroup.bind(tcsObras, "cs");
		
		fieldGroup.addCommitHandler(new CommitHandler() {
			
			@Override
			public void preCommit(CommitEvent commitEvent) throws CommitException {
				
			}
			
			@Override
			public void postCommit(CommitEvent commitEvent) throws CommitException {
				
				Long id = fieldGroup.getItemDataSource().getBean().getId();
				
				//si es un nuevo usuario, valida que no exista un usuario con el mismo email
				if( id == null ){
					Field<?> email = fieldGroup.getField("email");
					
					User user = service.findUsuarioByUsername((String)email.getValue());
					if(user != null ){
						Map<Field<?>,InvalidValueException> map = new HashMap<Field<?>,InvalidValueException>();
						map.put(email, new InvalidValueException("Ya existe un usuario con el mismo email."));
						throw new FieldGroupInvalidValueException(map);
					}
				}
				
				//antes de comitear revisa que los passwords sean iguales si alguno es distinto de null
				Field<?> pf = fieldGroup.getField("password");
				Field<?> pf2 = fieldGroup.getField("password2");
				
				//	creacion debe validar que venga seteado el password y además coincida, en edicion solo deben coincidir
				if(  id == null && pf.getValue() == null ){
					Map<Field<?>,InvalidValueException> map = new HashMap<Field<?>,InvalidValueException>();
					map.put(pf, new InvalidValueException("El password es requerido para crear el usuario."));
					throw new FieldGroupInvalidValueException(map);
				}
				if( pf.getValue() != null || pf2.getValue() != null ){
					if(!pf.getValue().equals(pf2.getValue())){
						Map<Field<?>,InvalidValueException> map = new HashMap<Field<?>,InvalidValueException>();
						map.put(pf, new InvalidValueException("Los passwords deben coincidir"));
						throw new FieldGroupInvalidValueException(map);
					}
				}
			}
		});
		
		detailLayout.addComponent(tcsObras);
		
		return vl;
	}
	
	private void setUser(BeanItem<User> userItem){
		
		//obtiene el vertical Layout
		if(userItem == null){
			detailLayout.setEnabled(false);
			return;
		}
		
        usersTable.select(userItem.getBean());
		
		detailLayout.setEnabled(true); //VER
		
		userItem.getBean().setPassword(null);
		userItem.getBean().setPassword2(null);
		// We need an item data source before we create the fields to be able to
        // find the properties, otherwise we have to specify them by hand
        fieldGroup.setItemDataSource(userItem);

		if(SecurityHelper.hasPermission(Permission.ASIGNAR_OBRA)){
			
			tcsObras.setVisible(true);
			
        }else{
        	tcsObras.setVisible(false);
        }
		
	}
	
	private void setRole(BeanItem<Role> roleItem){
		
		//obtiene el vertical Layout
		if(roleItem == null){
			roleDetailLayout.setEnabled(false);
			return;
		}
		
        rolesTable.select(roleItem.getBean());
		
        roleDetailLayout.setEnabled(true); //VER
		
		// We need an item data source before we create the fields to be able to
        // find the properties, otherwise we have to specify them by hand
        roleFieldGroup.setItemDataSource(roleItem);

		if(SecurityHelper.hasPermission(Permission.ASIGNAR_OBRA)){
			
			tcsObras.setVisible(true);
			
        }else{
        	tcsObras.setVisible(false);
        }
		
	}
	
	private FilterTable drawTableRoles() {
		FilterTable rolesTable =  new FilterTable();
		rolesTable.setContainerDataSource(rolesContainer);
		rolesTable.setSizeFull();
		rolesTable.setFilterBarVisible(true);
		rolesTable.setVisibleColumns("name","description");
		rolesTable.setColumnHeaders("Nombre","Descripción");
		rolesTable.setSelectable(true);
		
		rolesTable.addItemClickListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				setRole((BeanItem<Role>)event.getItem());
			}
		});
		
		return rolesTable;
	}

	private FilterTable drawTableUsers() {
		FilterTable usersTable =  new FilterTable();
		userContainer.addNestedContainerProperty("role.name");
		usersTable.setContainerDataSource(userContainer);
		usersTable.setSizeFull();
		usersTable.setFilterBarVisible(true);
		usersTable.setVisibleColumns("firstname","email","role.name");
		usersTable.setColumnHeaders("Nombre","Email","Perfil");
		usersTable.setSelectable(true);
		
		usersTable.addItemClickListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				setUser((BeanItem<User>)event.getItem());
			}
		});
		
		return usersTable;
	}

	private VerticalLayout drawUsers() {
		
		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeFull();
		
		//botones agrega y eliminar
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		vl.addComponent(hl);
		vl.setComponentAlignment(hl, Alignment.BOTTOM_LEFT);
				
		Button agregaUsuario = new Button("Agregar Usuario",FontAwesome.PLUS);
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
				
		        fieldGroup.setItemDataSource(new BeanItem<User>(user));
				
			}
		});
		hl.addComponent(agregaUsuario);
		Button borrarUsuario = new Button(null,FontAwesome.TRASH_O);
		borrarUsuario.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				//recupera el elemento seleccionado
				final User user = (User) usersTable.getValue();
				if(user == null){
					Notification.show("Debe seleccionar un usuario para eliminarlo");
					return;
				}
				ConfirmDialog.show(UI.getCurrent(), "Confirmar Acción:", "¿Está seguro de eliminar el usuario seleccionado?",
						"Eliminar", "Cancelar", new ConfirmDialog.Listener() {
					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							//si el usuario es nuevo solo lo quita de la lista
							if(user.getId() != null )
								service.deleteUser(user.getId());
							userContainer.removeItem(user);				
							setUser( userContainer.getItem( userContainer.firstItemId() ));
						}
					}
				});	
				
			}
		});
		hl.addComponent(borrarUsuario);
		
		//la tabla con su buscador buscador
		usersTable =  drawTableUsers();
		vl.addComponent(usersTable);
		vl.setExpandRatio(usersTable, 1.0f);
		
		return vl;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		((MagalUI)UI.getCurrent()).setBackVisible(false);
		((MagalUI)UI.getCurrent()).highlightMenuItem(NAME);
		((MagalUI)UI.getCurrent()).getTitle().setValue("<h1>Usuarios</h1>");
		reloaData();		
	}
	
	public void reloaData(){
		Page<User> page = service.findAllUser(new PageRequest(0, 20));
		//List<User> users = service.findAllUser();
		userContainer.removeAllItems();
		userContainer.addAll(page.getContent());
		
		List<ConstructionSite> css = service.getAllObra();
        constructionContainer.removeAllItems();
        constructionContainer.addAll(css);
        
        List<Role> roles = service.getAllRole();
        rolContainer.removeAllItems();
        rolContainer.addAll(roles);
        rolesContainer.removeAllItems();
        rolesContainer.addAll(roles);
		
		setUser( userContainer.getItem( userContainer.firstItemId() ));
		
		setRole( rolesContainer.getItem( rolesContainer.firstItemId() ));
	}
	
	private String tradProperty(Object propertyId) {
		if(propertyId.equals("rut"))
			return "RUT";
		else if(propertyId.equals("email"))
			return "Email";
		else if(propertyId.equals("firstname"))
			return "Nombres";
		else if(propertyId.equals("secondname"))
			return "Segundo Nombre";
		else if(propertyId.equals("lastname"))
			return "Apellidos";
		else if(propertyId.equals("secondlastname"))
			return "Segundo Apellido";
		else if(propertyId.equals("dateBirth"))
			return "Fecha de Nacimiento";
		else if(propertyId.equals("address"))
			return "Dirección";
		else if(propertyId.equals("mobileNumber"))
			return "Teléfono móvil";
		else if(propertyId.equals("phone"))
			return "Teléfono fijo";
		else if(propertyId.equals("dateAdmission"))
			return "Fecha de Admisión";
		else if(propertyId.equals("provenance"))
			return "Procedencia";
		else if(propertyId.equals("reward"))
			return "Premio";
		else if(propertyId.equals("town"))
			return "Ciudad";
		else if(propertyId.equals("commune"))
			return "Comuna";
		else if(propertyId.equals("wedge"))
			return "Calzado";
		else if(propertyId.equals("bankAccount"))
			return "Cta. Banco";
		else if(propertyId.equals("dependets"))
			return "Cargas";
		else if(propertyId.equals("description"))
			return "Descripción";
		else if(propertyId.equals("name"))
			return "Nombre";
		else
			return propertyId.toString();
	}
	
	public static class ListCsToSetConverter implements Converter<Object, List> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5001359177239627061L;

		@Override
		public Class<Object> getPresentationType() {
			return Object.class;
		}

		@Override
		public List convertToModel(Object value,Class<? extends List> targetType, Locale locale)
				throws com.vaadin.data.util.converter.Converter.ConversionException {
			Set<ConstructionSite> pres = ((Set<ConstructionSite>)value);
			List<ConstructionSite> model =  new LinkedList<ConstructionSite>();
			for (ConstructionSite str: pres){
				model.add( str );
			}
			return model;
		}

		@Override
		public Object convertToPresentation(List value,
				Class<? extends Object> targetType, Locale locale)throws com.vaadin.data.util.converter.Converter.ConversionException {
			HashSet<ConstructionSite> set = new HashSet<ConstructionSite>(value.size());
			for (ConstructionSite str: (List<ConstructionSite>)value)
				set.add(str);
			return set;
		}

		@Override
		public Class<List> getModelType() {
			return List.class;
		}
	}
	
}
