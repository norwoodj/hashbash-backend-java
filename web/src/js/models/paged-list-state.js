
export default class PagedListState {
    constructor() {
        this.loading = true;
        this.pageStartOffset = 0;
        this.pageSizeLimit = 5;
        this.pageSizeOptions = [5, 10, 15, 25, 50];
        this.pages = 0;
        this.objects = [];
        this.objectCount = 0;
    }

    updateForObjects(objects) {
        this.objects = objects;
        this.loading = false;
    }

    updateForObjectCount(objectCount) {
        this.objectCount = objectCount;
        this.pages = Math.ceil(objectCount / this.pageSizeLimit);
    }

    handlePageChange(page) {
        this.pageStartOffset = page * this.pageSizeLimit;
        this.loading = true;
    }

    handlePageSizeChange(pageSize, page) {
        this.pageStartOffset = page * pageSize;
        this.pageSizeLimit = pageSize;
        this.pages = Math.ceil(this.objectCount / pageSize);
        this.loading = true;
    }
}
