package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        return adminRepository1.save(admin);
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Admin admin = adminRepository1.findById(adminId).orElseThrow(() -> new RuntimeException("Admin not found"));
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);
        serviceProvider = serviceProviderRepository1.save(serviceProvider);
        admin.getServiceProviders().add(serviceProvider);
        return adminRepository1.save(admin);
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).orElseThrow(() -> new RuntimeException("Service Provider not found"));
        CountryName countryEnum;
        String countryCode;

        switch (countryName.toLowerCase()) {
            case "ind":
                countryEnum = CountryName.IND;
                countryCode = CountryName.IND.toCode();
                break;
            case "usa":
                countryEnum = CountryName.USA;
                countryCode = CountryName.USA.toCode();
                break;
            case "aus":
                countryEnum = CountryName.AUS;
                countryCode = CountryName.AUS.toCode();
                break;
            case "chi":
                countryEnum = CountryName.CHI;
                countryCode = CountryName.CHI.toCode();
                break;
            case "jpn":
                countryEnum = CountryName.JPN;
                countryCode = CountryName.JPN.toCode();
                break;
            default:
                throw new Exception("Country not found");
        }

        Country country = new Country();
        country.setCountryName(countryEnum);
        country.setCode(countryCode);
        country.setServiceProvider(serviceProvider);

        country = countryRepository1.save(country);
        serviceProvider.getCountryList().add(country);
        return serviceProviderRepository1.save(serviceProvider);

    }
}
