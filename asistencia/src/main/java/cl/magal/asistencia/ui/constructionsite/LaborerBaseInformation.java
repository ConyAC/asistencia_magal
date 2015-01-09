package cl.magal.asistencia.ui.constructionsite;

import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.enums.Afp;
import cl.magal.asistencia.entities.enums.MaritalStatus;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LaborerBaseInformation extends VerticalLayout {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 453082527742097304L;
	
	String prefix = "";
	
	/**
	 * BINDER
	 */
	private BeanFieldGroup<?> binder;
	protected BeanFieldGroup<?> getBinder(){
		return binder;
	}

	protected LaborerBaseInformation(BeanFieldGroup<?> fg) {
		this.binder = fg;
		init();
	}
	
	protected LaborerBaseInformation(BeanFieldGroup<?> fg,String prefix) {
		this.binder = fg;
		this.prefix = prefix + ".";
		init();
	}
		
	private void init(){
		setSpacing(true);
		setMargin(true);
		setSizeFull();

		GridLayout detalleObrero = new GridLayout(3,5);
		detalleObrero.setMargin(true);
		detalleObrero.setSpacing(true);
		addComponent(detalleObrero);

		// Loop through the properties, build fields for them and add the fields
		// to this UI
		for (Object propertyId : new String[]{"rut","firstname","secondname","lastname", "secondlastname", "dateBirth", "address", "mobileNumber", "phone", "dateAdmission", //"job",
				"afp", "maritalStatus"}) {
			if(propertyId.equals("laborerId") || propertyId.equals("constructionSites") || propertyId.equals("contractId") || propertyId.equals("teamId"))
				;
			else if(propertyId.equals("afp")){
				ComboBox afpField = new ComboBox("AFP");
				afpField.setNullSelectionAllowed(false);
				for(Afp a : Afp.values()){
					afpField.addItem(a);
				}
				detalleObrero.addComponent(afpField);
				bind(afpField, prefix+"afp");   
				detalleObrero.setComponentAlignment(afpField, Alignment.MIDDLE_CENTER);
//			}else if(propertyId.equals("job")){
//				ComboBox jobField = new ComboBox("Oficio");
//				jobField.setNullSelectionAllowed(false);
//				for(Job j : Job.values()){
//					jobField.addItem(j);
//				}
//				detalleObrero.addComponent(jobField);
//				bind(jobField, "job");    
//				detalleObrero.setComponentAlignment(jobField, Alignment.MIDDLE_CENTER);
			}else if(propertyId.equals("maritalStatus")){
				ComboBox msField = new ComboBox("Estado Civil");
				msField.setNullSelectionAllowed(false);
				for(MaritalStatus ms : MaritalStatus.values()){
					msField.addItem(ms);
				}
				detalleObrero.addComponent(msField);
				bind(msField, prefix+"maritalStatus");   
				detalleObrero.setComponentAlignment(msField, Alignment.MIDDLE_CENTER);
			}else{        		
				String t = tradProperty(propertyId);
				Field<?> field = buildAndBind(t, prefix+propertyId);
				if(field instanceof TextField){
					((TextField)field).setNullRepresentation("");
					field.addValidator(new BeanValidator(Laborer.class, (String) propertyId));
				}
				
				if(propertyId.equals("firstname")){
					field.focus();
				}
				detalleObrero.addComponent(field);
				detalleObrero.setComponentAlignment(field, Alignment.MIDDLE_CENTER);
			}
		}

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
			return "Direcciòn";
		else if(propertyId.equals("mobileNumber"))
			return "Teléfono móvil";
		else if(propertyId.equals("phone"))
			return "Teléfono fijo";
		else if(propertyId.equals("dateAdmission"))
			return "Fecha de Admisión";
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
