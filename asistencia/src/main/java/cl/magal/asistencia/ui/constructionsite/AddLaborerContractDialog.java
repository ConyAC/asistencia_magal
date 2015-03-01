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

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.entities.validator.RutDigitValidator;
import cl.magal.asistencia.services.ConstructionSiteService;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.ui.MagalUI;
import cl.magal.asistencia.util.Constants;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.Utils;
import cl.magal.asistencia.util.VelocityHelper;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
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
	BeanItemContainer<Laborer> laborersBC = new BeanItemContainer<Laborer>(Laborer.class);
	boolean addLaborer = false;
	transient private VelocityEngine velocityEngine;
	
	ComboBox cbRut,cbJob,cbStep;
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
		setWidth("70%");
		setHeight("80%");
		
		if(addLaborer){
//			List<Laborer> laborers = laborerService.getAllLaborerExceptThisConstruction(((BeanItem<LaborerConstructionsite>)getItem()).getBean().getConstructionsite());
			List<Laborer> l = laborerService.findAllLaborer();
			laborersBC.addAll(l);
		}else {
			laborersBC.addBean(((LaborerConstructionsite) getItem().getBean()).getLaborer());
		}
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
		
		for(Job j : Job.values()){
			cbJob.addItem(j);
		}
		
		gl.addComponent(cbJob,0,rows);
		gl.setComponentAlignment(cbJob, Alignment.MIDDLE_CENTER);	
		
		// codigo por asignar
		lbCodJob = new TextField("Código Asignado a Trabajador");
		lbCodJob.setReadOnly(true);
		gl.addComponent(lbCodJob,1,rows++);
		gl.setComponentAlignment(lbCodJob, Alignment.MIDDLE_CENTER);	
		
		//cada vez que cambia el job, calcula el siguiente codigo
		cbJob.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				String newCode = laborerService.getNextJobCode((Job) event.getProperty().getValue() ,((BeanItem<LaborerConstructionsite>) getItem()).getBean().getConstructionsite() )+"";
				Utils.setLabelValue(lbCodJob,newCode);
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
			if(itemId.getLaborerId() == null){
				laborersBC.removeItem(itemId);
			}
		}
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
		Job job = (Job) cbJob.getValue();
		
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
			
		}
		
		
		if(msj != null)
			Notification.show(msj,Type.ERROR_MESSAGE);
		else{ //si pasa todas las validaciones, lo agrega al item para guardar
			if(addLaborer){
				//dependiendo del perfil lo marca como confirmado o no
				((BeanItem<LaborerConstructionsite>) getItem()).getBean().setConfirmed( SecurityHelper.hasPermission(Permission.CONFIRMAR_OBREROS) );
				((BeanItem<LaborerConstructionsite>) getItem()).getBean().setLaborer(laborer);
			}
			
			//crea el contrato
			Contract contract = new Contract();
			contract.setStartDate(dfAdmissionDate.getValue());
			contract.setName(laborer.getFullname());
			contract.setJob(job);
			contract.setJobCode(Integer.valueOf(lbCodJob.getValue()));
			contract.setStep((String) cbStep.getValue());
			contract.setActive(true);
			
			((BeanItem<LaborerConstructionsite>) getItem()).getBean().setActiveContract(contract);
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
		else if(propertyId.equals("town"))
			return "Ciudad";
		else if(propertyId.equals("commune"))
			return "Comuna";
		else if(propertyId.equals("wedge"))
			return "Calzado";
		else if(propertyId.equals("bankAccount"))
			return "Cta. Banco";
		else
			return propertyId.toString();
	}

}
