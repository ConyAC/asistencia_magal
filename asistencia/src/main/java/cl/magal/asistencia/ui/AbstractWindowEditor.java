package cl.magal.asistencia.ui;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public abstract class AbstractWindowEditor extends Window implements ClickListener {

	
	transient Logger logger = LoggerFactory.getLogger(AbstractWindowEditor.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5583783226723562829L;
	/**
	 * ITEM
	 */
	private BeanItem<?> item;
	protected BeanItem<?> getItem(){
		return item;
	}
	
	private VerticalLayout root = new VerticalLayout();
	private Button btnGuardar,btnCancelar;
	
	/**
	 * BINDER
	 */
	private BeanFieldGroup<?> binder;
	protected BeanFieldGroup<?> getBinder(){
		return binder;
	}
	
	protected AbstractWindowEditor(BeanItem<?> item) {
		if(item == null )
			throw new RuntimeException("Error al crear el dialgo, el item no puede ser nulo.");
		this.item = item;
		binder = new BeanFieldGroup(item.getBean().getClass());
		getBinder().setItemDataSource(item);
		
		setModal(true);
		setResizable(false);
		center();

		HorizontalLayout footer = new HorizontalLayout();
		footer.setHeight("40px");
		footer.setSpacing(true);
		
		root.setMargin(true);
				
		btnGuardar = new Button("Guardar", this);
		btnGuardar.addStyleName("default");
		footer.addComponent(btnGuardar);
		footer.setComponentAlignment(btnGuardar, Alignment.MIDDLE_CENTER);

		btnCancelar = new Button("Cancelar", this);
		btnCancelar.addStyleName("link");
		footer.addComponent(btnCancelar);
		footer.setComponentAlignment(btnCancelar, Alignment.MIDDLE_CENTER);

		root.addComponent(new Label("<hr />", ContentMode.HTML));
		root.addComponent(footer);
		root.setComponentAlignment(footer, Alignment.MIDDLE_RIGHT);
		
		setContent(root);
//		setWindowContent(createBody());
	}
	
	public void init(){
		setWindowContent(createBody());
	}
	
	private void setWindowContent(Component content) {
		
		root.addComponentAsFirst(content);
		root.setExpandRatio(content, 1.0F);
	}
	
	/**
	 * permite bindear una propiedad al binder del dialog, si no existe la propiedad en el item, la intenta agregar solo si el item es una instancia de BeanItem
	 * @param field
	 * @param propertyId
	 */
	protected <T> void bind(Field<T> field, String propertyId) {
		//si la propiedad no existe como tal, intenta agregar la propiedad nested
		addNestedPropertyIfNeeded(propertyId);
		getBinder().bind(field, propertyId);
	}
	
	/**
	 * permite bindear una propiedad al binder del dialog, si no existe la propiedad en el item, la intenta agregar solo si el item es una instancia de BeanItem
	 * @param field
	 * @param propertyId
	 */
	protected Field<?> buildAndBind(String caption, Object propertyId) {		
		//si la propiedad no existe como tal, intenta agregar la propiedad nested
		addNestedPropertyIfNeeded(propertyId);
		Field<?> field = getBinder().buildAndBind(caption, propertyId);
		return field;
	}
	
	/**
	 * si no existe la propiedad en el item, la intenta agregar solo si el item es una instancia de BeanItem
	 * @param propertyId
	 */
	private void addNestedPropertyIfNeeded( Object propertyId ){
		//si el argumento no es de tipo string, da error 
		if(!(propertyId instanceof String))
			throw new RuntimeException("El la propiedad "+propertyId+" debe ser de tipo string");
		
		//si la propiedad no existe como tal, intenta agregar la propiedad nested
		if( getBinder().getItemDataSource().getItemProperty(propertyId) == null){
			//solo puede recuperar el bean si el item es una instancia de BeanItem
			if( !(getItem() instanceof BeanItem) )
				throw new RuntimeException("El la propiedad "+propertyId+" no existe en el item definido y no se puede agregar dado que no es una instancia de BeanItem");
			BeanItem beanItem = (BeanItem)getItem();
			getItem().addItemProperty(propertyId, new NestedMethodProperty(beanItem.getBean(),(String) propertyId));
		}
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == btnGuardar) {
//			if(!getBinder().isValid()){
//				Notification.show("Falta parámetro obligatorio");
//				getBinder().
//				return;
//			}
			try {
				if(!preCommit())
					return;
				getBinder().commit();
				
				if(!postCommit())
					return;
				
				fireEvent(new EditorSavedEvent(this, item));
			} catch (EmptyValueException e){
				Notification.show("Falta un campo requerido",Type.HUMANIZED_MESSAGE);
				logger.error("EmptyValueException",e);
				return;
			} catch (CommitException e) {
				Notification.show("Existen valores inválidos",Type.ERROR_MESSAGE);
				logger.error("CommitException",e);
				for (Component c: root){
					try{ ((AbstractField)c).setValidationVisible(true); }catch(Exception e1){}
				}
				return;
			}
			
		} else if (event.getButton() == btnCancelar) {
			if(!preDiscard())
				return;
			logger.debug("llamando discard");
			getBinder().discard();
			
			if(!postDiscard())
				return;
		}
		close();
	}
	
	protected boolean postDiscard() {
		return true;
	}

	protected boolean preDiscard() {
		return true;
	}

	protected boolean preCommit(){
		return true;
	}
	
	protected boolean postCommit(){
		return true;
	}
	
	/**
	 * Se debe implementar el siguiente metodo con la vista deseada para el dialogo
	 * @return
	 */
	abstract protected Component createBody();
	
	public void addListener(EditorSavedListener editorSavedListener) {
		try {
			Method method = EditorSavedListener.class.getDeclaredMethod( "editorSaved",new Class[] { EditorSavedEvent.class });
			addListener(EditorSavedEvent.class,editorSavedListener, method);
		} catch (final java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException(
					"Internal error, editor saved method not found");
		}
	}

	public void removeListener(EditorSavedListener listener) {
		removeListener(EditorSavedEvent.class, listener);
	}
	
	public static class EditorSavedEvent extends Component.Event {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4411059733094680039L;
		private Item savedItem;

		public EditorSavedEvent(Component source, Item savedItem) {
			super(source);
			this.savedItem = savedItem;
		}

		public Item getSavedItem() {
			return savedItem;
		}
	}

	public interface EditorSavedListener extends Serializable {
		public void editorSaved(EditorSavedEvent event);
	}
}
