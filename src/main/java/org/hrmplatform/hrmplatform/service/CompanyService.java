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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
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
    private final PasswordService passwordService;
    private final PasswordEncoder passwordEncoder;
    
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
                         JwtManager jwtManager,
                          PasswordService passwordService,
                          PasswordEncoder passwordEncoder) {
		this.companyRepository = companyRepository;
		this.companyMapper = companyMapper;
		this.emailService = emailService;
		this.userRoleService = userRoleService;
		this.employeeService = employeeService;
		this.userService = userService;
        this.emailNotificationService= emailNotificationService;
        this.jwtManager = jwtManager;
        this.passwordService=passwordService;
        this.passwordEncoder=passwordEncoder;
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
    @PreAuthorize("hasAuthority('SITE_ADMIN')")
    public Company approveCompany(Long id) {
        log.info("Şirket onaylama işlemi başladı. ID: {}", id);
        
        // Şirketi ID ile bul
        Company company = companyRepository.findById(id)
                                           .orElseThrow(() -> new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND));
        
        // Şirket zaten onaylı mı?
        if (company.getStatus() == Status.APPROVED) {
            log.warn("Şirket zaten onaylanmış: {}", company.getId());
            throw new HRMPlatformException(ErrorType.COMPANY_ALREADY_APPROVED);
        }
        
        // Şirketin e-posta doğrulaması yapılmış mı?
        if (!company.isEmailVerified()) {
            log.error("Şirketin e-postası doğrulanmamış: {}", company.getEmail());
            throw new HRMPlatformException(ErrorType.EMAIL_NOT_VERIFIED);
        }
        
        // Şirketi onaylı hale getir
        company.setStatus(Status.APPROVED);
        companyRepository.save(company);
        
        // Kullanıcı için rastgele bir şifre oluştur
        String generatedPassword = passwordService.generateRandomPassword();
        String hashedPassword = passwordEncoder.encode(generatedPassword);
        
        // Kullanıcı oluştur ve kaydet
        User user = User.builder()
                        .name(company.getContactPerson())
                        .email(company.getEmail())
                        .password(hashedPassword)
                        .status(true)
                        .companyId(company.getId())
                        .employeeId(null)
                        .activated(true)
                        .activationCode(null)
                        .activationCodeExpireAt(null)
                        .resetToken(null)
                        .resetTokenExpireAt(null)
                        .build();

// Kullanıcıyı veritabanına kaydet
        userService.save(user);
        // Kullanıcıya rol atama işlemi
        UserRole userRole = UserRole.builder()
                                    .userId(user.getId())  // User nesnesi yerine ID kullanılmalı
                                    .role(Role.COMPANY_ADMIN)   // Çalışana EMPLOYEE rolü atanıyor
                                    .build();
        
        userRoleService.save(userRole);
        
        // Kullanıcıya e-posta ile şifreyi gönder
        emailService.sendEmail(company.getEmail(), "Şirket Başvurunuz Onaylandı",
                               "Tebrikler, " + company.getName() + " şirketinizin başvurusu onaylandı!\n" +
                                       "Platformumuza giriş yapmak için aşağıdaki bilgileri kullanabilirsiniz:\n\n" +
                                       "E-posta: " + company.getEmail() + "\n" +
                                       "Şifre: " + generatedPassword + "\n\n" +
                                       "Lütfen giriş yaptıktan sonra şifrenizi değiştirin.");
        
        log.info("Şirket onaylandı ve şifre e-postası gönderildi. ID: {}", company.getId());
        return company;
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
                        company.getEmployeeCount(),
                        company.getId()
                ))
                .collect(Collectors.toList());

    }
    //Aktif üyelikler
    public Long getActiveSubscriptionsCount() {
        return companyRepository.countBySubscriptionEndDateAfterAndIsDeletedFalse(java.time.LocalDateTime.now());

    }

    //istatistik
    public Map<String, Long> getMonthlyCompanyStats(int year) {
        List<Object[]> results = companyRepository.countByYearGroupedByMonth(year);

        // Ay isimlerini içeren bir LinkedHashMap oluşturuyoruz. LinkedHashMap sıralama sırasını korur.
        Map<String, Long> monthlyStats = new LinkedHashMap<>();
        String[] monthNames = {
                "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
                "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
        };

        // Ayların tamamını sıfırla başlatıyoruz.
        for (String month : monthNames) {
            monthlyStats.put(month, 0L);  // Başlangıçta her ay için 0 değerini atıyoruz.
        }

        // Veritabanından gelen her ay için veriyi eşleştiriyoruz.
        for (Object[] result : results) {
            int monthIndex = (int) result[0] - 1; // SQL'de Ocak = 1, Java dizisinde 0
            Long count = (Long) result[1];
            monthlyStats.put(monthNames[monthIndex], count);  // Ayın adına göre değer güncelleniyor.
        }

        return monthlyStats;
    }



}