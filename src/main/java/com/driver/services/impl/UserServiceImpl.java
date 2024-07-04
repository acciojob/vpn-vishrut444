package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
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

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setConnected(false);
        user.setMaskedIp(null);

        Country country = new Country();
        country.setCountryName(countryEnum);
        country.setCode(countryCode);
        country.setUser(user);
        user.setOriginalCountry(country);

        user = userRepository3.save(user);
        user.setOriginalIp(countryCode + "." + user.getId());
        return userRepository3.save(user);
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository3.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).orElseThrow(() -> new RuntimeException("Service Provider not found"));

        user.getServiceProviderList().add(serviceProvider);
        return userRepository3.save(user);

    }
}
