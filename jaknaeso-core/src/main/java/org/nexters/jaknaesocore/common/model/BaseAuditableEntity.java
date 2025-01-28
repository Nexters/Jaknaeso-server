package org.nexters.jaknaesocore.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@Getter
@MappedSuperclass
public class BaseAuditableEntity extends BaseTimeEntity {

  @CreatedBy
  @Column(updatable = false)
  private Long createdBy;

  @LastModifiedBy private Long updatedBy;
}
