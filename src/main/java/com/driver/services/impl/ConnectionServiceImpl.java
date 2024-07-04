package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user = userRepository2.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getMaskedIp() != null) {
            throw new Exception("Already connected");
        }

        if (user.getOriginalCountry().getCountryName().toString().equalsIgnoreCase(countryName)) {
            return user;
        }

        List<ServiceProvider> serviceProviders = user.getServiceProviderList();
        ServiceProvider selectedServiceProvider = null;

        for (ServiceProvider serviceProvider : serviceProviders) {
            for (Country country : serviceProvider.getCountryList()) {
                if (country.getCountryName().toString().equalsIgnoreCase(countryName)) {
                    if (selectedServiceProvider == null || serviceProvider.getId() < selectedServiceProvider.getId()) {
                        selectedServiceProvider = serviceProvider;
                    }
                }
            }
        }

        if (selectedServiceProvider == null) {
            throw new Exception("Unable to connect");
        }

        String maskedIp = countryName.toUpperCase() + "." + selectedServiceProvider.getId() + "." + user.getId();
        user.setMaskedIp(maskedIp);
        user.setConnected(true);

        Connection connection = new Connection();
        connection.setUser(user);
        connection.setServiceProvider(selectedServiceProvider);
        connectionRepository2.save(connection);

        return userRepository2.save(user);
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getMaskedIp() == null) {
            throw new Exception("Already disconnected");
        }

        user.setMaskedIp(null);
        user.setConnected(false);
        return userRepository2.save(user);
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender = userRepository2.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository2.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

        String receiverCurrentCountry = receiver.getMaskedIp() != null ?
                receiver.getMaskedIp().split("\\.")[0] : receiver.getOriginalCountry().getCountryName().toString();

        if (sender.getOriginalCountry().getCountryName().toString().equalsIgnoreCase(receiverCurrentCountry)) {
            return sender;
        }

        return connect(senderId, receiverCurrentCountry);
    }
}
