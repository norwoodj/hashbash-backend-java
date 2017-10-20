package com.johnmalcolmnorwood.hashbash.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Date;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rainbow_table_search")
public class RainbowTableSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "rainbowtableid")
    private short rainbowTableId;

    @Column
    private String hash;

    @Column
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    protected RainbowTableSearchStatus status;

    @Column
    private Date created;

    @PrePersist
    public void setCreateTimestamp() {
        created = new Date();
    }
}
