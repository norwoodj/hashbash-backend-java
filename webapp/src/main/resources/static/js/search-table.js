jQuery(function ($) {
    var rainbowTableMap = new Map();

    function updateSelectedRainbowTable(rainbowTable) {
        var tableBody = $('tbody');
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
            updateSelectedRainbowTable(rainbowTables[0]);
        }
    });

    $('#rainbow-table-select').change(function () {
        var optionId = $('#rainbow-table-select').find('option:selected')[0].id;
        var rainbowTableId = parseInt(optionId.replace('rt-option-', ''));
        var selectedRainbowTable = rainbowTableMap.get(rainbowTableId);
        updateSelectedRainbowTable(selectedRainbowTable);
    });
});
