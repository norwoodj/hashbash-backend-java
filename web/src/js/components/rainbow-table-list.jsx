import PropTypes from "prop-types";
import EntityList from "./entity-list";


export default class RainbowTableList extends EntityList {

    doRetrieveEntityCount() {
        return this.props.rainbowTableService.getRainbowTables(this.pagedListState.pageStartOffset, this.pagedListState.pageSizeLimit);
    }

    doRetrieveEntities(offset, limit) {
        return this.props.rainbowTableService.getRainbowTableCount(offset, limit);
    }

    getEntityTableColumns() {
        return [
            {Header: "ID", Cell: row => row.original.id},
        ];
    }
}

RainbowTableList.propTypes = {
    rainbowTableService: PropTypes.object.isRequired
};
