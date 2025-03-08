package org.hrmplatform.hrmplatform.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.hrmplatform.hrmplatform.dto.request.CompanyDto;
import org.hrmplatform.hrmplatform.dto.response.SubscriptionResponse;
import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.entity.Employee;
import org.hrmplatform.hrmplatform.entity.User;
import org.hrmplatform.hrmplatform.entity.UserRole;
import org.hrmplatform.hrmplatform.enums.Role;
import org.hrmplatform.hrmplatform.enums.Status;
import org.hrmplatform.hrmplatform.enums.SubscriptionPlan;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.mapper.CompanyMapper;
import org.hrmplatform.hrmplatform.repository.CompanyRepository;
import org.hrmplatform.hrmplatform.util.JwtManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);
    
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final EmailService emailService; // EmailService enjekte edildi
    private final UserRoleService userRoleService;
    private final EmailNotificationService emailNotificationService;
    private final JwtManager jwtManager;
    
    @Lazy
    private final EmployeeService employeeService;
    private final UserService userService;
    
    @Value("${hrmplatform.siteAdminEmail}")
    private String siteAdminEmail; // siteAdminEmail değerini application.yml'den al
	
	public CompanyService(CompanyRepository companyRepository,
                          CompanyMapper companyMapper,
                          EmailService emailService,
                          UserRoleService userRoleService,
                          @Lazy EmployeeService employeeService,
                          UserService userService,
                          EmailNotificationService emailNotificationService,
                         JwtManager jwtManager) {
		this.companyRepository = companyRepository;
		this.companyMapper = companyMapper;
		this.emailService = emailService;
		this.userRoleService = userRoleService;
		this.employeeService = employeeService;
		this.userService = userService;
        this.emailNotificationService= emailNotificationService;
        this.jwtManager = jwtManager;
	}
	
	
	//tüm şirketleri getirme
    public List<Company> findAllCompanies() {
        return companyRepository.findAll();
    }
    
    //id'ye göre şirket bulma
    public Optional<Company> findByCompanyId(Long id) {
        return companyRepository.findById(id);
    }
    
    //şirket ekleme
    public void addCompany(@Valid CompanyDto companyDto) {
        Company company = companyMapper.fromCompanyDto(companyDto);
        
        // Benzersiz bir doğrulama tokeni oluştur
        company.setEmailVerificationToken(UUID.randomUUID().toString());
        company.setTokenExpirationTime(LocalDateTime.now().plusHours(24)); // 24 saat geçerli
        
        
        companyRepository.save(company);
        
        
        // SITE_ADMIN'e ve başvuran şirkete e-posta gönder
        emailNotificationService.notifyAdminAndApplicant(company);
    }

//     Bu kod ne yapıyor?
//     UUID.randomUUID().toString() ile rastgele bir doğrulama kodu üretiyoruz.
//    tokenExpirationTime ile 24 saatlik süre veriyoruz.
//    Kullanıcının e-posta adresine doğrulama linki gönderiyoruz.
    
    /* public void updateCompany(Long id, @Valid CompanyDto companyDto) {
         Company company = companyRepository.findById(id)
                 .orElseThrow(() -> new RuntimeException("Şirket bulunamadı"));
         // Mevcut company nesnesini güncelle
         companyMapper.updateCompanyFromDto(companyDto, company);

         companyRepository.save(company);
     }*/
    /*
    public Company updateCompany(Long id, CompanyDto dto) {
           // Mevcut şirketi bul
           Company existingCompany = companyRepository.findById(id)
                   .orElseThrow(() -> new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND));

           // DTO'dan gelen değerleri mevcut şirkete aktar
           if (dto.name() != null) {
               existingCompany.setName(dto.name());
           }

           if (dto.address() != null) {
               existingCompany.setAddress(dto.address());
           }

           if (dto.phone() != null) {
               existingCompany.setPhone(dto.phone());
           }

           if (dto.email() != null) {
               existingCompany.setEmail(dto.email());
           }

           if (dto.subscriptionPlan() != null) {
               existingCompany.setSubscriptionPlan(dto.subscriptionPlan());
           }

           // Güncellenmiş şirketi kaydet ve döndür
           return companyRepository.save(existingCompany);
       }*/
