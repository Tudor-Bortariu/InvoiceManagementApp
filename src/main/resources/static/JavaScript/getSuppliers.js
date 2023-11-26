$(document).ready(function(){
    $.ajax({
        type: 'GET',
        url: '/supplier-management/suppliers',
        success: onSuccess,
        error: function(jqXHR, status, error){
            if(jqXHR.status == 400){
                var data = jqXHR.responseText;
                var jSonResponse = JSON.parse(data);
                alert('Error: ' + jSonResponse['errors'][0]['defaultMessage']);
            }else {
                var err = eval('(' + jqXHR.responseText + ')');
                alert(err.message);
            }
        }
    });
});

function onSuccess(data){
    var items = data;
    var length = data.length;
    var table = $('#supplierTable tbody');

    for(var i = 0; i < length; i++){
        var supplierId = data[i].id;
        var supplierName = data[i].supplierName;
        var phoneNumber = data[i].phoneNumber;

        table.append('<tr><td>' + supplierName + '</td><td>' + phoneNumber + '</td><td>'
        + "<a class='btn btn-outline-info' href='/supplier-management/" + supplierId + "/form'><i class='bi bi-pencil-fill'></i></a>"
        + "<form method='delete' id='supplierDelete' name='supplierDelete' action='/supplier-management/" + supplierId
        + "'><button type='submit' class='btn btn-danger'><i class='bi bi-trash'></i></button></form>" + '</td></tr>');
    }
};

$('body').on('submit', '#supplierDelete', function(e){
    e.preventDefault();

    $.ajax({
        type: $(this).attr('method'),
        url: $(this).attr('action'),
        contentType: 'application/json; charset=utf-8',
        success: function(){
            window.location.reload();
        },
        error: function(jqXHR, textStatus, errorThrown){
            if(jqXHR.status == 400){
                var data = jqXHR.responseText;
                var jSonResponse = JSON.parse(data);
                alert('Error: ' + jSonResponse['errors'][0]['defaultMessage']);
            }else {
                var err = eval('(' + jqXHR.responseText + ')');
                alert(err.message);
            }
        }
    });
});