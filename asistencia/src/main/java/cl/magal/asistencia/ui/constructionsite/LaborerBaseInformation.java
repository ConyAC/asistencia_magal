package cl.magal.asistencia.ui.constructionsite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.enums.Afp;
import cl.magal.asistencia.entities.enums.Bank;
import cl.magal.asistencia.entities.enums.Isapre;
import cl.magal.asistencia.entities.enums.MaritalStatus;
import cl.magal.asistencia.entities.enums.Nationality;
import cl.magal.asistencia.util.Constants;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

public class LaborerBaseInformation extends VerticalLayout {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 453082527742097304L;
	transient Logger logger = LoggerFactory.getLogger(LaborerBaseInformation.class);
	
	String prefix = "";
	boolean viewElement;
	/**
	 * BINDER
	 */
	private BeanFieldGroup<?> binder;
	protected BeanFieldGroup<?> getBinder(){
		return binder;
	}

	public LaborerBaseInformation(BeanFieldGroup<?> fg, boolean viewElement) {
		this.binder = fg;
		this.viewElement = viewElement;
		init();
	}
	
	public LaborerBaseInformation(BeanFieldGroup<?> fg,String prefix, boolean viewElement) {
		this.binder = fg;
		this.prefix = prefix + ".";
		this.viewElement = viewElement;
		init();
	}
		
	private void init(){
		setSpacing(true);
		setWidth("100%");

		GridLayout detalleObrero = new GridLayout(3,7);
		detalleObrero.setSpacing(true);
		addComponent(detalleObrero);
		
		detalleObrero.addComponent(new Label("<h2>Información Personal : </h2>",ContentMode.HTML),0,0,2,0);
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);	
			
		Upload upload = new Upload("Cargar fotografía", null);
		upload.setButtonCaption("Iniciar carga");
		
		hl.addComponent(upload);
		hl.setComponentAlignment(upload, Alignment.TOP_LEFT);
				        
		// Show uploaded file in this placeholder
		final Embedded image = new Embedded();
		image.setVisible(false);
		hl.addComponent(image);
		
		// Implement both receiver that saves upload in a file and
		// listener for successful upload
		class ImageUploader implements Receiver, SucceededListener {
		    public File file;
		    
		    public OutputStream receiveUpload(String filename, String mimeType) {
		        // Create upload stream
		        FileOutputStream fos = null; // Output stream to write to
		        try {
		        	//añadimos el nombre de la imagen a la base
		        	getBinder().getItemDataSource().getItemProperty(prefix+"photo").setValue(filename);
		        	String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		            // Open the file for writing.
		            file = new File(basepath + "/WEB-INF/images/" + filename);
		            fos = new FileOutputStream(file);
		        } catch (final java.io.FileNotFoundException e) {
		        	logger.error("Error al obtener el archivo ",e);
		        	new Notification("No es posible acceder al archivo", e.getMessage());
		            //Notification.show("Error al guardar la imagen.");
		            return null;
		        }catch (Exception e) {
		        	logger.error("Error al obtener el archivo ",e);
		        	new Notification("No es posible acceder al archivo", e.getMessage());
		            //Notification.show("Error al guardar la imagen.");
		            return null;
		        }
		        return fos; // Return the output stream to write to
		    }

		    public void uploadSucceeded(SucceededEvent event) {
		        // Show the uploaded file in the image viewer
		        image.setVisible(true);
		        image.setHeight("100");
		        image.setWidth("100");
		        image.setSource(new FileResource(file));
		    }
		};
		//agrega la propiedad de la foto
		addNestedPropertyIfNeeded(prefix+"photo");
		final ImageUploader uploader = new ImageUploader(); 
		upload.setReceiver(uploader);
		upload.addListener(uploader);