//şirket güncelleme
    public Company updateCompany(Long id, CompanyDto dto) {
        // Mevcut şirketi bul
        Company existingCompany = companyRepository.findById(id)
                                                   .orElseThrow(() -> new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND));
        
        // DTO'dan entity'ye güncelleme yap
        companyMapper.updateCompanyFromDto(dto, existingCompany);
        
        // Güncellenmiş şirketi kaydet ve döndür
        return companyRepository.save(existingCompany);
    }
    
    //şirket silme soft delete ile
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id).orElseThrow((() -> new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND)));
        
        company.setDeleted(true); //COMPANY SİLİNMİŞ HALE GELDİ
        companyRepository.save(company);
    }
    
    //şirket başvurularını görüntüleme
    public List<Company> getPendingCompanies() {
        return companyRepository.findAllByIsDeletedFalseAndStatus(Status.PENDING);
    }
    
    @Transactional
    public Company approveCompany(Long id, String token) {
        // Şirketi ID ile bul
        Company company = companyRepository.findById(id)
                                           .orElseThrow(() -> new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND));
        
        // Soft delete olan şirketler onaylanamaz!
        if (company.isDeleted()) {
            throw new HRMPlatformException(ErrorType.COMPANY_ALREADY_DELETED);
        }
        
        // Eğer token sağlanmışsa, token ile e-posta doğrulaması yap
        if (token != null && !token.isEmpty()) {
            company = companyRepository.findByEmailVerificationToken(token)
                                       .orElseThrow(() -> new HRMPlatformException(ErrorType.TOKEN_NOT_FOUND));
            
            company.setEmailVerified(true);  // E-posta doğrulandı
            company.setEmailVerificationToken(null);  // Tokeni sıfırla
            company.setTokenExpirationTime(null);  // Token süresi geçersiz
        }
        
        // Mail doğrulaması yapılmamış şirketler onaylanamaz!
        if (!company.isEmailVerified()) {
            throw new HRMPlatformException(ErrorType.EMAIL_NOT_VERIFIED);
        }
        
        // Şirketin durumunu onaylı olarak güncelle
        company.setStatus(Status.APPROVED);
        
        // Onay mailini başvuran şirkete gönder
        emailService.sendEmail(
                company.getEmail(), "Şirket Başvurunuz Onaylandı",
                "Tebrikler, " + company.getName() + " şirketinizin başvurusu onaylandı! " +
                        "Platformumuza giriş yaparak yönetim işlemlerini gerçekleştirebilirsiniz.");
        
        return companyRepository.save(company);
    }
    
    
    @Transactional
    public Company rejectCompany(Long id) {
        Company company = companyRepository.findById(id)
                                           .orElseThrow(() -> new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND));
        // Soft delete olan şirketler reddedilemez!
        if (company.isDeleted()) {
            throw new HRMPlatformException(ErrorType.COMPANY_ALREADY_DELETED);
        }
        //  Soft Delete işlemi
        company.setDeleted(true);
        company.setStatus(Status.REJECTED);
        
        
        // Şirket sahibine red maili gönder
        
        emailService.sendEmail(
                company.getEmail(),
                "Şirket Başvurunuz Reddedildi",
                "Üzgünüz, " + company.getName() + " şirketinizin başvurusu reddedildi." +
                        "Detaylı bilgi için destek ekibimizle iletişime geçebilirsiniz."
        );
        return companyRepository.save(company);
    }
    
    @Transactional
    public Company setSubscriptionPlan(Long id, SubscriptionPlan plan) {
        Company company = companyRepository.findById(id)
                                           .orElseThrow(() -> new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND));
        
        // Eğer üyelik planı zaten aynı ise güncellemeye gerek yok
        if (company.getSubscriptionPlan() == plan) {
            throw new HRMPlatformException(ErrorType.ALREADY_SUBSCRIBED);
        }
        
        company.setSubscriptionPlan(plan);
        return companyRepository.save(company);
    }
    
    // Üyelik süresi dolan şirketleri otomatik olarak devre dışı bırak
    @Scheduled(cron = "0 0 0 * * ?") // Her gece çalışır
    @Transactional
    public void checkExpiredMembership() {
        List<Company> expiredCompanies = companyRepository.findBySubscriptionEndDateBeforeAndIsDeletedFalse(LocalDateTime.now());
        
        for (Company company : expiredCompanies) {
            company.setDeleted(false); // Şirketi devre dışı bırak
            companyRepository.save(company);
        }
    }
    
    
    public String findCompanyNameById(Long companyId) {
        return companyRepository.findCompanyNameById(companyId);
    }
    
    //  Mail Gönderme Metodu
