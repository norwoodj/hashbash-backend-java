import React from "react";
import PropTypes from "prop-types";
import DynamicListEntityTable from "./dynamic-list-entity-table";
import Checkbox from "muicss/lib/react/checkbox";


export default class RainbowTableSearchTable extends DynamicListEntityTable {
    constructor() {
        super();
        Object.assign(this.state, {includeNotFound: false});
    }

    doRetrieveEntities() {
        return this.props.rainbowTableService.getRainbowTableSearches(
            this.props.rainbowTableId,
            this.state.pagedListState.pageNumber,
            this.state.pagedListState.pageSize,
            this.state.pagedListState.sortKey,
            this.state.includeNotFound
        );
    }

    doRetrieveEntityCount() {
        return this.props.rainbowTableService.getRainbowTableSearchCount(this.props.rainbowTableId, this.state.includeNotFound);
    }

    getEntityTableColumns() {
        return [
            {Header: "Status", accessor: "status"},
            {Header: "Hash", accessor: "hash"},
            {Header: "Password", accessor: "password"},
            {Header: "Search Time", Cell: row => row.original.searchTime ? `${row.original.searchTime}s` : "N/A", sortable: false},
        ];
    }

    getRowPropsForEntity(entity) {
        if (entity.status === "NOT_FOUND") {
            return {className: "search-not-found"};
        } else {
            return {className: `search-${entity.status.toLowerCase()}`};
        }
    }


    toggleIncludeNotFound(evt) {
        this.state.includeNotFound = evt.target.checked;
        this.retrieveEntities();
        this.retrieveEntityCount();
    }

    render() {
        return (
            <div>
                <Checkbox
                    label="Include Not Found searches"
                    checked={this.state.includeNotFound}
                    onChange={this.toggleIncludeNotFound.bind(this)}
                />
                {super.render()}
            </div>
        );
    }
}

RainbowTableSearchTable.propTypes = {
    rainbowTableService: PropTypes.object.isRequired,
    rainbowTableId: PropTypes.string.isRequired,
    refreshRateSeconds: PropTypes.number
};
