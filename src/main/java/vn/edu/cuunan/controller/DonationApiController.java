package vn.edu.cuunan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.cuunan.entity.Donation;
import vn.edu.cuunan.service.DonationService;

import java.util.*;

@RestController
@RequestMapping("/api/donations")
public class DonationApiController {

    private final DonationService donationService;

    @Autowired
    public DonationApiController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(donationService.getDonationStats());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Map<String, Object>>> getRecent() {
        List<Donation> list = donationService.getRecentDonations();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Donation d : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("donorName", d.getDonorName());
            item.put("amount", d.getAmount());
            item.put("formattedAmount", d.getFormattedAmount());
            item.put("message", d.getMessage());
            item.put("targetProvince", d.getTargetProvince());
            item.put("paidAt", d.getPaidAt());
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status/{code}")
    public ResponseEntity<Map<String, Object>> checkStatus(@PathVariable("code") String code) {
        Optional<Donation> optionalDonation = donationService.getDonationByCode(code);
        Map<String, Object> response = new HashMap<>();
        if (optionalDonation.isPresent()) {
            response.put("status", optionalDonation.get().getStatus());
            response.put("amount", optionalDonation.get().getAmount());
            response.put("transactionCode", code);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "NOT_FOUND");
            return ResponseEntity.status(404).body(response);
        }
    }
}
