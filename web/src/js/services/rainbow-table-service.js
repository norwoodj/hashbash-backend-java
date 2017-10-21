
export default class RainbowTableService {
    constructor(http, errorHandler) {
        this.http = http;
        this.errorHandler = errorHandler;
    }

    static getListQueryString(pageNumber, limit, sortKey) {
        return [
            `?pageNumber=${pageNumber / limit}&pageSize=${limit}`,
            sortKey !== null ? `&sortKey=${sortKey.id}&sortOrder=${sortKey.desc ? "DESC" : "ASC"}` : ""
        ].join("");
    }

    getRainbowTables(pageNumber, limit, sortKey) {
        return new Promise(resolve => {
            this.http.ajax({
                type: "GET",
                url: `/api/rainbow-table${RainbowTableService.getListQueryString(pageNumber, limit, sortKey)}`,
                success: rainbowTables => resolve(rainbowTables),
                error: this.errorHandler
            });
        });
    }

    getRainbowTableById(rainbowTableId) {
        return new Promise(resolve => {
            this.http.ajax({
                type: "GET",
                url: `/api/rainbow-table/${rainbowTableId}`,
                success: rainbowTable => resolve(rainbowTable),
                error: this.errorHandler
            });
        });
    }

    getRainbowTableSearches(rainbowTableId, pageNumber, limit, sortKey) {
        return new Promise(resolve => {
            this.http.ajax({
                type: "GET",
                url: `/api/rainbow-table/${rainbowTableId}/search${RainbowTableService.getListQueryString(pageNumber, limit, sortKey)}`,
                success: rainbowTables => resolve(rainbowTables),
                error: this.errorHandler
            });
        });
    }

    getRainbowTableCount() {
        return new Promise(resolve => {
            this.http.ajax({
                type: "GET",
                url: "/api/rainbow-table/count",
                success: res => resolve(res.rainbowTableCount),
                error: this.errorHandler
            });
        });
    }

    getSupportedHashFunctions() {
        return new Promise(resolve => {
            this.http.ajax({
                type: "GET",
                url: "/api/rainbow-table/hash-functions",
                success: hashFunctions => resolve(hashFunctions),
                error: this.errorHandler
            });
        });
    }
}
