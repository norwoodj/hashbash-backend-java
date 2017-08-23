package com.johnmalcolmnorwood.hashbash.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BATCH_STEP_EXECUTION")
@Entity
public class BatchStepExecution {

    @Id
    @Column(name = "STEP_EXECUTION_ID")
    @Getter(onMethod = @__(@JsonIgnore))
    private int stepExecutionId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "WRITE_COUNT")
    private int writeCount;
}
