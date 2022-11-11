package ro.siit.FinalProject.api;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@RequestMapping("supplierManagement")
public interface SupplierApi {

    @GetMapping("")
    String supplierManagement(Model model);

    @GetMapping("/addSupplier")
    String addSupplierForm(Model model);

    @PostMapping("/addSupplier")
    RedirectView addSupplier(Model model,
                                    @RequestParam String supplierName,
                                    @RequestParam String phoneNumber,
                                    @RequestParam String county);

    @GetMapping("/delete/{id}")
    RedirectView deleteSupplier(Model model, @PathVariable UUID id);

    @GetMapping("/edit/{id}")
    String editSupplierForm(Model model, @PathVariable UUID id);

    @PostMapping("/edit")
    RedirectView editInvoice(Model model,
                                    @RequestParam UUID supplierId,
                                    @RequestParam String updatedSupplierName,
                                    @RequestParam String updatedPhoneNumber,
                                    @RequestParam String updatedCounty);
}
