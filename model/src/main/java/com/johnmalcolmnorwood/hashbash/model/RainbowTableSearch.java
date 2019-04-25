package com.johnmalcolmnorwood.hashbash.model;

import com.fasterxml.jackson.annotation.JsonGetter;
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
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;


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

    @Column
    private Date searchStarted;

    @Column
    private Date searchCompleted;

    @Column
    private Date lastUpdated;

    @JsonGetter
    public Double getSearchTime() {
        if (searchCompleted == null) {
            return null;
        }

        return (searchCompleted.getTime() - searchStarted.getTime()) / 1000.;
    }

    @PrePersist
    public void setCreated() {
        created = Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant());
    }
}
