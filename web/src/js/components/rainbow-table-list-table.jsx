import React from "react";
import PropTypes from "prop-types";
import DynamicListEntityTable from "./dynamic-list-entity-table";
import ProgressBar from "./progress-bar";


export default class RainbowTableListTable extends DynamicListEntityTable {
    static getProgressBar(rainbowTable) {
        return (
            <ProgressBar
                full={rainbowTable.status === "COMPLETED"}
                numerator={rainbowTable.chainsGenerated}
                denominator={rainbowTable.numChains}
            />
        );
    }

    static getRainbowTableLink(rainbowTable) {
        return <a href={`/search-rainbow-table?rainbowTableId=${rainbowTable.id}`}>{rainbowTable.name}</a>;
    }

    doRetrieveEntities() {
        return this.props.rainbowTableService.getRainbowTables(
            this.state.pagedListState.pageNumber,
            this.state.pagedListState.pageSize,
            this.state.pagedListState.sortKey
        );
    }

    doRetrieveEntityCount() {
        return this.props.rainbowTableService.getRainbowTableCount();
    }

    getEntityTableColumns() {
        return [
            {Header: "Name", accessor: "name", Cell: row => RainbowTableListTable.getRainbowTableLink(row.original)},
            {Header: "Status", accessor: "status", sortable: false},
            {Header: "Progress", Cell: row => RainbowTableListTable.getProgressBar(row.original), sortable: false},
            {Header: "Num Chains", accessor: "numChains"},
            {Header: "Chain Length", accessor: "chainLength"},
            {Header: "Hash Function", accessor: "hashFunction"},
            {Header: "Password Length", accessor: "passwordLength"},
            {Header: "Character Set", accessor: "characterSet", sortable: false},
        ];
    }
}

RainbowTableListTable.propTypes = {
    rainbowTableService: PropTypes.object.isRequired,
    refreshRateSeconds: PropTypes.number
};
