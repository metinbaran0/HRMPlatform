package org.hrmplatform.hrmplatform.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter

@AllArgsConstructor
public enum ErrorType implements ErrorTypeInterface {
    //
    VALIDATION_ERROR(400, "Validasyon hatası.. Girdiğiniz parametreler geçersizdir...", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR(500, "Sunucuda beklenmeyen hata. Lütfen daha sonra tekrar deneyin.", HttpStatus.INTERNAL_SERVER_ERROR),

    //USER ERROR TYPE
    USERID_NOTFOUND(1001, "Kullanici id bulunamadı.", HttpStatus.BAD_REQUEST),
    USER_NOTFOUND(1002, "Kullanici email bulunamadı.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(3000, "Girilen şifreler uyuşmamaktadır.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_EXISTS(1002, "Kullanıcı zaten mevcut.", HttpStatus.BAD_REQUEST),
    ACTIVATION_CODE_EXPIRED(456, "Etkinleştirme kodunun süresi doldu", HttpStatus.BAD_REQUEST),
    USER_ALREADY_ACTIVATED(123, "Kullanıcı zaten aktif", HttpStatus.BAD_REQUEST),
    USER_ROLE_NOT_FOUND(1004, "Kullanıcıya ait rol bulunamadı!", HttpStatus.BAD_REQUEST),

    //kullanılmıyor invalide_role
    INVALID_ROLE(1003, "Geçersiz rol seçildi.", HttpStatus.BAD_REQUEST),

    //COMPANY ERROR TYPE
    COMPANY_NOT_FOUND(5001, "Şirket bulunamadı.", HttpStatus.NOT_FOUND),
    COMPANY_ALREADY_DELETED(5002, "Şirket silinmiş.", HttpStatus.GONE),
    ALREADY_SUBSCRIBED(5003, "Şirket üyeliği zaten güncel.", HttpStatus.CONFLICT),
    SUBSCRIPTION_NOT_EXPIRED(5004, "Şirket aboneliği sona ermedi.", HttpStatus.BAD_REQUEST),

    //MAİL ERROR TYPE
    EMAIL_NOT_VERIFIED(402, "Mail onaylanmamış", HttpStatus.BAD_REQUEST),
    EMAIL_SENDING_FAILED(789, "Mail gönderirken hata oluştu", HttpStatus.BAD_REQUEST),

    //kullanılmıyor
    UNAUTHORIZED_OPERATION(403, "Bu işlemi yapmak için yetkiniz yok.", HttpStatus.FORBIDDEN),


    //Employee Error Type
    EMPLOYEE_NOT_FOUND(404, "Çalışan bulunamadı", HttpStatus.NOT_FOUND),




    TOKEN_EXPIRED(789, "Token süresi doldu.", HttpStatus.BAD_REQUEST),
    //kullanılmıyor
    TOKEN_NOT_FOUND(456, "Token bulunamadı", HttpStatus.BAD_REQUEST),


    //SHIFT-BREAK ERROR TYPE

    BREAK_NOT_FOUND_BY_SHIFT(3001, "Vardiyaya ait mola bulunamadı.", HttpStatus.NOT_FOUND),
    BREAK_NOT_FOUND_BY_COMPANY(3002, "Şirkete ait mola bulunamadı.", HttpStatus.NOT_FOUND),
    DATA_NOT_FOUND(1000, "Veri bulunamadı.", HttpStatus.NOT_FOUND),
    EMPLOYEE_NOT_FOUND_OR_NOT_IN_COMPANY(4001, "Şirkette çalışan bulunamadı.", HttpStatus.NOT_FOUND),
    SHIFT_ALREADY_ASSIGNED(2001, "Çalışan bu vardiyaya zaten atanmış.", HttpStatus.CONFLICT),
    SHIFT_NOT_FOUND(2002, "Vardiya bulunamadı.", HttpStatus.NOT_FOUND),
    SHIFT_NOT_FOUND_IN_COMPANY(2003, "Bu şirkete ait bir vardiya bulunamadı.", HttpStatus.NOT_FOUND),
    EMPLOYEE_ON_LEAVE(2004, "Çalışan izinli vardiya verilemez",HttpStatus.CONFLICT );


    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}