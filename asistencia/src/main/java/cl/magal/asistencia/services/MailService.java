package cl.magal.asistencia.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import cl.magal.asistencia.entities.ConstructionSite;
import cl.magal.asistencia.entities.User;
import cl.magal.asistencia.entities.enums.Permission;
import cl.magal.asistencia.repositories.UserRepository;

@Service
public class MailService {

	Logger logger = LoggerFactory.getLogger(MailService.class);

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private VelocityEngine velocityEngine;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private TaskExecutor taskExecutor;


	/**
	 * Enviar un a todos los usuarios con permiso confirmación central avisando que el 
	 * administrador de obra confirmó la asistencia del mes
	 */
	public void sendSalaryConfirmationEmail(final ConstructionSite constructionsite){

		//realiza las llamadas asincronicamente
		taskExecutor.execute(new Runnable() {

			@Override
			public void run() {
				//obtiene todos los usuarios que tengan el permiso de confirmar obra
				final List<User> users =  userRepo.findAllByPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL);

				for(final User user : users ){
					MimeMessagePreparator preparator = new MimeMessagePreparator() {
						public void prepare(MimeMessage mimeMessage) throws Exception {
							MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
							message.setTo(user.getEmail());
							message.setFrom("noreply@magal.cl");
							Map model = new HashMap();
							model.put("user", user);
							model.put("constructionsite", constructionsite);
							String text = VelocityEngineUtils.mergeTemplateIntoString(
									velocityEngine, "templates/mail/salary_confirmation_mail.vm","UTF-8", model);
							message.setText(text, true);
						}
					};
					mailSender.send(preparator);
				}
			}
		});



	}

	public void sendSupleConfirmationEmail(final ConstructionSite constructionsite){

		//realiza las llamadas asincronicamente
		taskExecutor.execute(new Runnable() {

			@Override
			public void run() {
				//obtiene todos los usuarios que tengan el permiso de confirmar obra
				final List<User> users =  userRepo.findAllByPermission(Permission.CONFIRMAR_ASISTENCIA_CENTRAL);

				for(final User user : users ){
					MimeMessagePreparator preparator = new MimeMessagePreparator() {
						public void prepare(MimeMessage mimeMessage) throws Exception {
							MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
							message.setTo(user.getEmail());
							message.setFrom("noreply@magal.cl");
							Map model = new HashMap();
							model.put("user", user);
							model.put("constructionsite", constructionsite);
							String text = VelocityEngineUtils.mergeTemplateIntoString(
									velocityEngine, "templates/mail/suple_confirmation_mail.vm","UTF-8", model);
							message.setText(text, true);
						}
					};
					mailSender.send(preparator);
				}
			}
		});
	}
}
