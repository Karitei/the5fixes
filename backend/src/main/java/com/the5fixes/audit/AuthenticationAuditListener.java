package com.the5fixes.audit;

import com.the5fixes.audit.Service.AuditService;
import com.the5fixes.audit.Type.AuditEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Wires Spring Security's own authentication events into the audit trail,
 * so no service has to remember to call the audit code manually.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationAuditListener {

    private final AuditService auditService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        auditService.record(AuditEventType.LOGIN_SUCCESS, event.getAuthentication().getName(),
                "User", null, "Authentication succeeded", null);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        auditService.record(AuditEventType.LOGIN_FAILURE, String.valueOf(event.getAuthentication().getPrincipal()),
                "User", null, "Authentication failed: " + event.getException().getClass().getSimpleName(), null);
    }
}
