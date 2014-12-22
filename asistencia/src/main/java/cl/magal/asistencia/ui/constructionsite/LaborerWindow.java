package cl.magal.asistencia.ui.constructionsite;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import cl.magal.asistencia.entities.Absence;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.Vacation;
import cl.magal.asistencia.entities.enums.AbsenceType;
import cl.magal.asistencia.entities.enums.Afp;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.entities.enums.MaritalStatus;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.ui.OnValueChangeFieldFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component
@Scope("prototype")
public class LaborerWindow extends AbstractWindowEditor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8280001172496734091L;
	transient Logger logger = LoggerFactory.getLogger(LaborerWindow.class);
	
	@Autowired
	LaborerService service;
	
	public LaborerWindow(BeanItem item){
		super(item);
		setWidth("70%");
	}
	

	@Override
	protected Component createBody() {
		
		TabSheet tab = new TabSheet();
		tab.addTab(drawInformation(),"Info");
		tab.addTab(drawVacations(),"Vacaciones");
		tab.addTab(drawAccidentesLicencias(),"Accidentes/Licencias");
	    return tab;
	}
	
	
	private Component drawAccidentesLicencias() {
		return new VerticalLayout(){
			{
				final BeanItemContainer<Absence> beanItem = new BeanItemContainer<Absence>(Absence.class);
				beanItem.addAll((Collection<? extends Absence>) getItem().getItemProperty("absences").getValue());
				
				setMargin(true);
				setSpacing(true);
				final Table table = new Table();
				
				addComponent(new GridLayout(3,2){
					{
						setWidth("100%");
						addComponent(new Label(""),0,0);
						
						addComponent(new Label(""),0,1);
						
						addComponent(new Button(null,new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								Laborer laborer = (Laborer) getItem().getBean();
								if(laborer == null ) throw new RuntimeException("El trabajador no es válido.");
								Absence absence = new Absence();
								laborer.addAbsence(absence);
								beanItem.addBean(absence);
								
							}
						}){{setIcon(FontAwesome.PLUS);}},2,0);
						
					}
				});
				table.setPageLength(5);
				table.setWidth("100%");
				table.setContainerDataSource(beanItem);
				table.setImmediate(true);
				table.addGeneratedColumn("total", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						
						final BeanItem<?> item = (BeanItem<?>) source.getItem(itemId);
						final Label label  = new Label(""+ source.getContainerProperty(itemId, columnId).getValue());
						Property.ValueChangeListener listener = new Property.ValueChangeListener() {
					            @Override
					            public void valueChange(Property.ValueChangeEvent event) {
					                label.setValue(((Absence) item.getBean()).getTotal()+"");
					            }
					        };
						for (String pid: new String[]{"fromDate", "toDate"})
				            ((ValueChangeNotifier)item.getItemProperty(pid)).addValueChangeListener(listener);
						
						return label; 
					}
				});
				
				table.setTableFieldFactory(new DefaultFieldFactory(){
					
					public Field<?> createField(final Container container,
							final Object itemId,Object propertyId,com.vaadin.ui.Component uiContext) {
						Field<?> field = null; 
						if( propertyId.equals("description") ){
							field = new TextField();
							((TextField)field).setNullRepresentation("");
						}

						else if(  propertyId.equals("fromDate") || propertyId.equals("toDate") ){
							field = new DateField();
						}
						else if(  propertyId.equals("absenceType") ){
							field = new ComboBox();
							field.setPropertyDataSource(container.getContainerProperty(itemId, propertyId));
							for(AbsenceType absenceType : AbsenceType.values()){
								((ComboBox)field).addItem(absenceType);
							}
							
						} else {
							return null;
						}
						
						((AbstractField<?>)field).setImmediate(true);
						return field;
					}
				});
				
				table.setVisibleColumns("absenceType","description","fromDate","toDate","total");
				table.setColumnHeaders("Tipo","Descripción","Desde","Hasta","Total");
				table.setEditable(true);				
				addComponent(table);
			}
		};
	}


	private Component drawVacations() {
		return new VerticalLayout(){
			{
				final BeanItemContainer<Vacation> beanItem = new BeanItemContainer<Vacation>(Vacation.class);
				beanItem.addAll((Collection<? extends Vacation>) getItem().getItemProperty("vacations").getValue());
				
				setMargin(true);
				setSpacing(true);
				final Table table = new Table();
				
				addComponent(new GridLayout(3,2){
					{
						setWidth("100%");
						addComponent(new Label("Días usados"),0,0);
						addComponent(new Label("3"),1,0);
						
						addComponent(new Label("Días disponibles"),0,1);
						addComponent(new Label("4"),1,1);
						
						addComponent(new Button(null,new Button.ClickListener() {
							
							@Override
							public void buttonClick(ClickEvent event) {
								Laborer laborer = (Laborer) getItem().getBean();
								if(laborer == null ) throw new RuntimeException("El trabajador no es válido.");
								Vacation vacation = new Vacation();
								laborer.addVacation(vacation);
								beanItem.addBean(vacation);
								
							}
						}){{setIcon(FontAwesome.PLUS);}},2,0);
						
					}
				});
				table.setPageLength(5);
				table.setWidth("100%");
				table.setContainerDataSource(beanItem);
				table.setImmediate(true);
				table.addGeneratedColumn("total", new Table.ColumnGenerator() {
					
					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						
						final BeanItem<?> item = (BeanItem<?>) source.getItem(itemId);
						final Label label  = new Label(""+ source.getContainerProperty(itemId, columnId).getValue());
						Property.ValueChangeListener listener = new Property.ValueChangeListener() {
					            @Override
					            public void valueChange(Property.ValueChangeEvent event) {
					                label.setValue(((Vacation) item.getBean()).getTotal()+"");
					            }
					        };
						for (String pid: new String[]{"fromDate", "toDate"})
				            ((ValueChangeNotifier)item.getItemProperty(pid)).addValueChangeListener(listener);
						
						return label; 
					}
				});
				
				table.setTableFieldFactory(new OnValueChangeFieldFactory(2));
				
				table.setVisibleColumns("fromDate","toDate","total");
				table.setColumnHeaders("Desde","Hasta","Total");
				table.setEditable(true);				
				addComponent(table);
			}
		};
	}


	private Component drawInformation(){

		GridLayout detalleObrero = new GridLayout(2,5);
		detalleObrero.setMargin(true);
		detalleObrero.setSpacing(true);
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		detalleObrero.addComponent(hl,0,0,1,0);
		detalleObrero.setComponentAlignment(hl, Alignment.TOP_RIGHT);
		
		
	    //agrega un boton que hace el commit
//	    Button add = new Button(null,new Button.ClickListener() {
//
//	    	@Override
//	    	public void buttonClick(ClickEvent event) {
//	    		try {
//	    			fieldGroup.commit();
//	    			Laborer laborer = fieldGroup.getItemDataSource().getBean();
//	    			constructionSiteService.addLaborerToConstructionSite(laborer,cs);				
//	    			laborerContainer.addBean(laborer);
//	    			close();
//	    		} catch (Exception e) {
//	    			logger.error("Error al guardar la información del obrero");
//	    			Notification.show("Es necesario agregar todos los campos obligatorios", Type.ERROR_MESSAGE);
//	    		}
//
//	    	}
//	    }){{
//	    	setIcon(FontAwesome.SAVE);
//	    }};
//	    hl.addComponent(add);
	    //detalleObrero.addComponent(add);
	    //detalleObrero.setComponentAlignment(add, Alignment.TOP_RIGHT);
	    
		//boton para imprimir
		Button btnPrint = new Button(null,new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Imprimiendo");
				
			}
		}){{
			setIcon(FontAwesome.PRINT);
		}};
		 hl.addComponent(btnPrint);
		//detalleObrero.addComponent(btnPrint);
		//detalleObrero.setComponentAlignment(btnPrint, Alignment.TOP_LEFT);        
	    // Loop through the properties, build fields for them and add the fields
	    // to this UI
		 for (Object propertyId : new String[]{"rut","firstname","secondname","lastname", "secondlastname", "dateBirth", "address", "mobileNumber", "phone", "dateAdmission"}) {
	    	if(propertyId.equals("laborerId") || propertyId.equals("constructionSites") || propertyId.equals("contractId") || propertyId.equals("teamId"))
	    		;
	    	else if(propertyId.equals("afp")){
	    		ComboBox afpField = new ComboBox("AFP");
	    		afpField.setNullSelectionAllowed(false);
				for(Afp a : Afp.values()){
					afpField.addItem(a);
				}
				detalleObrero.addComponent(afpField);
				bind(afpField, "afp");    			
	    	}else if(propertyId.equals("job")){
	    		ComboBox jobField = new ComboBox("Oficio");
	    		jobField.setNullSelectionAllowed(false);
				for(Job j : Job.values()){
					jobField.addItem(j);
				}
				detalleObrero.addComponent(jobField);
				bind(jobField, "job");    
	    	}else if(propertyId.equals("maritalStatus")){
	    		ComboBox msField = new ComboBox("Estado Civil");
	    		msField.setNullSelectionAllowed(false);
				for(MaritalStatus ms : MaritalStatus.values()){
					msField.addItem(ms);
				}
				detalleObrero.addComponent(msField);
				bind(msField, "maritalStatus");    
	    	}else{        		
	    		String t = tradProperty(propertyId);
	    		Field field = buildAndBind(t, propertyId);
	    		if(field instanceof TextField){
	    			((TextField)field).setNullRepresentation("");
	    		}
	    		detalleObrero.addComponent(field);
	    		detalleObrero.setComponentAlignment(field, Alignment.MIDDLE_CENTER);
	    	}
	    }
	    
	    detalleObrero.setWidth("100%");

	    return detalleObrero;
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

	
}
