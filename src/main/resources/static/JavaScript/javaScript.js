function filterBySupplierName() {
  var input, filter, table, tr, td, i, txtValue;
  input = document.getElementById("supplierInput");
  filter = input.value.toUpperCase();
  table = document.getElementById("invoiceTable");
  tr = table.getElementsByTagName("tr");

  for (i = 0; i < tr.length; i++) {
    td = tr[i].getElementsByTagName("td")[0];
    if (td) {
      txtValue = td.textContent || td.innerText;
      if (txtValue.toUpperCase().indexOf(filter) > -1) {
        tr[i].style.display = "";
      } else {
        tr[i].style.display = "none";
      }
    }
  }
}

function findSupplierByName() {
  var input, filter, table, tr, td, i, txtValue;
  input = document.getElementById("supplierName");
  filter = input.value.toUpperCase();
  table = document.getElementById("supplierTable");
  tr = table.getElementsByTagName("tr");

  for (i = 0; i < tr.length; i++) {
    td = tr[i].getElementsByTagName("td")[0];
    if (td) {
      txtValue = td.textContent || td.innerText;
      if (txtValue.toUpperCase().indexOf(filter) > -1) {
        tr[i].style.display = "";
      } else {
        tr[i].style.display = "none";
      }
    }
  }
}