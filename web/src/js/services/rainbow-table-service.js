
export default class RainbowTableService {
    constructor(http, errorHandler) {
        this.http = http;
        this.errorHandler = errorHandler;
    }

    static getRainbowTableListQueryString(offset, limit) {
        return `?offset=${offset}&limit=${limit}`;
    }

    getRainbowTables(offset, limit) {
        return new Promise(resolve => {
            this.http.ajax({
                type: "GET",
                url: `/api/rainbow-table${RainbowTableService.getRainbowTableListQueryString(offset, limit)}`,
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
