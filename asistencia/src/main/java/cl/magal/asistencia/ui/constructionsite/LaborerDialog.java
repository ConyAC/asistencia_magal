package cl.magal.asistencia.ui.constructionsite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cl.magal.asistencia.services.LaborerService;
import cl.magal.asistencia.ui.AbstractWindowEditor;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;

public class LaborerDialog extends AbstractWindowEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6159862066608506007L;

	transient Logger logger = LoggerFactory.getLogger(LaborerDialog.class);

	@Autowired
	transient LaborerService service;
	
	public LaborerDialog(BeanItem<?> item,LaborerService service ){
		super(item);
		if(service == null )
			throw new RuntimeException("Error al crear el dialgo, el servicio de trabajadores no puede ser nulo.");

		this.service = service;
		init();
	}

	public void init(){
		super.init();
	}

	@Override
	protected Component createBody() {
		return new LaborerBaseInformation(getBinder(), false);
	}
	
	@Override
	protected boolean preCommit() {
		return super.preCommit();
	}
	
	@Override
	protected boolean preDiscard() {
		return super.preDiscard();
	}
	
}
