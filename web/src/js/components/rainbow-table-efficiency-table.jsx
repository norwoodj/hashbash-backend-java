import React from "react";
import PropTypes from "prop-types";
import SinglePageStaticListEntityTable from "./single-page-static-list-entity-table";
import ProgressBar from "./progress-bar";


export default class RainbowTableEfficiencyTable extends SinglePageStaticListEntityTable {
    static getProgressBar(rainbowTable) {
        return (
            <ProgressBar
                numerator={rainbowTable.finalChainCount}
                denominator={rainbowTable.numChains}
                roundUp={false}
            />
        );
    }

    getEntityTableColumns() {
        return [
            {Header: "Num Chains", accessor: "numChains"},
            {Header: "ChainsGenerated", accessor: "finalChainCount"},
            {Header: "Efficiency", Cell: row => RainbowTableEfficiencyTable.getProgressBar(row.original), sortable: false},
        ];
    }
}

RainbowTableEfficiencyTable.propTypes = {
    entities: PropTypes.array.isRequired
};
