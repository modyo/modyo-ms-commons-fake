package com.modyo.ms.commons.audit.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.modyo.ms.commons.audit.AuditLogType;
import com.modyo.ms.commons.audit.service.ChangeType;
import org.junit.jupiter.api.Test;

class AuditJpaEntityFactoryTest {

  @Test
  void create_withChildAndParentInfo() {
    AuditJpaEntity auditJpaEntity = AuditJpaEntityFactory.create(
        AuditLogType.INFO,
        "childId",
        "parentId",
        new ParentClass("parent"),
        new ChildClass("before"),
        new ChildClass("after"),
        ChangeType.CHANGE_STATUS,
        "Changed status"
    );

    assertThat(auditJpaEntity.getAuditableId(), is("childId"));
    assertThat(auditJpaEntity.getAuditableType(), is("ChildClass"));
    assertThat(auditJpaEntity.getAuditableParentId(), is("parentId"));
    assertThat(auditJpaEntity.getAuditableParentType(), is("ParentClass"));
    assertThat(auditJpaEntity.getChangeType(), is(ChangeType.CHANGE_STATUS));
    assertThat(auditJpaEntity.getEvent(), is("Changed status"));
    assertThat(auditJpaEntity.getLogType(), is(AuditLogType.INFO));
    assertThat(auditJpaEntity.getInitialValue(), is("{\"id\":\"before\"}"));
    assertThat(auditJpaEntity.getNewValue(), is("{\"id\":\"after\"}"));
  }

  @Test
  void create_withOnlyParentInfo() {
    String entityId = "entityId";
    AuditJpaEntity auditJpaEntity = AuditJpaEntityFactory.create(
        AuditLogType.ERROR,
        entityId,
        null,
        null,
        new ParentClass("before"),
        new ParentClass("after"),
        ChangeType.CHANGE_STATUS,
        "Changed status"
    );

    assertThat(auditJpaEntity.getAuditableId(), is(entityId));
    assertThat(auditJpaEntity.getAuditableType(), is("ParentClass"));
    assertThat(auditJpaEntity.getAuditableParentId(), is(entityId));
    assertThat(auditJpaEntity.getAuditableParentType(), is("ParentClass"));
    assertThat(auditJpaEntity.getChangeType(), is(ChangeType.CHANGE_STATUS));
    assertThat(auditJpaEntity.getEvent(), is("Changed status"));
    assertThat(auditJpaEntity.getLogType(), is(AuditLogType.ERROR));
    assertThat(auditJpaEntity.getInitialValue(), is("{\"id\":\"before\"}"));
    assertThat(auditJpaEntity.getNewValue(), is("{\"id\":\"after\"}"));
  }

  static class ParentClass {
    private final String id;

    public ParentClass(String id) {
      this.id = id;
    }
  }

  static class ChildClass {
    private final String id;

    public ChildClass(String id) {
      this.id = id;
    }
  }

  static class OtherClass {
    private final String id;

    public OtherClass(String id) {
      this.id = id;
    }
  }

}
