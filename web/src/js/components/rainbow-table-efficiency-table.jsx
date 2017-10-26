import React from "react";
import PropTypes from "prop-types";
import SinglePageStaticListEntityTable from "./single-page-static-list-entity-table";
import ProgressBar from "./progress-bar";


export default class RainbowTableEfficiencyTable extends SinglePageStaticListEntityTable {
    static getNumPossiblePasswords(rainbowTable) {
        return Math.pow(rainbowTable.characterSet.length, rainbowTable.passwordLength);
    }

    static getMaxContainedPasswords(rainbowTable) {
        return rainbowTable.finalChainCount * rainbowTable.chainLength;
    }

    static getEfficiencyProgressBar(rainbowTable) {
        return (
            <ProgressBar
                numerator={rainbowTable.finalChainCount}
                denominator={rainbowTable.numChains}
            />
        );
    }

    static getKeySpaceProgressBar(rainbowTable) {
        let numPossiblePassword = RainbowTableEfficiencyTable.getNumPossiblePasswords(rainbowTable);
        let maxContainedPassword = RainbowTableEfficiencyTable.getMaxContainedPasswords(rainbowTable);

        return (
            <ProgressBar
                numerator={maxContainedPassword}
                denominator={numPossiblePassword}
            />
        );
    }

    getEntityTableColumns() {
        return [
            {Header: "Num Chains", accessor: "numChains"},
            {Header: "Final Chains Generated", accessor: "finalChainCount"},
            {Header: "Generate Efficiency", Cell: row => RainbowTableEfficiencyTable.getEfficiencyProgressBar(row.original), sortable: false},
            {Header: "Possible Passwords", Cell: row => RainbowTableEfficiencyTable.getNumPossiblePasswords(row.original), sortable: false},
            {Header: "Passwords in Table", Cell: row => RainbowTableEfficiencyTable.getMaxContainedPasswords(row.original), sortable: false},
            {Header: "Key Space Coverage", Cell: row => RainbowTableEfficiencyTable.getKeySpaceProgressBar(row.original), sortable: false}
        ];
    }
}

RainbowTableEfficiencyTable.propTypes = {
    entities: PropTypes.array.isRequired
};
