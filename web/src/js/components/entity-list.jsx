import React from "react";
import ReactTable from "react-table";
import PagedListState from "../models/paged-list-state";


export default class EntityList extends React.Component {
    constructor() {
        super();

        this.pagedListState = new PagedListState();
        this.state = {pagedListState: this.pagedListState};
    }

    componentDidMount() {
        this.setState({pagedListState: this.pagedListState});
        this.retrieveEntities(this.state.pagedListState.pageStartOffset, this.state.pagedListState.pageSizeLimit);
        this.retrieveEntityCount();
    }

    retrieveEntityCount() {
        this.doRetrieveEntityCount().then((entityCount) => {
            this.pagedListState.updateForObjectCount(entityCount);
            this.setState(this.pagedListState);
        });
    }

    retrieveEntities() {
        this.doRetrieveEntities(this.state.pagedListState.pageStartOffset, this.state.pagedListState.pageSizeLimit).then(entities => {
            this.pagedListState.updateForObjects(entities);
            this.setState(this.pagedListState);
        });
    }

    handlePageChange(page) {
        this.pagedListState.handlePageChange(page);
        this.setState(this.pagedListState, () => this.retrieveEntities());
    }

    handlePageSizeChange(pageSize, page) {
        this.pagedListState.handlePageSizeChange(pageSize, page);
        this.setState(this.pagedListState, () => this.retrieveEntities());
    }

    render() {
        return (
            <ReactTable
                manual
                columns={this.getEntityTableColumns()}
                sortable={false}
                loading={this.state.pagedListState.loading}
                defaultPageSize={this.state.pagedListState.pageSizeLimit}
                pageSizeOptions={this.state.pagedListState.pageSizeOptions}
                pages={this.state.pagedListState.pages}
                data={this.state.pagedListState.objects}
                onPageChange={this.handlePageChange.bind(this)}
                onPageSizeChange={this.handlePageSizeChange.bind(this)}
            />
        );
    }
}
