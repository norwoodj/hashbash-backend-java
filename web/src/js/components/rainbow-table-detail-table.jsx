import React from "react";
import PropTypes from "prop-types";
import SinglePageStaticListEntityTable from "./single-page-static-list-entity-table";
import ProgressBar from "./progress-bar";


export default class RainbowTableDetailTable extends SinglePageStaticListEntityTable {

    static getProgressBar(rainbowTable) {
        return (
            <ProgressBar
                full={rainbowTable.status === "COMPLETED"}
                numerator={rainbowTable.chainsGenerated}
                denominator={rainbowTable.numChains}
            />
        );
    }

    getEntityTableColumns() {
        return [
            {Header: "Name", accessor: "name"},
            {Header: "Status", accessor: "status", sortable: false},
            {Header: "Progress", Cell: row => RainbowTableDetailTable.getProgressBar(row.original), sortable: false},
            {Header: "Chains Generated", accessor: "chainsGenerated", sortable: false},
            {Header: "Num Chains", accessor: "numChains"},
            {Header: "Chain Length", accessor: "chainLength"},
            {Header: "Hash Function", accessor: "hashFunction"},
            {Header: "Password Length", accessor: "passwordLength"},
            {Header: "Character Set", accessor: "characterSet", sortable: false},
        ];
    }
}

RainbowTableDetailTable.propTypes = {
    entities: PropTypes.array.isRequired
};
