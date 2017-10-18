import React from "react";
import PropTypes from "prop-types";
import EntityList from "./entity-list";
import ProgressBar from "./progress-bar";


export default class RainbowTableList extends EntityList {
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

    getProgressBar(rainbowTable) {
        return <ProgressBar
            full={rainbowTable.status === "COMPLETED"}
            numerator={rainbowTable.chainsGenerated}
            denominator={rainbowTable.numChains}
        />;
    }

    getEntityTableColumns() {
        return [
            {Header: "Name", Cell: row => <a href={`/search-rainbow-table.html?id=${row.original.id}`}>{row.original.name}</a>},
            {Header: "Status", accessor: "status"},
            {Header: "Progress", Cell: row => this.getProgressBar(row.original)},
            {Header: "Num Chains", accessor: "numChains"},
            {Header: "Chain Length", accessor: "chainLength"},
            {Header: "Hash Function", accessor: "hashFunction"},
            {Header: "Password Length", accessor: "passwordLength"},
            {Header: "Character Set", accessor: "characterSet"},
        ];
    }
}

RainbowTableList.propTypes = {
    rainbowTableService: PropTypes.object.isRequired,
    refreshRateSeconds: PropTypes.number
};
