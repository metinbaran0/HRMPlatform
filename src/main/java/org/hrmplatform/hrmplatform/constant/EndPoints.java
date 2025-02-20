package org.hrmplatform.hrmplatform.constant;

public class EndPoints {
	
	public static final String VERSION = "/v1";
	public static final String API = "/api";
	public static final String AUTH = API + "/auth"; // Base path olarak auth eklendi
	public static final String DEVELOPER = "/dev";
	public static final String TEST = "/test";
	public static final String PROD = "/prod";
	
	public static final String ROOT = VERSION + DEVELOPER;
	
	public static final String USER = ROOT + "/user";
	public static final String USERROLE = ROOT + "/userrole";
	
	// USER
	public static final String REGISTER = "/register";
	public static final String DOLOGIN = "/dologin";
	public static final String ACTIVATE = "/activate";
	public static final String RESENDACTIVATIONEMAIL = "/resend-activation-email";
	public static final String FORGOTPASSWORD = "/forgot-password";
	public static final String RESETPASSWORD = "/reset-password";
	
	// USERROLE
	public static final String ASSIGNROLES = "/assign-roles";
	public static final String FINDALL = "/findall";
	public static final String FINDBYUSERID = "/findbyuserid";
	public static final String SEARCHBYNAME = "/searchByName";
	public static final String SEARCHBYROLE = "/searchByRole";
	public static final String DELETEUSERROLE = "/userrole/delete/{userId}";
	
	//USERMANAGEMENT
	public static final String PROFILE = "/{userId}";
	public static final String DELETE = "/{userId}";
	public static final String SEARCH = "/search";
	public static final String STATUS = "/{userId}/status";
	public static final String UPDATE = "/update";
	public static final String FINDALL_USERS = "/findAll-users";
	
	
	/**
	 * Bu Yapıyla Şu URL Yapısına Ulaşacaksınız:
	 *
	 * POST /api/auth/register → Kullanıcı kaydını oluşturur ve aktivasyon e-postası gönderir.
	 * POST /api/auth/dologin → Kullanıcı girişini yapar ve JWT token döner.
	 * GET /api/auth/activate?code=<activation_code> → Kullanıcıyı aktivasyon koduyla aktifleştirir.
	 * POST /api/auth/resend-activation-email → Kullanıcıya yeni bir aktivasyon e-postası gönderir.
	 * POST /api/auth/forgot-password → Parola sıfırlama linki gönderir.
	 * POST /api/auth/reset-password → Parola sıfırlama işlemi gerçekleştirir.
	 */
	
}