//    private void sendMail(String to, String subject, String text) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(text);
//        mailSender.send(message);
    
    public SubscriptionResponse getSubscriptionPlan(Long id) {
        Company company = companyRepository.findById(id)
                                           .orElseThrow(() -> new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND));
        
        return new SubscriptionResponse(company.getSubscriptionPlan(), company.getSubscriptionEndDate());
    }
    
    @Transactional
    public void expireSubscription(Long id) {
        Company company = companyRepository.findById(id)
                                           .orElseThrow(() -> new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND));
        
        // Üyelik süresi kontrolü
        if (company.getSubscriptionEndDate() == null || company.getSubscriptionEndDate().isAfter(LocalDateTime.now())) {
            throw new HRMPlatformException(ErrorType.SUBSCRIPTION_NOT_EXPIRED);
        }
        
        // Şirketin erişimini kısıtla
        company.setActive(false);
        companyRepository.save(company);
        
    }
    
    @Transactional
    public void verifyEmail(String token) {
        Company company = companyRepository.findByEmailVerificationToken(token)
                                           .orElseThrow(() -> new HRMPlatformException(ErrorType.TOKEN_NOT_FOUND));
        
        // Token geçerliliğini kontrol et
        if (company.getTokenExpirationTime().isBefore(LocalDateTime.now())) {
            throw new HRMPlatformException(ErrorType.TOKEN_EXPIRED);
        }
        
        // E-posta doğrulama işlemi
        company.setEmailVerified(true);
        company.setEmailVerificationToken(null); // Tokeni sıfırlıyoruz
        company.setTokenExpirationTime(null);    // Token süresini sıfırlıyoruz
        companyRepository.save(company);
        
      
    }
    
    
    
    
    //     METIN
    
    
    public int getTotalCompanyCount() {
        return (int) companyRepository.count();
    }
    
    @Transactional
    public List<Company> getExpiringSoonCompanies() {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime soonExpireDate = currentDate.plusDays(7);  // 7 gün içerisinde üyelik süresi dolacak şirketler
        
        return companyRepository.findCompaniesBySubscriptionDateRange(currentDate, soonExpireDate);
    }
    
    

    
    
    // companyId'ye göre şirketi bulma
    public Optional<Company> findById(Long companyId) {
        // Verilen companyId'ye göre şirketi bulur
        return companyRepository.findById(companyId);
    }
    
    // Company'i bulma ve hata fırlatma
    public Company getCompanyById(Long companyId) {
        // findById metodunu çağırarak şirketi arar
        return findById(companyId)
                .orElseThrow(() -> new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND));
    }


    
    
    //-----------
    // SITE_ADMIN ve başvuran şirkete e-posta bildirimlerini gönderen yardımcı metod
//    public void notifyAdminAndApplicant(Company company) {
//        try {
//            // Site Admin'in e-posta adresini al
//            if (siteAdminEmail == null || siteAdminEmail.isEmpty()) {
//                log.warn("Site Admin e-posta adresi tanımlı değil. Başvuru bildirimi gönderilemedi.");
//            } else {
//                String verificationLink = appConfig.getBaseUrl() + "/api/company/verify-email?token=" + company.getEmailVerificationToken();
//                emailService.sendEmail(siteAdminEmail,
//                                       "Yeni Şirket Başvurusu",
//                                       "Yeni bir şirket başvurusu var. Onaylamak için linki tıklayın: " + verificationLink);
//                log.info("Site Admin'e başvuru bildirimi başarıyla gönderildi.");
//            }
//
//            // Başvuran kişinin e-posta adresini al
//            String applicantEmail = company.getEmail();
//            if (applicantEmail == null || applicantEmail.isEmpty()) {
//                log.warn("Şirketin e-posta adresi tanımlı değil. Başvuru onay e-postası gönderilemedi.");
//            } else {
//                emailService.sendEmail(applicantEmail,
//                                       "Başvurunuz alındı",
//                                       "Başvurunuz alındı. Onaylandıktan sonra hesabınızı aktif edebilmeniz için linke tıklayın.");
//                log.info("Başvuran şirkete bilgilendirme e-postası başarıyla gönderildi.");
//            }
//        } catch (Exception e) {
//            log.error("Admin ve başvuran kişiye e-posta gönderirken hata oluştu: " + e.getMessage(), e);
//        }
//    }



//    // 1️⃣ SITE_ADMIN → Tüm şirketleri getirebilir
//    public Optional<Company> findByCompanyId(Long companyId) {
//        return companyRepository.findById(companyId);
//    }
//
//    // 2️⃣ COMPANY_ADMIN → Kullanıcının yönettiği şirketi getir
//    public Optional<Company> findCompanyByUserEmail(String email) {
//        Optional<User> user = userRepository.findByEmail(email);
//
//        if (user.isPresent() && user.get().getCompany() != null) {
//            return Optional.of(user.get().getCompany());
//        }
//        return Optional.empty();
//    }
//
//    // 3️⃣ EMPLOYEE → Kullanıcının çalıştığı şirketi getir
//    public Optional<Company> findCompanyByEmployeeEmail(String email) {
//        Optional<User> user = userRepository.findByEmail(email);
//
//        if (user.isPresent() && user.get().getCompany() != null) {
//            return Optional.of(user.get().getCompany());
//        }
//        return Optional.empty();
//    }




    public List<Company> findByCompanyName(String name) {
        return companyRepository.findByNameIgnoreCase(name);
    }


    public List<CompanyDto> getApprovedCompanies() {
        List<Company> approvedCompanies = companyRepository.findByStatusAndIsDeletedFalse(Status.APPROVED);
        return approvedCompanies.stream()
                .map(company -> new CompanyDto(
                        company.getName(),
                        company.getAddress(),
                        company.getPhone(),
                        company.getEmail(),
                        company.getSubscriptionPlan(),
                        company.getContactPerson(),
                        company.getSector(),
                        company.getEmployeeCount()
                ))
                .collect(Collectors.toList());

    }
}