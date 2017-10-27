import React from "react";
import PropTypes from "prop-types";
import Panel from "muicss/lib/react/panel";

import RainbowTableDetailTable from "./rainbow-table-detail-table";
import RainbowTableEfficiencyTable from "./rainbow-table-efficiency-table";
import RainbowTableSearchResultsTable from "./rainbow-table-search-results-table";
import RainbowTableSearchForm from "./rainbow-table-search-form";
import RainbowTableSearchTable from "./rainbow-table-search-table";
import ErrorElement from "./error-element";
import DefaultRainbowTablePage from "./default-rainbow-table-page";


export default class SearchRainbowTablePage extends DefaultRainbowTablePage {
    constructor() {
        super();
        Object.assign(this.state, {rainbowTable: null, searchResults: {totalSearches: 0, foundSearches: 0}});
    }

    retrieveData() {
        this.state.rainbowTableService.getRainbowTableById(this.props.rainbowTableId).then(rainbowTable => {
            this.setState({rainbowTable: rainbowTable});

            if (rainbowTable.status === "STARTED" || rainbowTable.status === "QUEUED") {
                setTimeout(this.retrieveData.bind(this), 5000);
            } else {
                this.state.rainbowTableService.getRainbowTableSearchResultsById(this.props.rainbowTableId).then(searchResults => {
                    this.setState({searchResults: searchResults});
                });
            }
        });

    }

    renderWithRainbowTableService() {
        if (this.state.rainbowTable === null) {
            return null;
        }

        return (
            <Panel>
                <ErrorElement error={this.state.error}/>
                <h2>Rainbow Table '{this.state.rainbowTable.name}'</h2>
                <div className="mui-divider"/>

                <div className="content-block">
                    <h4>Table Details</h4>
                    <RainbowTableDetailTable entities={[this.state.rainbowTable]}/>
                </div>

                <div className="content-block">
                    <h4>Table Efficiency Stats</h4>
                    <RainbowTableEfficiencyTable entities={[this.state.rainbowTable]}/>
                </div>

                <div className="content-block">
                    <h4>Search Result Stats</h4>
                    <RainbowTableSearchResultsTable entities={[this.state.searchResults]}/>
                </div>

                <div className="content-block">
                    <h4>Past Rainbow Table Searches</h4>
                    <RainbowTableSearchTable
                        rainbowTableService={this.state.rainbowTableService}
                        rainbowTableId={this.props.rainbowTableId}
                        refreshRateSeconds={5}
                    />
                </div>
                <div className="content-block">
                    <h4>New Rainbow Table Search</h4>
                    <RainbowTableSearchForm
                        rainbowTable={this.state.rainbowTable}
                        rainbowTableService={this.state.rainbowTableService}
                    />
                </div>
            </Panel>
        );
    }
}

SearchRainbowTablePage.propTypes = {
    rainbowTableId: PropTypes.string.isRequired,
    httpService: PropTypes.func.isRequired,
    error: PropTypes.string
};
