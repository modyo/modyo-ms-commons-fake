# USS Microservices Commons Audit

This lib can be used to audit custom events of your Microservice.
These Audit Events will be saved in a DB via JPA.

In the following sections will be explained how to setup and use this Audit Components:


## Set Up
- Add the dependency to your build.gradle:
```groovy
implementation 'com.modyo.ms.commons:audit:2.x.x-RELEASE'
```

- Add the DB config in your application.yml:
```yaml
spring:
  datasource:
    audit:
      url: <db_url>
      username: <db_username>
      password: <db_password>
      ddl-auto: <db_ddl_type>
```

## Fields that will be logged:

- `auditable_id`: Id of the entity you want to log
- `auditable_type`: Type/Name of the entity you want to log (e.g. Reservation)
- `auditable_parent_id`: If there is a parent entity you can save the id there. E.g. to be able to show all audits of the childs Reservation and Address under the Parent User. If not it will be used the `auditable_id`
- `auditable_parent_type`: See `auditable_parent_id`
- `change_type`: Type of change (e.g. update_db, http_request, ...)
- `event`: Custom event name
- `log_type`: `INFO`, `SUCCESS` or `ERROR`
- `initial_value`: Content of the entity to be changed, http request info, etc...
- `new_value`: Content of the updated entity, http response info, exception info, etc...
- `created_at`: Timestamp
- `created_by`: `email` claim of the AccessToken
- `user_agent`: Value of the Request-Header `User-Agent`

## Custom configuration
```
commons.audit.user-id-token-claim: <default:email, claim-key of the access token which will be used for the field `created_by`
```

## How to use:

### Via the annotation `@ModyoAudit`:

In a Service Method of your choice you can add the annotation `@ModyoAudit` to automatically create an Audit Log (`log_type=SUCCESS` in case of success, `log_type=ERROR` in case of error)
There are two paremeters required: `changeType and event` which will be used to save the respective fields

To be able to save the custom ids, types and values of the entities which will only be known during runtime,
you have to use the class `AuditContext` with their two methods:
- `setInitialInfo(Object parentEntity, String parentEntityId, Object entity, String entityId)`
- `setNewValue(Object value)`
These value will be saved in Spring's RequestContext so that the Aspect marked with `@ModyoAudit`
can automatically get this info before creating an Audit Log

Here is an example how to use `@ModyoAudit` with `AuditContext`:

```java
@ModyoAudit(changeType = 'update_db', event = "Save Child")
  public void saveChild() {
    Parent parentEntity = load your parent entity
    Child childEntity = parentEntity.getChild();

    AuditContext.setInitialInfo(parentEntity, parentEntity.getId(), childEntity, childEntity.getId());

    childEntity.updateData(......);

    AuditContext.setNewValue(childEntity);
  ...
}
```

### Http Request

All Http Request which will be made via the `RestTemplate` of modyo-ms-commons will be logged automatically

Idea: Create config to be able to enable/disable this feature
