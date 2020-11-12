INSERT INTO Audit
            (id, auditable_parent_id, auditable_parent_type,
            auditable_id, auditable_type,
            change_type, event,
            log_type, created_by, user_agent
            )
VALUES      ('1', '10', 'parent1',
            '10', 'parent1',
            'CHANGE_STATUS', 'Created',
            'SUCCESS', 'user1', 'Firefox'
            );

INSERT INTO Audit
            (id, auditable_parent_id, auditable_parent_type,
            auditable_id, auditable_type,
            change_type, event,
            log_type, created_by, user_agent
            )
VALUES      ('2', '10', 'parent1',
            '100', 'child1',
            'CHANGE_STATUS', 'Changed',
            'INFO', 'user1', 'Firefox'
            );

INSERT INTO Audit
            (id, auditable_parent_id, auditable_parent_type,
            auditable_id, auditable_type,
            change_type, event,
            log_type, created_by, user_agent
            )
VALUES      ('3', '10', 'parent1',
            '100', 'child1',
            'CHANGE_STATUS', 'Aborted',
            'ERROR', 'user1', 'Firefox'
            );

INSERT INTO Audit
            (id, auditable_parent_id, auditable_parent_type,
            auditable_id, auditable_type,
            change_type, event,
            log_type, created_by, user_agent
            )
VALUES      ('4', '10', 'parent2',
            '101', 'child2',
            'CHANGE_STATUS', 'Created',
            'SUCCESS', 'user1', 'Firefox'
            );

INSERT INTO Audit
            (id, auditable_parent_id, auditable_parent_type,
            auditable_id, auditable_type,
            change_type, event,
            log_type, created_by, user_agent
            )
VALUES      ('5', '11', 'parent1',
            '101', 'child2',
            'CHANGE_STATUS', 'Created',
            'SUCCESS', 'user1', 'Firefox'
            );
