package ro.siit.FinalProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ro.siit.FinalProject.mapstruct.dto.supplier.CreateSupplierDto;
import ro.siit.FinalProject.mapstruct.dto.supplier.UpdateSupplierDto;
import ro.siit.FinalProject.mapstruct.response.supplier.SupplierResponse;
import ro.siit.FinalProject.service.SupplierService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/supplier-management")
@RequiredArgsConstructor
public class SupplierManagementController {
    private final SupplierService supplierService;

    @GetMapping
    public ModelAndView supplierManagementView() {
        return new ModelAndView("SupplierManagement/supplierManagement");
    }

    @GetMapping("/form")
    public ModelAndView getAddSupplierForm() {
        return new ModelAndView("SupplierManagement/addSupplier");
    }

    @GetMapping("/{supplierId}/form")
    public ModelAndView getEditSupplierForm(@PathVariable UUID supplierId) {
        ModelAndView modelAndView = new ModelAndView("SupplierManagement/editForm");
        modelAndView.addObject("supplier", supplierService.findById(supplierId));
        return modelAndView;
    }

    @GetMapping("/suppliers")
    public ResponseEntity<List<SupplierResponse>> getSuppliers() {
        return new ResponseEntity<>(supplierService.findByUser_OrderBySupplierNameAsc(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SupplierResponse> addSupplier(@RequestBody CreateSupplierDto createSupplierDto) {
        return new ResponseEntity<>(supplierService.createSupplier(createSupplierDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{supplierId}")
    public ResponseEntity<String> deleteSupplier(@PathVariable UUID supplierId) {
        supplierService.deleteById(supplierId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{supplierId}")
    public ResponseEntity<SupplierResponse> editSupplier(@RequestBody UpdateSupplierDto updateSupplierDto) {
        return new ResponseEntity<>(supplierService.updateSupplier(updateSupplierDto), HttpStatus.OK);
    }
}
