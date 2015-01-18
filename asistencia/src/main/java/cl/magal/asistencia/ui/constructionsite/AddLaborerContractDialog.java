package cl.magal.asistencia.ui.constructionsite;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;

import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.entities.Laborer;
import cl.magal.asistencia.entities.LaborerConstructionsite;
import cl.magal.asistencia.entities.enums.Job;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.ui.AbstractWindowEditor;
import cl.magal.asistencia.ui.MagalUI;
import cl.magal.asistencia.util.Constants;
import cl.magal.asistencia.util.SecurityHelper;
import cl.magal.asistencia.util.Utils;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class AddLaborerContractDialog extends AbstractWindowEditor implements NewItemHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3250481772094615264L;

	transient Logger logger = LoggerFactory.getLogger(AddLaborerContractDialog.class);

	transient LaborerService laborerService;
	BeanItemContainer<Laborer> laborers = new BeanItemContainer<Laborer>(Laborer.class);
	boolean addLaborer = false;
	
	transient private VelocityEngine velocityEngine;
	
	protected AddLaborerContractDialog(BeanItem<?> item,LaborerService laborerService,boolean addLaborer) {
		super(item);
		this.laborerService= laborerService;
		this.addLaborer = addLaborer;
		init();
	}

	public void init(){
		
		velocityEngine = (VelocityEngine) ((MagalUI)UI.getCurrent()).getSpringBean(Constants.VELOCITY_ENGINE_BEAN);
		
		setWidth("50%");
		setHeight("300px");
		
		if(addLaborer)
			laborers.addAll( laborerService.getAllLaborerExceptThisConstruction(((BeanItem<LaborerConstructionsite>)getItem()).getBean().getConstructionsite()));
		else {
			laborers.addBean(((LaborerConstructionsite) getItem().getBean()).getLaborer());
		}
		logger.debug("laborers {}",laborers);
		//cambia el texto del guardar
		if(addLaborer)
			getBtnGuardar().setCaption("Agregar Trabajador");
		else
			getBtnGuardar().setCaption("Agregar Contrato");
		
		getBtnGuardar().setIcon(FontAwesome.PLUS_CIRCLE);
		super.init();
	}
	
	ComboBox cb,cbJob,cbStep;
	TextField lbCodJob;
	
	@Override
	protected Component createBody() {
		
		GridLayout gl = new GridLayout(3,3);
		gl.setSpacing(true);
		gl.setMargin(true);
		
		cb = new ComboBox("Rut trabajador:",laborers);
		gl.addComponent(cb);
		cb.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cb.setItemCaptionPropertyId("rut");
		cb.setTabIndex(1);
		
		cb.setEnabled(addLaborer);

		final TextField lbNombre = new TextField("Nombre:");
		if(!addLaborer){
			cb.select(laborers.firstItemId());
			Utils.setLabelValue( lbNombre , laborers.firstItemId().getFullname());
		}

		lbNombre.setReadOnly(true);
		final Button btn = new Button(null,FontAwesome.FLOPPY_O );
		btn.setEnabled(addLaborer);
		btn.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(cb.getValue() == null){
					Notification.show("Debe ingresar un rut para guardar y/o modificar el trabajador.",Type.ERROR_MESSAGE);
					return;
				}
				
				final Object itemId =  cb.getValue();
				logger.debug("itemIds {} ",laborers.getItemIds());
				final BeanItem<Laborer> beanItem= laborers.getItem( itemId );
				LaborerDialog dialog = new LaborerDialog(beanItem, laborerService);
				dialog.addListener(new EditorSavedListener() {
					
					@Override
					public void editorSaved(EditorSavedEvent event) {
						
						Laborer laborer = beanItem.getBean();
						//guarda al trabajador
						laborerService.saveLaborer(laborer);
						//refresca el item en el combobox
						laborers.removeAllItems();
						laborers.addAll( laborerService.getAllLaborerExceptThisConstruction(((BeanItem<LaborerConstructionsite>)getItem()).getBean().getConstructionsite()));
						Utils.setLabelValue( lbNombre , laborer.getFullname());
						if ( laborer.getLaborerId() != null )
							btn.setIcon(FontAwesome.PENCIL);
						else
							btn.setIcon(FontAwesome.FLOPPY_O );
						
					}
				});
				UI.getCurrent().addWindow(dialog);
			}
		});
		btn.setTabIndex(2);
		
		cb.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Laborer laborer = (Laborer)cb.getValue();
				if(laborer != null){
					Utils.setLabelValue( lbNombre , laborer.getFullname());
					if ( laborer.getLaborerId() != null )
						btn.setIcon(FontAwesome.PENCIL);
					else
						btn.setIcon(FontAwesome.FLOPPY_O );
				}
			}
		});
		cb.setNewItemsAllowed(true);
		cb.setNewItemHandler(this);
		cb.focus();
		
		gl.addComponent(lbNombre);
		gl.setComponentAlignment(lbNombre, Alignment.BOTTOM_LEFT);
		
		gl.addComponent(btn);
		gl.setComponentAlignment(btn, Alignment.BOTTOM_RIGHT);
		//campos asociados a la obra/contrato
		cbJob = new ComboBox("Oficio");
		cbJob.setTabIndex(3);
		cbJob.setRequired(true);
		cbJob.setRequiredError("Debe definir un oficio");
		
		for(Job j : Job.values()){
			cbJob.addItem(j);
		}
		
		gl.addComponent(cbJob);
		
		// codigo por asignar
		lbCodJob = new TextField("Código Trabajador");
		lbCodJob.setReadOnly(true);
		gl.addComponent(lbCodJob);
		gl.setComponentAlignment(lbCodJob, Alignment.BOTTOM_LEFT);
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
		gl.addComponent(cbStep);
		
		return gl;
	}

	@Override
	public void addNewItem(String newItemCaption) {
		//crea
		logger.debug("se llamo add new item con {}",newItemCaption);
		//quita el laborer sin id, para no juntar basura
		for(Laborer itemId : new ArrayList<Laborer>(laborers.getItemIds())){
			if(itemId.getLaborerId() == null){
				laborers.removeItem(itemId);
			}
		}
		Laborer laborer = new Laborer();
		laborer.setRut(newItemCaption);
		laborer.setFirstname("Nuevo trabajador");
		laborers.addBean(laborer);
		cb.select(laborer);
	}
	
	@Override
	protected boolean preCommit() {
		String msj = null;
		//valida que el trabajador seleccionado éste creado
		Laborer laborer = (Laborer) cb.getValue();
		Job job = (Job) cbJob.getValue();
		if( !cb.isValid() )
			msj = cb.getRequiredError();
		else if( laborer.getLaborerId() == null )
			msj = "Debe crear el trabajador antes de agregarlo";
		else if( !cbJob.isValid() )
			msj = cbJob.getRequiredError();
		else if( !cbStep.isValid() )
			msj = cbStep.getRequiredError();
		
		if(msj != null)
			Notification.show(msj,Type.ERROR_MESSAGE);
		else{ //si pasa todas las validaciones, lo agrega al item para guardar
			if(addLaborer){
				//dependiendo del perfil lo marca como confirmado o no
				((BeanItem<LaborerConstructionsite>) getItem()).getBean().setConfirmed( SecurityHelper.hastPermission(Permission.CONFIRMAR_OBREROS) );
				((BeanItem<LaborerConstructionsite>) getItem()).getBean().setLaborer(laborer);
			}
			
			//crea el contrato
			Contract contract = new Contract();
			contract.setStartDate(new Date());
			contract.setName(laborer.getFullname());
			contract.setJob(job);
			contract.setJobCode(Integer.valueOf(lbCodJob.getValue()));
			contract.setStep((String) cbStep.getValue());
			contract.setActive(true);
			((BeanItem<LaborerConstructionsite>) getItem()).getBean().refreshActiveContract();
			((BeanItem<LaborerConstructionsite>) getItem()).getBean().addContract(contract);
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
		input.put("tools", new DateTool());
		
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
