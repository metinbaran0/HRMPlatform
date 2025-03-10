package org.hrmplatform.hrmplatform.constant;

public class EndPoints {



    public static final String VERSION = "/v1";

    public static final String API = "/api";
    public static final String DEVELOPER = "/dev";
    public static final String TEST = "/test";
    public static final String PROD = "/prod";

    public static final String ROOT = VERSION + API;


    public static final String USER = ROOT + "/user";
    public static final String USERROLE = ROOT + "/userrole";
    public static final String LEAVE = ROOT + "/leave";
    public static final String COMMENT = ROOT + "/comment";
    public static final String COMPANY = ROOT + "/company";
    public static final String SHIFT = ROOT + "/shift";
    public static final String BREAK = ROOT + "/break";
    public static final String EMPLOYEE_SHIFT = ROOT + "/employee-shift";
    public static final String EXPENSE = ROOT + "/expense";
    
    
    public static final String AUTH = ROOT + "/auth"; // Base path olarak auth eklendi
    public static final String EMAIL = ROOT + "/email";
    public static final String ASSET = ROOT + "/asset";
    
    // USER
    public static final String REGISTER = "/register";
    public static final String DOLOGIN = "/dologin";
    public static final String ACTIVATE = "/activate";
    public static final String RESENDACTIVATIONEMAIL = "/resend-activation-email";
    public static final String FORGOTPASSWORD = "/forgot-password";
    public static final String RESETPASSWORD = "/reset-password";

    //USERMANAGEMENT
    public static final String PROFILE = "/{userId}";
    public static final String DELETE = "/{userId}";
    public static final String SEARCH = "/search";
    public static final String STATUS = "/{userId}/status";
    public static final String UPDATE = "/update";
    public static final String FINDALL_USERS = "/findAll-users";


    //USERROLE
    public static final String ASSIGNROLES = "/assign-roles";
    public static final String FINDALL = "/findall";
    public static final String FINDBYUSERID = "/findbyuserid";
    public static final String SEARCHBYNAME = "/searchByName";
    public static final String SEARCHBYROLE = "/searchByRole";
    public static final String DELETEUSERROLE = "/userrole/delete/{userId}";

    //COMPANY
    public static final String FINDALLCOMPANY = "/find-all-company";
    public static final String FINDBYCOMPANYID = "/find-byid-company";
    public static final String ADDCOMPANY = "/add-company";
    public static final String UPDATECOMPANY = "/update-company";
    public static final String DELETECOMPANY = "/delete-company";
    public static final String PENDING = "/pending-company";
    public static final String APPROVE = "/approve-company";
    public static final String APPROVED = "/approved";
    public static final String REJECT = "/reject-company";
    public static final String SUBSCRIPTION = "/subscription";
    public static final String FINDBYCOMPANYNAME = "/find-byname-company";

    //LEAVEREQUEST

    public static final String LEAVEREQUEST = "/leaverequest"; // Kullanıcı yeni izin talebi oluşturur
    public static final String LEAVEBYUSERID = "/leavebyuserid/{employeeId}"; //kullanıcın kendi izinlerini getirme
    public static final String PENDINGLEAVESFORMANAGER = "/manager/{managerId}/pending-leaves"; // Yönetici, bekleyen izin taleplerini görüntüler
    public static final String ACCEPTLEAVE = "/manager/{managerId}/approve/{employeeId}"; // Yönetici, izin talebini onaylar
    public static final String REJECTLEAVE = "/manager/{managerId}/reject/{employeeId}"; // Yönetici, izin talebini reddeder
    // EMPLOYEE
    public static final String EMPLOYEE = ROOT + "/employee";
    public static final String GET_ALL_EMPLOYEES = "/get-all-employees";
    public static final String GET_EMPLOYEE_BY_ID = "/get-employee-by-id/{id}";
    public static final String CREATE_EMPLOYEE = "/create-employee";
    public static final String UPDATE_EMPLOYEE = "update-employee/{id}";
    public static final String DELETE_EMPLOYEE = "delete-employee/{id}";
    public static final String CHANGE_EMPLOYEE_STATUS = "change-/{id}/status";
  


    //SHIFT
    public static final String CREATE_SHIFT= "/create-shift";
    public static final String GETALL_SHIFT = "/getall-shift";
    public static final String GETSHIFTBY_COMPANYID ="getshift-by-company-id/{companyId}" ;
    public static final String GETSHIF_BYID = "/getshift-by-id/{id}";
    public static final String ACTIVE_SHIFT ="active-shift" ;
    public static final String DELETE_SHIFT = "/delete-shift";
    public static final String UPDATE_SHIFT = "/update-shift/{id}";
    public static final String SHIFTTYPE = "/shifttype/{shiftType}";
    public static final String DATE_SHIFT = "/date-shift";
    public static final String DISTRIBUTION = "/distribution";


    //BREAK
    public static final String CREATE_BREAK= "/create-break";
    public static final String ALL_BREAK = "/get-all-break";
    public static final String GET_BREAK_BYID ="/getbreak-by-id/{breakId}" ;
    public static final String GET_BREAK_BYSHIFT ="/getbreak-by-shift/{shiftId}" ;
    public static final String GET_BREAK_BYCOMPANY ="/getbreak-by-company/{companyId}" ;
    public static final String UPDATE_BREAK ="/update-break/{breakId}" ;
    public static final String DELETE_BREAK ="/delete-break/{breakId}" ;

    //EMPLOYEE_SHIFT
    public static final String CREATE_EMPLOYEE_SHIFT= "/create-employee-shift";
    public static final String ALL_EMPLOYEE_SHIFTS= "/all-employee-shift";
    public static final String GET_EMPLOYEE_SHIFTS_BY_EMPLOYEE_ID= "/get-employee-shift-byemployee-id/{employeeId}";
    public static final String UPDATE_EMPLOYEE_SHIFT= "/update-employee-shift/{employeeShiftId}";
    public static final String DELETE_EMPLOYEE_SHIFT= "/delete-employee-shift/{employeeShiftId}";
    public static final String FILTERDATE = "//employee-shifts/by-date";

    //COMMENT
    public static final String CREATE_COMMENT= "/create-comment";
    public static final String GETALL_COMMENT= "/getall-comment";

    
    //EMAIL
    public static final String SEND_EMAIL = "/send-email";
    
    //ASSET
    public static final String ADD_ASSET = "/add";
    public static final String GET_ALL_ASSETS = "/all";
    public static final String GET_ASSET_BY_ID = "/{id}";
    public static final String UPDATE_ASSET = "/update/{id}";
    public static final String DELETE_ASSET = "/delete/{id}";
    
    //EXPENCE
    public static final String CREATE_EXPENSE = "/create-expense";
    public static final String GETALL_EXPENSE = "/getall-expense";
    
    
}