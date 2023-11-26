$('#updateSupplierForm').submit(function(e){
    e.preventDefault();

    var supplier = new Object();
    supplier.id = $('#supplierId').val();
    supplier.supplierName = $('#supplierName').val();
    supplier.phoneNumber = $('#phoneNumber').val();

    $.ajax({
        type: 'PUT',
        url: '/supplier-management/' + supplier.id,
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

$('#updateInvoiceForm').submit(function(e){
    e.preventDefault();
    var pageUrl = window.location.href;
    var str = pageUrl.split("/");
    var invoiceId = str[4];

    var invoice = new Object();
    invoice.id = invoiceId;
    invoice.supplier = $('#updatedSupplier').val();
    invoice.invoiceNumber = $('#invoiceNumber').val();
    invoice.currency = $('#currency').val();
    invoice.value = $('#value').val();
    invoice.dueDate = $('#dueDate').val();
    invoice.status = $('#paymentStatus').val();
//    invoiceImage = $('#invoiceImage').val();

    $.ajax({
        type: 'PUT',
        url: '/invoice-management/' + invoiceId,
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
