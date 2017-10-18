import React from "react";
import PropTypes from "prop-types";
import ReactTable from "react-table";
import PagedListState from "../models/paged-list-state";


export default class EntityList extends React.Component {
    constructor() {
        super();
        this.pagedListState = new PagedListState();
        this.state = {pagedListState: this.pagedListState};
    }

    componentDidMount() {
        this.setState(
            {pagedListState: this.pagedListState},
            this.start.bind(this)
        );
    }

    start() {
        this.pollEntityInfo();
    }

    pollEntityInfo() {
        this.retrieveEntities();
        this.retrieveEntityCount();

        if (this.props.refreshRateSeconds) {
            setTimeout(this.pollEntityInfo.bind(this), this.props.refreshRateSeconds * 1000);
        }
    }

    retrieveEntityCount() {
        this.doRetrieveEntityCount().then((entityCount) => {
            this.pagedListState.updateForObjectCount(entityCount);
            this.setState(this.pagedListState);
        });
    }

    retrieveEntities() {
        this.doRetrieveEntities().then(entities => {
            this.pagedListState.updateForObjects(entities);
            this.setState(this.pagedListState);
        });
    }

    fetchData(state) {
        let sortKey = state.sorted.length ? state.sorted[0] : null;
        this.pagedListState.handleFetchData(state.page, state.pageSize, sortKey);
        this.setState(
            {pagedListState: this.pagedListState},
            () => {
                this.retrieveEntities();
                this.retrieveEntityCount();
            }
        );
    }

    render() {
        return (
            <ReactTable
                manual
                columns={this.getEntityTableColumns()}
                loading={this.state.pagedListState.loading}
                defaultPageSize={this.state.pagedListState.pageSize}
                pageSizeOptions={this.state.pagedListState.pageSizeOptions}
                pages={this.state.pagedListState.pages}
                data={this.state.pagedListState.objects}
                onFetchData={this.fetchData.bind(this)}
            />
        );
    }
}

EntityList.propTypes = {
    refreshRateSeconds: PropTypes.number
};
