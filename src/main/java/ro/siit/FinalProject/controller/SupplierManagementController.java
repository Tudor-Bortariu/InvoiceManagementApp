package ro.siit.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ro.siit.FinalProject.api.SupplierApi;
import ro.siit.FinalProject.exception.ObjectNotFoundException;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.repository.JpaSupplierRepository;
import ro.siit.FinalProject.service.InvoiceServiceImpl;
import ro.siit.FinalProject.service.SecurityServiceImpl;

import java.util.Optional;
import java.util.UUID;

@Controller
public class SupplierManagementController implements SupplierApi {

    @Autowired
    private SecurityServiceImpl securityService;

    @Autowired
    private JpaSupplierRepository supplierRepository;

    @Autowired
    private InvoiceServiceImpl invoiceService;

    @Override
    public String supplierManagement(Model model) {
        model.addAttribute("suppliers", supplierRepository.findAllSuppliersByUser(securityService.getUser()));

        return "SupplierManagement/supplierManagement";
    }

    @Override
    public String addSupplierForm(Model model) {
        return "SupplierManagement/addSupplier";
    }

    @Override
    public RedirectView addSupplier(Model model,
                                    @RequestParam String supplierName,
                                    @RequestParam String phoneNumber,
                                    @RequestParam String county) {

        invoiceService.checkIfSupplierExistsForUser(supplierName);

        Supplier addedSupplier = new Supplier();

        addedSupplier.setSupplierName(supplierName);
        addedSupplier.setPhoneNumber(phoneNumber);

        addedSupplier.setUser(securityService.getUser());

        supplierRepository.saveAndFlush(addedSupplier);

        return new RedirectView("/supplierManagement");
    }

    @Override
    public RedirectView deleteSupplier(Model model, @PathVariable UUID id) {
        supplierRepository.deleteBySupplierId(id);
        return new RedirectView("/supplierManagement");
    }

    @Override
    public String editSupplierForm(Model model, @PathVariable UUID id) {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        model.addAttribute("supplier", supplier.orElseThrow(ObjectNotFoundException::new));

        return "SupplierManagement/editForm";
    }

    @Override
    public RedirectView editInvoice(Model model,
                                    @RequestParam UUID supplierId,
                                    @RequestParam String updatedSupplierName,
                                    @RequestParam String updatedPhoneNumber,
                                    @RequestParam String updatedCounty) {

        Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(ObjectNotFoundException::new);

        supplier.setSupplierName(updatedSupplierName);
        supplier.setPhoneNumber(updatedPhoneNumber);

        supplierRepository.saveAndFlush(supplier);

        return new RedirectView("/supplierManagement");
    }
}
