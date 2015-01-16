package cl.magal.asistencia.ui.workerfile;


import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.FilterTable;

import ru.xpoft.vaadin.VaadinView;
import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.Vacation;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.ui.MagalUI;
import cl.magal.asistencia.ui.constructionsite.LaborerBaseInformation;
import cl.magal.asistencia.ui.constructionsite.LaborerConstructionDialog;
import cl.magal.asistencia.util.Utils;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@VaadinView(value=WorkerFileView.NAME)
@Scope("prototype")
@Component
public class WorkerFileView extends HorizontalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7438608047589376668L;

	private transient Logger logger = LoggerFactory.getLogger(WorkerFileView.class);	
	public static final String NAME = "fichas";	
	BeanItemContainer<Laborer> laborerContainer = new BeanItemContainer<Laborer>(Laborer.class);
	BeanFieldGroup<Laborer> fieldGroup = new BeanFieldGroup<Laborer>(Laborer.class);
	BeanItemContainer<LaborerConstructionsite> historyContainer = new BeanItemContainer<LaborerConstructionsite>(LaborerConstructionsite.class);
	
	@Autowired
	private transient LaborerService service;
	@Autowired
	transient private VelocityEngine velocityEngine;

	LaborerBaseInformation detalleObrero;
	FilterTable tableObrero;
	Label label1,label2;
	Label fullname, rut, job, photo;

	public WorkerFileView(){

		setSizeFull();
		VerticalLayout obreros = drawObreros();		
		addComponent(obreros);
		setExpandRatio(obreros, 0.2F);

		TabSheet tab = new TabSheet();

		tab.addTab(drawOverview(),"Resumen");
		tab.addTab(drawDetalleObrero(),"Ficha");

		addComponent(tab);
		setExpandRatio(tab, 0.8F);		
	}

	private com.vaadin.ui.Component drawOverview() {

		historyContainer.addNestedContainerProperty("constructionsite.name");

		fullname = new Label();
		rut = new Label();
		job = new Label();
		photo = new Label();
		
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.setSizeFull();

		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();
		vl.addComponent(hl);

		label1 = new Label("",ContentMode.HTML);
		hl.addComponent(label1);
		hl.setExpandRatio(label1,0.8f);

		label2 = new Label("",ContentMode.HTML);
		hl.addComponent(label2);
		hl.setExpandRatio(label2,0.2f);

		//VerticalLayout vh = new VerticalLayout();
		GridLayout vh = new GridLayout(2,3);
		vh.setWidth("100%");
		vh.setSpacing(true);
		vl.addComponent(vh);

		fullname.addStyleName("title-summary");
		vh.addComponent(fullname);
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		// Image as a file resource
		FileResource resource = new FileResource(new File(basepath + "/WEB-INF/images/1.JPG" /*+photo.getValue()*/));
		logger.debug("xSS"+photo);
		Embedded image = new Embedded("", resource);
		image.setWidth("100");
		image.setHeight("100");
		vh.addComponent(image);     
		vh.setComponentAlignment(image, Alignment.MIDDLE_RIGHT);

		
		vh.addComponent(rut);
		vh.addComponent(job);

		Table table = new Table();
		table.addStyleName("table-summary");
//		table.addGeneratedColumn("endingDate", new Table.ColumnGenerator() {
//
//			@Override
//			public Object generateCell(Table source, Object itemId, Object columnId) {
//				Property endingDateProp = source.getItem(itemId).getItemProperty(columnId);
//				if(endingDateProp.getValue() == null)
//					return "Sin fecha de termino";
//				else 
//					return endingDateProp.getValue();
//			}
//		});
		
		table.addGeneratedColumn("contracts", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				
				List<Contract> contracts = (List<Contract>) source.getContainerProperty(itemId, columnId).getValue();
				
//				VerticalLayout vl = new VerticalLayout();
//				vl.setSpacing(true);
//				vl.setMargin(true);
				
				GridLayout vl = new GridLayout(3,contracts.size());
				vl.setSpacing(true);
//				vl.setMargin(true);
				
				StringBuilder sb = new StringBuilder();
				if(contracts == null || contracts.isEmpty()){
					sb.append("Sin contratos");
				}else{
					for(Contract contract : contracts){
						if(contract.isActive())
							job.setValue(contract.getJob().toString());
						sb.append(contract.getJob().toString()).append("(").append(contract.getJobCode()).append(") ");
						vl.addComponent(new Label(sb.toString(),ContentMode.HTML));
						vl.addComponent(new Label(contract.getStep(),ContentMode.HTML));
						vl.addComponent(new Label(contract.isActive() ? "Activo" : "Inactivo",ContentMode.HTML));
						sb.setLength(0);
					}
				}
				return vl;
			}
		});
		
		table.addGeneratedColumn("numberOfAccidents", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				List<Vacation> vacations = (List<Vacation>) source.getItem(itemId).getItemProperty("vacations").getValue();
				StringBuilder sb = new StringBuilder();
				if(vacations == null || vacations.isEmpty()){
					sb.append("0");
				}else{
					sb.append(vacations.size());
				}
				return new Label(sb.toString());
			}
		});
		
		table.addGeneratedColumn("averageWage", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				//TODO Calcular jornal promedio
				return new Label(Utils.random(7000, 15000)+"");
			}
		});

		table.setContainerDataSource(historyContainer);
		table.addGeneratedColumn("active", new Table.ColumnGenerator() {
			
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				return (Short)source.getItem(itemId).getItemProperty(columnId).getValue() == 1  ? "Activo":"No activo";
			}
		});
		table.setWidth("100%");
		table.setHeight("250");
		table.setVisibleColumns("constructionsite.name","averageWage","reward","numberOfAccidents","contracts","active"//,"startingDate","endingDate"
				);
		table.setColumnHeaders("Obra","Jornal Promedio","Premio","N° Accidentes","Oficios","Estado"//,"Fecha Inicio","Fecha Termino"
				);
		
		table.addItemClickListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				final BeanItem<LaborerConstructionsite> beanItem = (BeanItem<LaborerConstructionsite>) event.getItem();
				LaborerConstructionDialog userWindow = new LaborerConstructionDialog(beanItem,service,velocityEngine,true);
		        UI.getCurrent().addWindow(userWindow);
			}
		});
		
		vl.addComponent(table);

		return vl;
	}

	private com.vaadin.ui.Component drawDetalleObrero() {
		//crea un objeto vacio para que cree bien la interfaz
		fieldGroup.setItemDataSource(new BeanItem<Laborer>(new Laborer()));
		detalleObrero = new LaborerBaseInformation(fieldGroup, false);
		detalleObrero.setMargin(true);
		return detalleObrero;
	}


	private void setLaborer(BeanItem<Laborer> laborerItem){

		if(laborerItem == null){
			detalleObrero.setEnabled(false);
			return;
		}

		detalleObrero.setEnabled(true);
		fieldGroup.setItemDataSource(laborerItem);

		//obtiene las obras historicas
//		List<HistoryVO> history = service.getLaborerHistory(laborerItem.getBean());
//		historyContainer.removeAllItems();
//		historyContainer.addAll(history);

		List<LaborerConstructionsite> history = service.findAllLaborerConstructionsiteByLaborer(laborerItem.getBean());
		historyContainer.removeAllItems();
		historyContainer.addAll(history);
		
		fullname.setValue(laborerItem.getBean().getFullname());
		rut.setValue(laborerItem.getBean().getRut());
		photo.setValue(laborerItem.getBean().getPhoto());

		
		//		//obtiene el vertical Layout
		//		detalleObrero.removeAllComponents();
		//		if(laborerItem == null){
		//			detalleObrero.addComponent(new Label("Seleccione un obrero para ver su información"));
		//			return;
		//		}
		//		
		//		HorizontalLayout hl = new HorizontalLayout();
		//		hl.setSpacing(true);
		//		detalleObrero.addComponent(hl,0,0,1,0);
		//		detalleObrero.setComponentAlignment(hl, Alignment.TOP_RIGHT);
		//		
		//		//define la información de resumen
		//		LaborerConstructionsite lc = laborerItem.getBean();
		//		Laborer laborer = lc.getLaborer();
		//        //obtiene las obras historicas
		//        List<HistoryVO> history = service.getLaborerHistory(laborer);
		//        historyContainer.removeAllItems();
		//        historyContainer.addAll(history);
		//        
		//		label1.setValue("<h1><b>"+laborer.getRut()+" : "+laborer.getFullname()+"</b></h1>");
		////		label2.setValue("<h1 style='margin-bottom: 1px;' ><b>Activo </h1></b><span >"+laborer.getConstructionSites().get(0).getName()+"</span>");
		//		
		//		
		//        fieldGroup.setItemDataSource(laborerItem);
		//        
		//
		//        //agrega un boton que hace el commit
		//        Button add = new Button(null,new Button.ClickListener() {
		//
		//        	@Override
		//        	public void buttonClick(ClickEvent event) {
		//        		try {
		//        			fieldGroup.commit();
		//        			service.saveLaborer(fieldGroup.getItemDataSource().getBean());
		//        		} catch (CommitException e) {
		//        			logger.error("Error al guardar la información del obrero");
		//        			Notification.show("Error al guardar la información del obrero", Type.ERROR_MESSAGE);
		//        		}
		//
		//        	}
		//        }){{
		//        	setIcon(FontAwesome.SAVE);
		//        }};
		//        hl.addComponent(add);
		//        
		//		//boton para imprimir
		//		Button btnPrint = new Button(null,new Button.ClickListener() {
		//			
		//			@Override
		//			public void buttonClick(ClickEvent event) {
		//				Notification.show("Imprimiendo");
		//				
		//			}
		//		}){{
		//			setIcon(FontAwesome.PRINT);
		//		}};
		//		 hl.addComponent(btnPrint);
		//		//detalleObrero.addComponent(btnPrint);
		//		//detalleObrero.setComponentAlignment(btnPrint, Alignment.TOP_LEFT);        
		//        // Loop through the properties, build fields for them and add the fields
		//        // to this UI
		//		 for (Object propertyId : new String[]{"rut","firstname","secondname","lastname", "secondlastname", "dateBirth", "address", "mobileNumber", "phone", "dateAdmission"}) {
		//        	if(propertyId.equals("laborerId") || propertyId.equals("constructionSites") || propertyId.equals("contractId") || propertyId.equals("teamId"))
		//        		;
		//        	else if(propertyId.equals("afp")){
		//        		ComboBox afpField = new ComboBox("AFP");
		//        		afpField.setNullSelectionAllowed(false);
		//    			for(Afp a : Afp.values()){
		//    				afpField.addItem(a);
		//    			}
		//    			detalleObrero.addComponent(afpField);
		//    			fieldGroup.bind(afpField, "laborer.afp");    			
		//        	}else if(propertyId.equals("job")){
		//        		ComboBox jobField = new ComboBox("Oficio");
		//        		jobField.setNullSelectionAllowed(false);
		//    			for(Job j : Job.values()){
		//    				jobField.addItem(j);
		//    			}
		//    			detalleObrero.addComponent(jobField);
		//    			fieldGroup.bind(jobField, "job");    
		//        	}else if(propertyId.equals("maritalStatus")){
		//        		ComboBox msField = new ComboBox("Estado Civil");
		//        		msField.setNullSelectionAllowed(false);
		//    			for(MaritalStatus ms : MaritalStatus.values()){
		//    				msField.addItem(ms);
		//    			}
		//    			detalleObrero.addComponent(msField);
		//    			fieldGroup.bind(msField, "laborer.maritalStatus");    
		//        	}else{        		
		//        		String t = tradProperty(propertyId);
		//        		Field field = fieldGroup.buildAndBind(t, "laborer."+propertyId);
		//        		if(field instanceof TextField){
		//        			((TextField)field).setNullRepresentation("");
		//        		}
		//        		detalleObrero.addComponent(field);
		//        		detalleObrero.setComponentAlignment(field, Alignment.MIDDLE_CENTER);
		//        	}
		//        }
		//        
		//        detalleObrero.setWidth("100%");
	}

	private FilterTable drawTablaObreros() {
		tableObrero =  new FilterTable();
		tableObrero.setContainerDataSource(laborerContainer);
		tableObrero.setSizeFull();
		tableObrero.setFilterBarVisible(true);
		tableObrero.setVisibleColumns("rut","fullname");
		tableObrero.setColumnHeaders("RUT", "Nombre");
		tableObrero.setSelectable(true);

		tableObrero.addItemClickListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
				setLaborer((BeanItem<Laborer>)event.getItem());
			}
		});

		return tableObrero;
	}

	private VerticalLayout drawObreros() {

		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);

		//la tabla con su buscador buscador
		vl.addComponent(drawTablaObreros());
		//botones agrega y eliminar
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		vl.addComponent(hl);
		vl.setComponentAlignment(hl, Alignment.BOTTOM_CENTER );
		Button agregaObrero = new Button(null,FontAwesome.PLUS);
		//agregando obras dummy
		agregaObrero.addClickListener(new Button.ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -5582668940084219150L;			

			@Override
			public void buttonClick(ClickEvent event) {

//				LaborerConstructionsite lc = new LaborerConstructionsite();
//				Laborer l = new Laborer();
//				l.setFirstname("Nuevo Trabajador");
//				l.setLastname("");
//				l.setRut("");
//				//l.setAfp(Afp.CAPITAL);
//				l.setAddress("");
//				//l.setJob(Job.ALBAÑIL);
//				//l.setMaritalStatus(MaritalStatus.CASADO);
//				l.setPhone("");
//				l.setMobileNumber("");
//				l.setSecondlastname("");
//				l.setSecondname("");
//
//				service.saveLaborer(l);
//				//laborerContainer.addBean(l);
//				lc.setLaborer(l);
//				BeanItem<LaborerConstructionsite> item = laborerContainer.addBean(lc);
//				setLaborer(item);
			}
		});

		hl.addComponent(agregaObrero);
		Button borrarObrero = new Button(null,FontAwesome.TRASH_O);
		borrarObrero.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				//recupera el elemento seleccionado
				Laborer l = (Laborer) tableObrero.getValue();
				if(l == null){
					Notification.show("Debe seleccionar un obrero para eliminarlo");
					return;
				}
				//TODO dialogo de confirmación
				service.delete(l);
				laborerContainer.removeItem(l);

				setLaborer(null);
			}
		});
		hl.addComponent(borrarObrero);

		return vl;
	}

	@PostConstruct
	private void init(){
	}

	@Override
	public void enter(ViewChangeEvent event) {
		((MagalUI)UI.getCurrent()).setBackVisible(false);
		((MagalUI)UI.getCurrent()).highlightMenuItem(NAME);
		//setea el nombre de la sección
		((MagalUI)UI.getCurrent()).getTitle().setValue("<h1>Histórico</h1>");

		reloaData();		
	}

	public void reloaData(){
		Page<Laborer> page = service.findAllLaborer(new PageRequest(0, 200));
		laborerContainer.removeAllItems();
		laborerContainer.addAll(page.getContent());

		setLaborer( laborerContainer.getItem( laborerContainer.firstItemId() ));
		tableObrero.select(laborerContainer.firstItemId());
	}
}
