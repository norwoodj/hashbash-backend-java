
export default class PagedListState {
    constructor() {
        this.loading = true;
        this.pageNumber = 0;
        this.pageSize = 10;
        this.pageSizeOptions = [5, 10, 15, 25, 50];
        this.sortKey = null;
        this.pages = 0;
        this.objects = [];
    }

    updateForObjects(objects) {
        this.objects = objects;
        this.loading = false;
    }

    updateForObjectCount(objectCount) {
        this.pages = Math.ceil(objectCount / this.pageSize);
    }

    handleFetchData(pageNumber, pageSize, sortKey) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortKey = sortKey;
        this.loading = true;
    }
}
