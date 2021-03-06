package cl.magal.asistencia.ui.constructionsite;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;

import cl.magal.asistencia.entities.AdvancePaymentConfigurations;
import cl.magal.asistencia.entities.AdvancePaymentItem;
import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.Speciality;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.entities.validator.RutDigitValidator;
import cl.magal.asistencia.services.ConfigurationService;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.ui.MagalUI;
import cl.magal.asistencia.util.Constants;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.Utils;
import cl.magal.asistencia.util.VelocityHelper;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AddLaborerContractDialog extends AbstractWindowEditor implements NewItemHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3250481772094615264L;

	transient Logger logger = LoggerFactory.getLogger(AddLaborerContractDialog.class);

	transient LaborerService laborerService;
	transient ConstructionSiteService constructionSiteService;
	transient ConfigurationService configurationService;
	BeanItemContainer<Laborer> laborersBC = new BeanItemContainer<Laborer>(Laborer.class);
	BeanItemContainer<Speciality> specialityBC = new BeanItemContainer<Speciality>(Speciality.class);
	boolean addLaborer = false;
	transient private VelocityEngine velocityEngine;
	
	ComboBox cbRut,cbJob,cbSpeciality,cbStep;
	TextField lbCodJob;
	DateField dfAdmissionDate;
	
	protected AddLaborerContractDialog(BeanItem<?> item,boolean addLaborer) {
		super(item);
		this.addLaborer = addLaborer;
		init();
	}

	public void init(){
		
		velocityEngine = (VelocityEngine) ((MagalUI)UI.getCurrent()).getSpringBean(Constants.VELOCITY_ENGINE_BEAN);
		laborerService = (LaborerService) ((MagalUI)UI.getCurrent()).getSpringBean(Constants.LABORER_SERVICE_BEAN);
		constructionSiteService = (ConstructionSiteService) ((MagalUI)UI.getCurrent()).getSpringBean(Constants.CONSTRUCTIONSITE_SERVICE_BEAN);
		configurationService= (ConfigurationService) ((MagalUI)UI.getCurrent()).getSpringBean(Constants.CONFIGURATION_SERVICE_BEAN);
		setWidth("70%");
		setHeight("80%");
		
		if(addLaborer){
//			List<Laborer> laborers = laborerService.getAllLaborerExceptThisConstruction(((BeanItem<LaborerConstructionsite>)getItem()).getBean().getConstructionsite());
			List<Laborer> l = laborerService.findAllLaborer();
			laborersBC.addAll(l);
		}else {
			laborersBC.addBean(((LaborerConstructionsite) getItem().getBean()).getLaborer());
		}
		//agrega las especialidad
		specialityBC.addAll( constructionSiteService.findSpecialitiesByConstructionSite(((LaborerConstructionsite) getItem().getBean()).getConstructionsite()) );
		//cambia el texto del guardar
		if(addLaborer)
			getBtnGuardar().setCaption("Guardar");
		else
			getBtnGuardar().setCaption("Agregar Contrato");
		
		getBtnGuardar().setIcon(FontAwesome.FLOPPY_O);
		super.init();
	}
	
	@Override
	protected Component createBody() {
		
		GridLayout gl = new GridLayout(3,20);
		gl.setWidth("100%");
		gl.setHeightUndefined();
		gl.setSpacing(true);
		gl.setMargin(true);
		
		cbRut = new ComboBox("Rut trabajador:",laborersBC);
		gl.addComponent(cbRut,0,0,2,0);
		gl.setComponentAlignment(cbRut, Alignment.MIDDLE_CENTER);
		cbRut.setInputPrompt("Ej.: 12345678-9");
		cbRut.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbRut.setItemCaptionPropertyId("rut");
		cbRut.setTabIndex(1);
		cbRut.setEnabled(addLaborer);
		
		gl.addComponent(new Label("<hr />",ContentMode.HTML),0,1,2,1);
		gl.addComponent(new LaborerBaseInformation(getBinder(),"laborer", false),0,2,2,2);
		
		cbRut.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Laborer laborer = (Laborer)cbRut.getValue();
				if(laborer != null){
					//setea la información del trabajador si ya existe
					BeanItem<LaborerConstructionsite> laborerConstructionsiteItem = ((BeanItem<LaborerConstructionsite>)getItem());
					laborerConstructionsiteItem.getItemProperty("laborer").setValue(laborer);	
					getBinder().setItemDataSource(laborerConstructionsiteItem);
					//muestra la procedencia
					if(addLaborer){
						ConstructionSite lastConstructionSite = laborerService.getLastConstructionSite(laborer);
						logger.debug("lastConstructionSite {}",lastConstructionSite);
						if(lastConstructionSite != null && laborerConstructionsiteItem.getItemProperty("laborer.provenance").getValue() == null )
							laborerConstructionsiteItem.getItemProperty("laborer.provenance").setValue(lastConstructionSite.getName());
						//valida que no tenga otra obra
						ConstructionSite cs  = laborerService.findActiveConstructionSite(laborer);
						if( cs != null ){ //si tiene otra obra, avisa y de igual forma validará antes de agregar
							Notification.show("El trabajador ya pertenece a otra obra : "+cs.getName(),Type.ERROR_MESSAGE);
						}
					}
				}
			}
		});
		cbRut.setNewItemsAllowed(true);
		cbRut.setNewItemHandler(this);
		//cb.focus(); para visualizar el texto de ayuda en el campo del rut

		//filas hasta aquí
		int rows = 3;
		
		gl.addComponent(new Label("<hr />",ContentMode.HTML),0,rows,2,rows++);
		gl.addComponent(new Label("<h2>Información de Obra : </h2>",ContentMode.HTML),0,rows,2,rows++);
		
		//campos asociados a la obra/contrato
		cbJob = new ComboBox("Oficio");
		cbJob.setTabIndex(3);
		cbJob.setRequired(true);
		cbJob.setRequiredError("Debe definir un oficio");
		
		for(Job job : Job.values()){
			cbJob.addItem(job);
		}
		
		gl.addComponent(cbJob,0,rows);
		gl.setComponentAlignment(cbJob, Alignment.MIDDLE_CENTER);	
		
		//campos asociados a la obra/contrato
		cbSpeciality = new ComboBox("Especialidad");
		cbSpeciality.setEnabled(false);
		cbSpeciality.setTabIndex(3);
		cbSpeciality.setRequired(true);
		cbSpeciality.setRequiredError("Debe definir una especialidad");
		cbSpeciality.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbSpeciality.setItemCaptionPropertyId("name");
		
		cbSpeciality.setContainerDataSource(specialityBC);
		
		gl.addComponent(cbSpeciality,1,rows);
		gl.setComponentAlignment(cbSpeciality, Alignment.MIDDLE_CENTER);	
		
		// codigo por asignar
		lbCodJob = new TextField("Rol asignado a Trabajador");
		lbCodJob.setReadOnly(true);
		gl.addComponent(lbCodJob,2,rows++);
		gl.setComponentAlignment(lbCodJob, Alignment.MIDDLE_CENTER);	
		
		// codigo por asignar
		final ComboBox cbSupleCode = new ComboBox("Código Suple");
		cbSupleCode.setRequired(true);
		gl.addComponent(cbSupleCode,0,rows++);
		gl.setComponentAlignment(cbSupleCode, Alignment.MIDDLE_CENTER);
		AdvancePaymentConfigurations supleConfigurations = configurationService.getSupleTableByCs(((BeanItem<LaborerConstructionsite>) getItem()).getBean().getConstructionsite());
		Map<Integer, AdvancePaymentItem> paymentTable = supleConfigurations.getMapTable();
		for(Integer key : paymentTable.keySet()){
			cbSupleCode.addItem(key);
		}
		
		cbSupleCode.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				getItem().getItemProperty("supleCode").setValue(cbSupleCode.getValue());
			}
		});
		
		//cada vez que cambia el job, calcula el siguiente codigo
		cbJob.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Job job = (Job) event.getProperty().getValue();
				String newCode = laborerService.getNextJobCode( job,((BeanItem<LaborerConstructionsite>) getItem()).getBean().getConstructionsite() )+"";
				Utils.setLabelValue(lbCodJob,newCode);
				//filtra la lista de especialidades
				specialityBC.removeAllContainerFilters();
				Filter filter = new Compare.Equal("job",job);
				specialityBC.addContainerFilter(filter);
				cbSpeciality.setEnabled(true);
			}
		});
		
		//text de etapa
		cbStep = new ComboBox("Etapa",((BeanItem<LaborerConstructionsite>) getItem()).getBean().getConstructionsite().getSteps());
		cbStep.setImmediate(true);
		cbStep.setTabIndex(4);
		cbStep.setRequired(true);
		cbStep.setRequiredError("Debe definir una etapa");
		gl.addComponent(cbStep,0,rows++);
		gl.setComponentAlignment(cbStep, Alignment.MIDDLE_CENTER);	
		
		dfAdmissionDate = new DateField("Fecha Ingreso : ");
		dfAdmissionDate.setRequired(true);
		dfAdmissionDate.setRequiredError("La fecha de ingreso es necesaria.");
		dfAdmissionDate.setValue(new Date());
		gl.addComponent(dfAdmissionDate,0,rows++);
		gl.setComponentAlignment(dfAdmissionDate, Alignment.MIDDLE_CENTER);	
		
		final DateField startDate = new DateField("Fecha Inicial",getItem().getItemProperty("rewardStartDate"));
		final DateField endDate = new DateField("Fecha Final",getItem().getItemProperty("rewardEndDate"));

		VerticalLayout vl = new VerticalLayout(){
			{
				setSpacing(true);
				TextField tfReward = new TextField("Premio: ",getItem().getItemProperty("reward")){{
					setNullRepresentation("");
					addValidator(new BeanValidator(LaborerConstructionsite.class,"reward"));
					setEnabled(addLaborer);
				}};
				addComponent(tfReward);
				setComponentAlignment(tfReward, Alignment.MIDDLE_CENTER);
				CheckBox checkbox = new CheckBox("Aplicar premio según fechas de contrato",getItem().getItemProperty("useDefaultDates"));
				startDate.setVisible(!checkbox.getValue());
				endDate.setVisible(!checkbox.getValue());
				checkbox.addValueChangeListener(new Property.ValueChangeListener() {

					@Override
					public void valueChange(ValueChangeEvent event) {
						boolean ischecked = (Boolean) event.getProperty().getValue();
						startDate.setVisible(!ischecked);
						endDate.setVisible(!ischecked);
						if(ischecked){
							startDate.setValue(null);
							endDate.setValue(null);
						}
					}
				});
				addComponent(checkbox);

			}
		};
		gl.addComponent( vl ,0,rows);
		gl.setComponentAlignment(vl, Alignment.MIDDLE_CENTER);
		gl.addComponent(startDate,1,rows);
		gl.setComponentAlignment(startDate, Alignment.MIDDLE_CENTER);
		gl.addComponent(endDate,2,rows);
		gl.setComponentAlignment(endDate, Alignment.MIDDLE_CENTER);
		
		return new Panel(gl){
			{
				setSizeFull();
			}
		};
	}

	@Override
	public void addNewItem(String newItemCaption) {
		//crea
		logger.debug("se llamo add new item con {}",newItemCaption);
		//quita el laborer sin id, para no juntar basura
		for(Laborer itemId : new ArrayList<Laborer>(laborersBC.getItemIds())){
			if(itemId.getId() == null){
				laborersBC.removeItem(itemId);
			}
		}
		
		//si el rut no tiene guion, lo agrega al final
		if(newItemCaption != null && !newItemCaption.contains("-"))
			newItemCaption = new StringBuilder(newItemCaption).insert(newItemCaption.length() - 1, "-").toString();
		//busca si ya existia en el combobox
		Laborer l = laborerService.findByRut(newItemCaption);
		if(l != null){
			cbRut.select(l);
			return ;
		}
		//si no existe hace todo el proceso de nuevo trabajador
		Laborer laborer = new Laborer();
		laborer.setRut(newItemCaption);
		//por defecto el nuevo trabajador tiene al menos 18 años
		laborer.setDateBirth( new DateTime().plusYears(-18).toDate() );
		laborersBC.addBean(laborer);
		cbRut.select(laborer);
	}
	
	@Override
	protected boolean preCommit() {
		String msj = null;
		//valida que el trabajador seleccionado éste creado
		Laborer laborer = (Laborer) cbRut.getValue();
		
		if( !cbRut.isValid() )
			msj = cbRut.getRequiredError();
//		//valida que no esté en la obra actual
//		boolean inThisConstruction = ((BeanItem<LaborerConstructionsite>) getItem()).getBean().getConstructionsite().getLaborers().contains(laborer);
//		if(inThisConstruction){
//			msj = "El trabajador ya pertenece a la obra";
//		}
		//valida que no tenga un contrato activo
		ConstructionSite cs  = laborerService.findActiveConstructionSite(laborer);
		if( cs != null ){
			msj = "El trabajador ya pertenece a otra obra : "+cs.getName();
		}
		
//		else if( laborer.getLaborerId() == null )
//			msj = "Debe crear el trabajador antes de agregarlo";
		
		if(msj == null ){
			RutDigitValidator rdv = new RutDigitValidator();
			boolean valid = rdv.isValid(((Laborer) cbRut.getValue()).getRut(), null);
			if(!valid){
				msj = "Rut inválido";
				cbRut.focus();
			}else if( !cbJob.isValid() )
				msj = cbJob.getRequiredError();
			else if( !cbStep.isValid() )
				msj = cbStep.getRequiredError();
			else if( !dfAdmissionDate.isValid() )
				msj = dfAdmissionDate.getRequiredError();
			else if( !cbSpeciality.isValid() )
				msj = cbSpeciality.getRequiredError();
		}
		
		Speciality speciality = (Speciality) cbSpeciality.getValue();
		if(msj != null)
			Notification.show(msj,Type.ERROR_MESSAGE);
		else{ //si pasa todas las validaciones, lo agrega al item para guardar
			if(addLaborer){
				//dependiendo del perfil lo marca como confirmado o no
				((BeanItem<LaborerConstructionsite>) getItem()).getBean().setConfirmed( SecurityHelper.hasPermission(Permission.CONFIRMAR_OBREROS) );
				((BeanItem<LaborerConstructionsite>) getItem()).getBean().setLaborer(laborer);
			}

			Job job = speciality.getJob();
			
			//crea el contrato
			((BeanItem<LaborerConstructionsite>) getItem()).getBean().setStartDate(dfAdmissionDate.getValue());
			((BeanItem<LaborerConstructionsite>) getItem()).getBean().setSpeciality(speciality);
			((BeanItem<LaborerConstructionsite>) getItem()).getBean().setJobCode(Integer.valueOf(lbCodJob.getValue()));
			((BeanItem<LaborerConstructionsite>) getItem()).getBean().setStep((String) cbStep.getValue());
			((BeanItem<LaborerConstructionsite>) getItem()).getBean().setActive(true);
			
		}
		return msj == null;
	}

	/**
	 * Luego de guardar la información, imprime los documentos asociados
	 */
	@Override
	protected boolean postCommit() {
		
		// imprime el contrato, el pacto horas extras y el acuse de recivo para ser impresos
		final Map<String, Object> input = new HashMap<String, Object>();
		input.put("laborerConstructions", new LaborerConstructionsite[] {(LaborerConstructionsite)getItem().getBean()});
		String jornalBase = Utils.getDecimalFormatSinDecimal().format(configurationService.findWageConfigurations().getMinimumWage() / 30);
		input.put("jornalBase", jornalBase);
		VelocityHelper.addTools(input);
		
		final StringBuilder sb = new StringBuilder();
		
		// contrato
		sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/temporary_work_contract_doc.vm", "UTF-8", input));
		// pacto horas extras
		sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/covenant_overtime.vm", "UTF-8", input) );
		// acuse recibo
		sb.append( VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/acknowledgment_of_receipt.vm", "UTF-8", input) );

		StreamResource.StreamSource source2 = new StreamResource.StreamSource() {

			public InputStream getStream() {
				return new ByteArrayInputStream(sb.toString().getBytes());
			}
		};
		StreamResource resource = new StreamResource(source2, "Documentos de "+((LaborerConstructionsite)getItem().getBean()).getJobCode()+".html");

		Window window = new Window();
		window.setResizable(true);
		window.setWidth("60%");
		window.setHeight("60%");
		window.center();
		window.setModal(true);
		BrowserFrame e = new BrowserFrame();
		e.setSizeFull();

		// Here we create a new StreamResource which downloads our StreamSource,
		// which is our pdf.
		// Set the right mime type
		//						        resource.setMIMEType("application/pdf");
		resource.setMIMEType("text/html");

		e.setSource(resource);
		window.setContent(e);
		UI.getCurrent().addWindow(window);
		return true;
	}

}
