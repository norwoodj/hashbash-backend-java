import React from "react";
import PropTypes from "prop-types";
import ReactTable from "react-table";


export default class SinglePageStaticListEntityTable extends React.Component {
    render() {
        return (
            <ReactTable
                manual
                sortable={false}
                showPagination={false}
                columns={this.getEntityTableColumns()}
                loading={false}
                defaultPageSize={this.props.entities.length}
                data={this.props.entities}
            />
        );
    }
}

SinglePageStaticListEntityTable.propTypes = {
    entities: PropTypes.array.isRequired
};
