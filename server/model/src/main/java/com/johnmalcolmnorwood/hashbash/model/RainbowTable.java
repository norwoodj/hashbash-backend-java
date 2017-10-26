package com.johnmalcolmnorwood.hashbash.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rainbow_table")
public class RainbowTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    private String name;

    @Column(name = "numchains")
    private int numChains;

    @Column(name = "chainlength")
    private int chainLength;

    @Column(name = "passwordlength")
    private int passwordLength;

    @Column(name = "finalchaincount")
    private long finalChainCount;

    @Column(name = "characterset")
    private String characterSet;


    @Column(name = "batchexecutionid")
    @Getter(onMethod = @__(@JsonIgnore))
    private Long batchExecutionId;

    @Column(name = "hashfunction")
    @Enumerated(EnumType.STRING)
    private HashFunctionName hashFunction;

    @OneToOne
    @JoinColumn(name = "batchexecutionid", insertable = false, updatable = false)
    @Getter(onMethod = @__(@JsonIgnore))
    private BatchStepExecution batchStepExecution;

    @Column(name = "created")
    private Date created;

    @PrePersist
    public void setCreated() {
        created = Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant());
    }

    @JsonGetter
    public int getChainsGenerated() {
        if (batchStepExecution == null) {
            return 0;
        }

        return batchStepExecution.getWriteCount();
    }

    @JsonGetter
    public String getStatus() {
        if (batchStepExecution == null) {
            return "QUEUED";
        }

        return batchStepExecution.getStatus();
    }
}