		detalleObrero.addComponent(hl,0,1,2,1);
		detalleObrero.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);

		// Loop through the properties, build fields for them and add the fields
		// to this UI
		for (Object propertyId : new String[]{"rut","firstname","secondname","lastname", "secondlastname", "dateBirth", "commune", "town", "address", "mobileNumber", "phone","afp", "maritalStatus", "isapre", "nationality", "provenance", "wedge", "bank", "bankAccount","dependents","validityPensionReview"}) {
			Field<?> field = null;
			if(propertyId.equals("laborerId") || propertyId.equals("constructionSites") || propertyId.equals("contractId") || propertyId.equals("teamId") || (propertyId.equals("rut") && !viewElement))
				;
			else if(propertyId.equals("afp")){
				field = new ComboBox("AFP");
				((AbstractSelect) field).setNullSelectionAllowed(false);
				for(Afp a : Afp.values()){
					((AbstractSelect) field).addItem(a);
				}
			}else if(propertyId.equals("commune")){
				field = new ComboBox("Comuna");
				((AbstractSelect) field).setNullSelectionAllowed(false);
				for(String ms : Constants.COMUNAS ){
					((AbstractSelect) field).addItem(ms);
				}
			}else if(propertyId.equals("town")){
				field = new ComboBox("Ciudad");
				((AbstractSelect) field).setNullSelectionAllowed(false);
				for(String ms : Constants.PROVINCIAS){
					((AbstractSelect) field).addItem(ms);
				}
			}else if(propertyId.equals("maritalStatus")){
				field = new ComboBox("Estado Civil");
				((AbstractSelect) field).setNullSelectionAllowed(false);
				for(MaritalStatus ms : MaritalStatus.values()){
					((AbstractSelect) field).addItem(ms);
				}
			}else if(propertyId.equals("nationality")){
				field = new ComboBox("Nacionalidad");
				((AbstractSelect) field).setNullSelectionAllowed(false);
				for(Nationality n : Nationality.values()){
					((AbstractSelect) field).addItem(n);
				}
			}else if(propertyId.equals("isapre")){
				field = new ComboBox("Isapre");
				((AbstractSelect) field).setNullSelectionAllowed(false);
				for(Isapre i : Isapre.values()){
					((AbstractSelect) field).addItem(i);
				}
			}else if(propertyId.equals("bank")){
				field = new ComboBox("Banco");
				((AbstractSelect) field).setNullSelectionAllowed(false);
				for(Bank b : Bank.values()){
					((AbstractSelect) field).addItem(b);
				}
			}else{        		
				String t = tradProperty(propertyId);
				field = buildAndBind(t, prefix+propertyId);
				if(field instanceof TextField){
					((TextField)field).setNullRepresentation("");
				}
				
				if(viewElement){
					if(propertyId.equals("firstname") || propertyId.equals("lastname") || propertyId.equals("rut")){
						field.setEnabled(false);
					}
				}
			}
			// agrega el campo
			if( field != null ){
				
				field.addValidator(new BeanValidator(Laborer.class, (String) propertyId));
				bind(field, prefix+propertyId);
				detalleObrero.addComponent(field);
				detalleObrero.setComponentAlignment(field, Alignment.MIDDLE_CENTER);
			}
		}
		
//		if(viewElement){		
////			HorizontalLayout hl = new HorizontalLayout();
////			hl.setWidth("250%");
////			hl.setSpacing(true);	
////			
////			Field<?> field = buildAndBind("Premio", "reward");
////			((TextField)field).setNullRepresentation("");
////			detalleObrero.addComponent(field);
////		
////			Upload upload = new Upload("Cargar fotografía", null);
////			upload.setButtonCaption("Iniciar carga");
////			
////			hl.addComponent(upload);
////			hl.setComponentAlignment(upload, Alignment.TOP_LEFT);
////					        
////			// Show uploaded file in this placeholder
////			final Embedded image = new Embedded();
////			image.setVisible(false);
////			hl.addComponent(image);
////			
////			// Implement both receiver that saves upload in a file and
////			// listener for successful upload
////			class ImageUploader implements Receiver, SucceededListener {
////			    public File file;
////			    
////			    public OutputStream receiveUpload(String filename, String mimeType) {
////			        // Create upload stream
////			        FileOutputStream fos = null; // Output stream to write to
////			        try {
////			        	//añadimos el nombre de la imagen a la base
////			        	getBinder().getItemDataSource().getItemProperty(prefix+"photo").setValue(filename);
////			        	String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
////			            // Open the file for writing.
////			            file = new File(basepath + "/WEB-INF/images/" + filename);
////			            fos = new FileOutputStream(file);
////			        } catch (final java.io.FileNotFoundException e) {
////			        	new Notification("No es posible acceder al archivo", e.getMessage());
////			            //Notification.show("Error al guardar la imagen.");
////			            return null;
////			        }
////			        return fos; // Return the output stream to write to
////			    }
////	
////			    public void uploadSucceeded(SucceededEvent event) {
////			        // Show the uploaded file in the image viewer
////			        image.setVisible(true);
////			        image.setHeight("100");
////			        image.setWidth("100");
////			        image.setSource(new FileResource(file));
////			    }
////			};
////			final ImageUploader uploader = new ImageUploader(); 
////			upload.setReceiver(uploader);
////			upload.addListener(uploader);
////	
////			detalleObrero.addComponent(hl);
//		}
		detalleObrero.setWidth("100%");

	}
	


	private String tradProperty(Object propertyId) {
		if(propertyId.equals("rut"))
			return "RUT";
		else if(propertyId.equals("firstname"))
			return "Primer Nombre";
		else if(propertyId.equals("secondname"))
			return "Segundo Nombre";
		else if(propertyId.equals("lastname"))
			return "Primer Apellido";
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
		else if(propertyId.equals("dwellers"))
			return "Población";
		else if(propertyId.equals("town"))
			return "Ciudad";
		else if(propertyId.equals("commune"))
			return "Comuna";
		else if(propertyId.equals("wedge"))
			return "Calzado";
		else if(propertyId.equals("bankAccount"))
			return "Cta. Banco";
		else if(propertyId.equals("dependents"))
			return "Cargas";
		else if(propertyId.equals("validityPensionReview"))
			return "Vigencia Examen Previsional";
		else
			return propertyId.toString();
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
			if( !(getBinder().getItemDataSource() instanceof BeanItem) )
				throw new RuntimeException("El la propiedad "+propertyId+" no existe en el item definido y no se puede agregar dado que no es una instancia de BeanItem");
			BeanItem beanItem = (BeanItem)getBinder().getItemDataSource();
			getBinder().getItemDataSource().addItemProperty(propertyId, new NestedMethodProperty(beanItem.getBean(),(String) propertyId));
		}
	}
}
