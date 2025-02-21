package org.hrmplatform.hrmplatform.constant;

public class EndPoints {
	
	public static final String VERSION = "/v1";
	
	public static final String API = "/api";
	public static final String DEVELOPER = "/dev";
	public static final String TEST = "/test";
	public static final String PROD = "/prod";
	
	public static final String ROOT = VERSION + DEVELOPER;
	
	public static final String USER = ROOT + "/user";
	public static final String USERROLE =ROOT+ "/userrole";
	
	//USER
	
	public static final String REGISTER = "/register";
	public static final String DOLOGIN = "/dologin";
	
	//USERROLE
	public static final String ASSIGNROLES = "/assign-roles";
	public static final String FINDALL = "/findall";
	public static final String FINDBYUSERID = "/findbyuserid";
	public static final String SEARCHBYNAME = "/searchByName";
	public static final String SEARCHBYROLE = "/searchByRole";
	public static final String DELETEUSERROLE = "/userrole/delete/{userId}";

	public static final String COMPANY = "/company";
	public static final String FINDALLCOMPANY =COMPANY+ "/find-all-company";
	public static final String FINDBYCOMPANYID =COMPANY+ "/find-byid-company";
	public static final String ADDCOMPANY =COMPANY+ "/add-company";
	public static final String UPDATECOMPANY =COMPANY+ "/update-company";
	public static final String DELETECOMPANY =COMPANY+ "/delete-company";
	public static final String PENDING =COMPANY+ "/pending-company";
	public static final String APPROVE =COMPANY+ "/approve-company";
	public static final String REJECT =COMPANY+ "/reject-company";

}


//package com.barisd.java16_x.constant;
//
//public class Endpoints {
//
//	public static final String VERSION="/v1";
//
//	public static final String API="/api";
//	public static final String DEVELOPER="/dev";
//	public static final String TEST="/test";
//	public static final String PROD="/prod";
//
//	public static final String ROOT=VERSION+DEVELOPER;
//
//	public static final String USER= ROOT+"/user";
//	public static final String POST= ROOT+"/post";
//	public static final String USERYETKI= ROOT+"/useryetki";
//
//	//USER
//	public static final String SAVE="/save";
//	public static final String FINDALL="/findall";
//	public static final String FINDBYUSERID="/findbyuserid";
//	public static final String REGISTER="/register";
//	public static final String DOLOGIN="/dologin";
//	public static final String GETPROFILE="/get-profile";
//	//POST
//	public static final String CREATEPOST="/create-post";
//	public static final String GETUSERSALLPOST="/get-users-all-post";
//	public static final String GETALLPOST="/get-all-post";
//
//
//}