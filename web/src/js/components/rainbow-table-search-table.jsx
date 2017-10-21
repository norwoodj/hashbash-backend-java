import React from "react";
import PropTypes from "prop-types";
import DynamicListEntityTable from "./dynamic-list-entity-table";


export default class RainbowTablesearchTable extends DynamicListEntityTable {

    doRetrieveEntities() {
        return this.props.rainbowTableService.getRainbowTableSearches(
            this.props.rainbowTableId,
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
            {Header: "Status", accessor: "status"},
            {Header: "Hash", accessor: "hash"},
            {Header: "Password", accessor: "password"},
        ];
    }
}

RainbowTableListTable.propTypes = {
    rainbowTableService: PropTypes.object.isRequired,
    rainbowTableId: PropTypes.object.isRequired,
    refreshRateSeconds: PropTypes.number
};
