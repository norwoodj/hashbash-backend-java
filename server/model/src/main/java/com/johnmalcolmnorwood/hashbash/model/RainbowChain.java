package com.johnmalcolmnorwood.hashbash.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rainbow_chain")
public class RainbowChain implements Comparable<RainbowChain> {

    @Id
    private String endHash;

    private String startPlaintext;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rainbowTableId")
    private RainbowTable rainbowTable;

    @Override
    public int compareTo(RainbowChain other) {
        if (other == null) {
            return -1;
        }

        return endHash.compareTo(other.endHash);
    }
}
