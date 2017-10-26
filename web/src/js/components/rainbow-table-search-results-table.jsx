import React from "react";
import PropTypes from "prop-types";
import SinglePageStaticListEntityTable from "./single-page-static-list-entity-table";
import ProgressBar from "./progress-bar";


export default class RainbowTableSearchResultsTable extends SinglePageStaticListEntityTable {

    static getSearchResultsProgressBar(searchResults) {
        return (
            <ProgressBar numerator={searchResults.foundSearches} denominator={searchResults.totalSearches}/>
        );
    }

    getEntityTableColumns() {
        return [
            {Header: "Total Searches", accessor: "totalSearches"},
            {Header: "Found Password", accessor: "foundSearches"},
            {Header: "Success %", Cell: row => RainbowTableSearchResultsTable.getSearchResultsProgressBar(row.original)}
        ];
    }
}

RainbowTableSearchResultsTable.propTypes = {
    entities: PropTypes.array.isRequired
};
