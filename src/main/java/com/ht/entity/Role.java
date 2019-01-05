package com.ht.entity;

import lombok.Data;
import lombok.Generated;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author HT
 * @version V1.0
 * @package com.ht.entity
 * @date 2018-06-27 21:50
 */
@Entity
@Table(name = "role")
@Data
public class Role {
    @Id
    @Generated
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String name;
}
