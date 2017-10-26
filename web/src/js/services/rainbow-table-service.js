
export default class RainbowTableService {
    constructor(http, errorHandler) {
        this.http = http;
        this.errorHandler = errorHandler;
    }

    static getListQueryString(pageNumber, limit, sortKey) {
        return [
            `?pageNumber=${pageNumber}&pageSize=${limit}`,
            sortKey !== null ? `&sortKey=${sortKey.id}&sortOrder=${sortKey.desc ? "DESC" : "ASC"}` : ""
        ].join("");
    }

    static getSearchListQueryString(pageNumber, limit, sortKey, includeNotFound) {
        return [
            RainbowTableService.getListQueryString(pageNumber, limit, sortKey),
            `&includeNotFound=${includeNotFound}`
        ].join("");
    }

    submitSearchRequest(rainbowTableId, hash) {
        return new Promise(resolve => {
            this.http.ajax({
                type: "POST",
                url: `/api/rainbow-table/${rainbowTableId}/search?hash=${hash}`,
                success: searchResponse => resolve(searchResponse),
                error: this.errorHandler
            });
        });
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

    getRainbowTableSearches(rainbowTableId, pageNumber, limit, sortKey, includeNotFound) {
        let queryString = RainbowTableService.getSearchListQueryString(pageNumber, limit, sortKey, includeNotFound);

        return new Promise(resolve => {
            this.http.ajax({
                type: "GET",
                url: `/api/rainbow-table/${rainbowTableId}/search${queryString}`,
                success: rainbowTables => resolve(rainbowTables),
                error: this.errorHandler
            });
        });
    }

    getRainbowTableSearchCount(rainbowTableId, includeNotFound) {
        return new Promise(resolve => {
            this.http.ajax({
                type: "GET",
                url: `/api/rainbow-table/${rainbowTableId}/search/count?includeNotFound=${includeNotFound}`,
                success: res => resolve(res.searchCount),
                error: this.errorHandler
            });
        });
    }

    getRainbowTableSearchResultsById(rainbowTableId) {
        return new Promise(resolve => {
            this.http.ajax({
                type: "GET",
                url: `/api/rainbow-table/${rainbowTableId}/searchResults`,
                success: searchResults => resolve(searchResults),
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
