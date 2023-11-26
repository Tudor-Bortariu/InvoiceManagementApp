$(document).ready(function(){
    $.ajax({
        type: 'GET',
        url: '/invoice-management/invoices',
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
    var table = $('#invoiceTable tbody');

    for(var i = 0; i < length; i++){
        var invoiceId = data[i].id;
        var invoiceNumber = data[i].invoiceNumber;
        var supplierName = data[i].supplier.supplierName;
        var value = data[i].value;
        var currency = data[i].currency;
        var dueDate = data[i].dueDate;
        var introductionDate = data[i].introductionDate;
        var status = data[i].status;
        var invoiceImage = data[i].invoiceImage;
        var tableButtons = null;

        if(invoiceImage != null){
            tableButtons = "<a class='btn btn-outline-primary' href='/invoice-management/" + invoiceNumber + "/resources'>View Invoice</a>"
            + "<form method='delete' id='resourceDelete' name='resourceDelete' action='/invoice-management/" + invoiceNumber
            + "/resources'><button type='submit' class='btn btn-success' style='background-color: #04AA6D;'>Delete Image</button></form>";
        }else {
            tableButtons = "<a class='btn btn-outline-secondary active'>No image available</a>";
        }

        table.append('<tr><th>' + invoiceNumber + '</th><td>' + supplierName + '</td><td>' + value + '</td><td>' + currency + '</td><td>' + dueDate
        + '</td><td>' + introductionDate.substring(0, 10) + '</td><td>' + status + '</td><td>' + "<form method='delete' id='invoiceDelete' name='invoiceDelete'"
        + "action='/invoice-management/" + invoiceId + "'><button type='submit' class='btn btn-danger'><i class='bi bi-trash'></i></button></form>"
        + "<a class='btn btn-outline-primary' href='/invoice-management/" + invoiceId + "/form'><i class='bi bi-pencil-fill'></i></a>"
        + '</td><td>' + "<form id='changePaymentStatus' name='changePaymentStatus' method='patch' action='/invoice-management/" + invoiceNumber
        + "'><button type='submit' class='btn btn-success' style='background-color: #04AA6D;'>Change Payment Status</button></form>"
        + '</td><td>' + tableButtons +'</td></tr>');
    }
};

$('body').on('submit', '#invoiceDelete', function(e){
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

$('body').on('submit', '#changePaymentStatus', function(e){
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

$('body').on('submit', '#resourceDelete', function(e){
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