package com.ht.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author HT
 * @version V1.0
 * @package com.ht.entity
 * @date 2018-07-07 11:23
 */
@Data
@Entity
@Table(name = "support_address")
public class SupportAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "belong_to")
    private String belongTo;

    @Column(name = "en_name")
    private String enName;

    @Column(name = "cn_name")
    private String cnName;

    private String level;
}
