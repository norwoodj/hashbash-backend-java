import React from "react";
import PropTypes from "prop-types";
import EntityList from "./entity-list";
import ProgressBar from "./progress-bar";


export default class RainbowTableList extends EntityList {
    doRetrieveEntities() {
        return this.props.rainbowTableService.getRainbowTables(this.pagedListState.pageStartOffset, this.pagedListState.pageSizeLimit);
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
            {Header: "ID", Cell: row => row.original.id},
            {Header: "Name", Cell: row => row.original.name},
            {Header: "Status", Cell: row => row.original.status},
            {Header: "Progress", Cell: row => this.getProgressBar(row.original)},
            {Header: "Chains Generated", Cell: row => row.original.chainsGenerated},
            {Header: "Num Chains", Cell: row => row.original.numChains},
            {Header: "Chain Length", Cell: row => row.original.chainLength},
            {Header: "Password Length", Cell: row => row.original.passwordLength},
            {Header: "Character Set", Cell: row => row.original.characterSet},
            {Header: "Hash Function", Cell: row => row.original.hashFunction}
        ];
    }
}

RainbowTableList.propTypes = {
    rainbowTableService: PropTypes.object.isRequired,
    refreshRateSeconds: PropTypes.number
};
