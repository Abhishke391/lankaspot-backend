package com.lankaspot.server.service;

import com.lankaspot.server.model.AuditLog;
import com.lankaspot.server.model.User;
import com.lankaspot.server.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Joins the caller's transaction, so the audit row commits or rolls back with the
     * business change. Pass actor = null for system-initiated events.
     * oldData / newData may be a Map, a record, or null; they are stored as JSON.
     */
    @Transactional
    public void record(User actor, String action, String entityType, Long entityId,
                       Object oldData, Object newData) {
        String ip = null;
        String userAgent = null;
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            HttpServletRequest req = attrs.getRequest();
            ip = truncate(req.getRemoteAddr(), 45);
            userAgent = req.getHeader("User-Agent");
        }
        AuditLog auditLog = new AuditLog();
        auditLog.setActor(actor);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setOldData(toJson(oldData));
        auditLog.setNewData(toJson(newData));
        auditLog.setIpAddress(ip);
        auditLog.setUserAgent(userAgent);
        auditLog.setCreatedAt(java.time.LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> historyFor(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }

    private String toJson(Object value) {
        if (value == null) return null;
        if (value instanceof String s) return s;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException e) {
            // An audit serialisation problem must never roll back a payment
            log.warn("Could not serialise audit payload", e);
            return String.valueOf(value);
        }
    }

    private static String truncate(String s, int max) {
        return s == null ? null : (s.length() > max ? s.substring(0, max) : s);
    }
}