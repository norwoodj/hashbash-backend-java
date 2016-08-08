package com.johnmalcolmnorwood.hashbash.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
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

    @JsonGetter
    public int getChainsGenerated() {
        return batchStepExecution.getWriteCount();
    }

    @JsonGetter
    public String getStatus() {
        return batchStepExecution.getStatus();
    }
}
