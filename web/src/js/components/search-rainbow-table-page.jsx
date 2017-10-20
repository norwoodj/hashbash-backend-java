import React from "react";
import PropTypes from "prop-types";
import Panel from "muicss/lib/react/panel";

import RainbowTableDetailTable from "./rainbow-table-detail-table";
import RainbowTableEfficiencyTable from "./rainbow-table-efficiency-table";
import ErrorElement from "./error-element";
import DefaultRainbowTablePage from "./default-rainbow-table-page";


export default class SearchRainbowTablePage extends DefaultRainbowTablePage {
    constructor() {
        super();
        Object.assign(this.state, {rainbowTable: null});
    }

    retrieveData() {
        this.state.rainbowTableService.getRainbowTableById(this.props.rainbowTableId).then(rainbowTable => {
            this.setState({rainbowTable: rainbowTable});
            if (rainbowTable.status !== "COMPLETED") {
                setTimeout(this.retrieveData.bind(this), 5000);
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
            </Panel>
        );
    }
}

SearchRainbowTablePage.propTypes = {
    rainbowTableId: PropTypes.string.isRequired,
    httpService: PropTypes.func.isRequired,
    error: PropTypes.string
};
