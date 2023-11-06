package ro.siit.FinalProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.service.SupplierService;

import java.util.UUID;

@RestController
@RequestMapping("/supplier-management")
@RequiredArgsConstructor
public class SupplierManagementController {
    private final SupplierService supplierService;

    @GetMapping
    public ModelAndView supplierManagementView() {
        ModelAndView modelAndView = new ModelAndView("SupplierManagement/supplierManagement");
        modelAndView.addObject("suppliers", supplierService.findByUser_OrderBySupplierNameAsc());
        return modelAndView;
    }

    @GetMapping("/form")
    public ModelAndView getAddSupplierForm() {
        return new ModelAndView("SupplierManagement/addSupplier");
    }

    @PostMapping
    public ResponseEntity<Supplier> addSupplier(@RequestParam String supplierName,
                                                @RequestParam String phoneNumber) {
        return new ResponseEntity<>(supplierService.createSupplier(supplierName, phoneNumber), HttpStatus.CREATED);
    }

    @DeleteMapping("/{supplierId}")
    public ResponseEntity<String> deleteSupplier(@PathVariable UUID supplierId) {
        supplierService.deleteById(supplierId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{supplierId}/form")
    public ModelAndView getEditSupplierForm(@PathVariable UUID supplierId) {
        ModelAndView modelAndView = new ModelAndView("SupplierManagement/editForm");
        modelAndView.addObject("supplier", supplierService.findById(supplierId));
        return modelAndView;
    }

    @PutMapping("/{supplierId}")
    public ResponseEntity<Supplier> editSupplier(@PathVariable UUID supplierId,
                                     @RequestParam String updatedSupplierName,
                                     @RequestParam String updatedPhoneNumber,
                                     @RequestParam String updatedCounty) {

        Supplier supplier = supplierService.findById(supplierId);
        supplier.setSupplierName(updatedSupplierName);
        supplier.setPhoneNumber(updatedPhoneNumber);

        return new ResponseEntity<>(supplierService.saveSupplier(supplier), HttpStatus.OK);
    }
}
