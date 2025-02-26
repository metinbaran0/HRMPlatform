package org.hrmplatform.hrmplatform.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.CompanyDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.enums.Status;
import org.hrmplatform.hrmplatform.enums.SubscriptionPlan;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.mapper.CompanyMapper;
import org.hrmplatform.hrmplatform.repository.CompanyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

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
        companyRepository.save(company);
    }

//    public void updateCompany(Long id, @Valid CompanyDto companyDto) {
//        Company company = companyRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Şirket bulunamadı"));
//        // Mevcut company nesnesini güncelle
//        companyMapper.updateCompanyFromDto(companyDto, company);
//
//        companyRepository.save(company);
//    }


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
    public Company approveCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow((() -> new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND)));
        // Soft delete olan şirketler onaylanamaz!
        if (company.isDeleted()) {
            throw new HRMPlatformException(ErrorType.COMPANY_ALREADY_DELETED);
        }
        // Mail doğrulaması yapılmamış şirketler onaylanamaz!
        if (!company.isEmailVerified()) {
            throw new HRMPlatformException(ErrorType.EMAIL_NOT_VERIFIED);
        }
        company.setStatus(Status.APPROVED);


        //email ile bildirim alınabilir onaylandı olarak..
//        sendMail(company.getEmail(), "Şirket Başvurunuz Onaylandı",
//                "Tebrikler, şirket başvurunuz onaylandı! Platformumuza giriş yapabilirsiniz.");


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
        companyRepository.save(company);

        //  Reddetme maili gönder
//        sendMail(company.getEmail(), "Şirket Başvurunuz Reddedildi",
//                "Üzgünüz, başvurunuz reddedildi. Sebep: " + reason);

        company.setStatus(Status.REJECTED);
        return companyRepository.save(company);
    }
    @Transactional
    public Company setSubscriptionPlan(Long id, SubscriptionPlan plan) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND));
        company.setSubscriptionPlan(plan);
        return companyRepository.save(company);
    }

    // Üyelik süresi dolan şirketleri otomatik olarak devre dışı bırak
    @Scheduled(cron = "0 0 0 * * ?") // Her gece çalışır
    @Transactional
    public void checkExpiredMembership(){
        List<Company> expiredCompanies = companyRepository.findBySubscriptionEndDateBeforeAndIsDeletedFalse(LocalDateTime.now());

        for (Company company : expiredCompanies) {
            company.setDeleted(false); // Şirketi devre dışı bırak
            companyRepository.save(company);
        }
    }

    //  Mail Gönderme Metodu
//    private void sendMail(String to, String subject, String text) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(text);
//        mailSender.send(message);
}