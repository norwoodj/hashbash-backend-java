jQuery(function ($) {
    var rainbowTableMap = new Map();
    var currentRainbowTable;

    function addResultRow(rainbowTableName, hash, password) {
        var tableBody = $('#rainbow-search-results').find('tbody');

        var rowHtml = [
            '<tr>',
            '<td>' + rainbowTableName + '</td>',
            '<td>' + hash + '</td>',
            '<td>' + password + '</td>',
            '</tr>'
        ].join('\n');

        tableBody.append(rowHtml);
    }

    function updateSelectedRainbowTable(rainbowTable) {
        var tableBody = $('#selected-rainbow-table').find('tbody');
        tableBody.find('tr').remove();

        var rowHtml = [
            '<tr>',
            '<td>' + rainbowTable.id + '</td>',
            '<td>' + rainbowTable.name + '</td>',
            '<td>' + rainbowTable.hashFunction + '</td>',
            '<td>' + rainbowTable.numChains + '</td>',
            '<td>' + rainbowTable.chainLength + '</td>',
            '<td>' + rainbowTable.characterSet + '</td>',
            '<td>' + rainbowTable.passwordLength + '</td>',
            '</tr>'
        ].join('\n');

        tableBody.append(rowHtml);
    }

    $.get('/api/rainbow-table/', function (rainbowTables) {
        var completedRainbowTables = rainbowTables.filter(function (rainbowTable) {
            return rainbowTable.status == 'COMPLETED';
        });

        completedRainbowTables.forEach(function (rainbowTable) {
            rainbowTableMap.set(rainbowTable.id, rainbowTable);
        });

        var optionsHtml = completedRainbowTables.map(function (rainbowTable) {
            return '<option id="rt-option-' + rainbowTable.id + '">' + rainbowTable.name + '</option>';
        }).join('\n');

        $('#rainbow-table-select').html(optionsHtml);

        if (completedRainbowTables.length > 0) {
            currentRainbowTable = rainbowTables[0];
            updateSelectedRainbowTable(currentRainbowTable);
        }
    });

    function startSearch() {
        $('#search-button').attr('disabled', 'disabled');
        $('.search-in-progress').css('visibility', 'visible');
    }

    function stopSearch() {
        $('#search-button').removeAttr('disabled');
        $('.search-in-progress').css('visibility', 'hidden');
    }

    $('#search-form').submit(function () {
        var hashValue = $('#hash-input').val();
        var rainbowTable = currentRainbowTable;
        startSearch();

        $.ajax({
            type: 'GET',
            url: '/api/rainbow-table/' + rainbowTable.id + '/search?hash=' + hashValue,
            success: function (searchResult) {
                addResultRow(rainbowTable.name, hashValue, searchResult.password);
                stopSearch();
            },
            error: function failure() {
                addResultRow(rainbowTable.name, hashValue, 'NULL');
                stopSearch();
            }
        });

        return false;
    });

    $('#rainbow-table-select').change(function () {
        var optionId = $('#rainbow-table-select').find('option:selected')[0].id;
        var rainbowTableId = parseInt(optionId.replace('rt-option-', ''));
        currentRainbowTable = rainbowTableMap.get(rainbowTableId);
        updateSelectedRainbowTable(currentRainbowTable);
    });
});
