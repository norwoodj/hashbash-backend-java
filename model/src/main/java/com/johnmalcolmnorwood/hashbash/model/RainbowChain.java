package com.johnmalcolmnorwood.hashbash.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * The Site object, representing a website on the internet, which has a set of login pages that can be injected
 * with our alternate authentication method
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RainbowChain implements Comparable<RainbowChain> {

    @Id
    @Column(name = "endhash")
    private String endHash;

    @Column(name = "startplaintext")
    private String startPlaintext;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rainbowtableid")
    private RainbowTable rainbowTable;


    @Override
    public int compareTo(RainbowChain other) {
        if (other == null) {
            return 1;
        }

        return endHash.compareTo(other.endHash);
    }
}
