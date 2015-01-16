package cl.magal.asistencia.ui.constructionsite;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.magal.asistencia.entities.Annexed;
import cl.magal.asistencia.entities.Contract;
import cl.magal.asistencia.ui.AbstractWindowEditor;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;

public class AddAnnexedContractDialog extends AbstractWindowEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3250481772094615264L;

	Logger logger = LoggerFactory.getLogger(AddAnnexedContractDialog.class);

	protected AddAnnexedContractDialog(BeanItem<?> item) {
		super(item);
		init();
	}

	public void init(){
		
		setWidth("20%");
		setHeight("20%");
		
		//cambia el texto del guardar
		getBtnGuardar().setCaption("Agregar Anexo");
		getBtnGuardar().setIcon(FontAwesome.PLUS_CIRCLE);
		super.init();
	}
	
	ComboBox lbStep;
	
	@Override
	protected Component createBody() {
		
		GridLayout gl = new GridLayout();
		gl.setSpacing(true);
		gl.setMargin(true);
		
		
		//text de etapa
		lbStep = new ComboBox("Etapa",((BeanItem<Contract>) getItem()).getBean().getLaborerConstructionSite().getConstructionsite().getSteps());
		lbStep.setImmediate(true);
		lbStep.setTabIndex(4);
		lbStep.setRequired(true);
		lbStep.setRequiredError("Debe definir una etapa");
		
		lbStep.focus();
		gl.addComponent(lbStep);
		
		return gl;
	}

	@Override
	protected boolean preCommit() {
		String msj = null;
		//valida que el trabajador seleccionado éste creado
		if( !lbStep.isValid() )
			msj = lbStep.getRequiredError();
		if(msj != null)
			Notification.show(msj,Type.ERROR_MESSAGE);
		else{ //si pasa todas las validaciones, lo agrega al item para guardar
			
			//crea el contrato
			Annexed annexed = new Annexed(); 
			annexed.setStartDate(new Date());
			annexed.setStep((String) lbStep.getValue());

			((BeanItem<Contract>) getItem()).getBean().addAnnexed(annexed);
		}
		
		return msj == null;
	}

}
