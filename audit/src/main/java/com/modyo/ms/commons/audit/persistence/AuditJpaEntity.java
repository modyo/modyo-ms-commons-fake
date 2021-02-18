package com.modyo.ms.commons.audit.persistence;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.modyo.ms.commons.audit.AuditLogType;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "Audit", indexes = {
    @Index(name = "index_by_parent", columnList = "auditable_parent_id, auditable_parent_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "auditable_id")
  private String auditableId;

  @Column(name = "auditable_type")
  private String auditableType;

  @Column(name = "auditable_parent_id")
  private String auditableParentId;

  @Column(name = "auditable_parent_type")
  private String auditableParentType;

  @Column(name = "change_type")
  private String changeType;

  @Column(name = "event")
  private String event;

  @Column(name = "log_type")
  @Enumerated(EnumType.STRING)
  private AuditLogType logType;

  @Column(name = "initial_value", columnDefinition = "LONGTEXT")
  private String initialValue;

  @Column(name = "new_value", columnDefinition = "LONGTEXT")
  private String newValue;

  @CreationTimestamp
  @JsonSerialize(using = ZonedDateTimeSerializer.class)
  @Column(name = "created_at")
  private ZonedDateTime createdAt;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "user_agent")
  private String userAgent;

}
