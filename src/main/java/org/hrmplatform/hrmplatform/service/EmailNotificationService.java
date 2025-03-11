package org.hrmplatform.hrmplatform.service;

import org.hrmplatform.hrmplatform.config.AppConfig;
import org.hrmplatform.hrmplatform.entity.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {
	
	private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);
	
	private final EmailService emailService;
	private final AppConfig appConfig;
	
	public EmailNotificationService(EmailService emailService, AppConfig appConfig) {
		this.emailService = emailService;
		this.appConfig = appConfig;
	}
	
	public void notifyAdminAndApplicant(Company company) {
		try {
			String verificationLink =
					appConfig.getBaseUrl() + "/v1/api/company/verify-email?token=" + company.getEmailVerificationToken();
			String siteAdminEmail = appConfig.getSiteAdminEmail();
			
			// Site Admin'e başvuru bildirimi gönder
			emailService.sendEmail(siteAdminEmail,
			                       "Yeni Şirket Başvurusu",
			                       "Yeni bir şirket başvurusu var. " );
			log.info("Site Admin'e bilgilendirme e-postası gönderildi.");
			
			// Başvuran şirkete bilgilendirme e-postası gönder
			String applicantEmail = company.getEmail();
			emailService.sendEmail(applicantEmail,
			                       "Şirket Başvurunuzu Doğrulayın",
			                       "Başvurunuz alınmıştır.\nŞirket başvurunuzu tamamlamak için aşağıdaki linke " +
					                       "tıklayın:\n\n" + verificationLink);
			log.info("Başvuran şirkete bilgilendirme e-postası gönderildi.");
			
		} catch (Exception e) {
			log.error("Admin ve başvuran kişiye e-posta gönderirken hata oluştu: {}", e.getMessage(), e);
		}
	}
}