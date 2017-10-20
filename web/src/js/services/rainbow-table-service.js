
export default class RainbowTableService {
    constructor(http, errorHandler) {
        this.http = http;
        this.errorHandler = errorHandler;
    }

    static getRainbowTableListQueryString(offset, limit, sortKey) {
        return [
            `?pageNumber=${offset / limit}&pageSize=${limit}`,
            sortKey !== null ? `&sortKey=${sortKey.id}&sortOrder=${sortKey.desc ? "DESC" : "ASC"}` : ""
        ].join("");
    }

    getRainbowTables(offset, limit, sortKey) {
        return new Promise(resolve => {
            this.http.ajax({
                type: "GET",
                url: `/api/rainbow-table${RainbowTableService.getRainbowTableListQueryString(offset, limit, sortKey)}`,
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
