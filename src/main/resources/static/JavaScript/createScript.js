$('#registerForm').submit(function(e){
    e.preventDefault();

    var user = new Object();
    user.username = $('#username').val();
    user.firstName = $('#firstName').val();
    user.lastName = $('#lastName').val();
    user.email = $('#email').val();
    user.password = $('#inputPasswordCheck').val();

    $.ajax({
        type: 'POST',
        url: '/register',
        data: JSON.stringify(user),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(){
            window.location.href = '/home'
        },
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

$('#createSupplierForm').submit(function(e){
    e.preventDefault();

    var supplier = new Object();
    supplier.supplierName = $('#supplierName').val();
    supplier.phoneNumber = $('#phoneNumber').val();

    $.ajax({
        type: 'POST',
        url: '/supplier-management',
        data: JSON.stringify(supplier),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(){
            window.location.href = '/supplier-management'
        },
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

$('#createInvoiceForm').submit(function(e){
    e.preventDefault();

    var invoice = new Object();
    invoice.supplier = $('#supplier').val();
    invoice.invoiceNumber = $('#invoiceNumber').val();
    invoice.currency = $('#currency').val();
    invoice.value = $('#value').val();
    invoice.dueDate = $('#dueDate').val();
    invoice.status = $('#status').val();
//    invoiceImage = $('#invoiceImage').val();

    $.ajax({
        type: 'POST',
        url: '/invoice-management',
        data: JSON.stringify(invoice),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function(){
            window.location.href = '/invoice-management'
        },
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
