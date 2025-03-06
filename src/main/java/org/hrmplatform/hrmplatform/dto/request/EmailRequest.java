package org.hrmplatform.hrmplatform.dto.request;

public record EmailRequest(String to, String subject, String text) {
}