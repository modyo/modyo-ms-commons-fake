package com.modyo.ms.commons.audit.persistence;

import com.modyo.ms.commons.audit.service.ChangeType;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.boot.logging.LogLevel;

@Entity
@Table(name = "Audit")
@Data
@NoArgsConstructor
class AuditJpaEntity {

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
  @Enumerated(EnumType.STRING)
  private ChangeType changeType;

  @Column(name = "event")
  private String event;

  @Column(name = "log_level")
  @Enumerated(EnumType.STRING)
  private LogLevel logLevel;

  @Column(name = "initial_value", columnDefinition = "LONGTEXT")
  private String initialValue;

  @Column(name = "new_value", columnDefinition = "LONGTEXT")
  private String newValue;

  @CreationTimestamp
  @Column(name = "created_at")
  private ZonedDateTime createdAt;

  @Column(name = "created_by")
  private String createdBy;

}
