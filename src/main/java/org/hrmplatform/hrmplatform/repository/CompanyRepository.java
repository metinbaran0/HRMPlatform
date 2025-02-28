package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {


    Optional<Company> findById(Long id); // id'ye göre arama yapıyoruz.


    // Silinmemiş ve onay bekleyen şirketleri getir (isDeleted=false ve status=PENDING)
    List<Company> findAllByIsDeletedFalseAndStatus(Status status);


    List<Company> findBySubscriptionEndDateBeforeAndIsDeletedFalse(LocalDateTime date);



    @Query("SELECT c.name FROM Company c WHERE c.id = :companyId")
    String findCompanyNameById(@Param("companyId") Long companyId);

   // Optional<Company> findByEmailVerificationToken(String token);

}
