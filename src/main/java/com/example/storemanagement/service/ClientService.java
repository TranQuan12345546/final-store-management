package com.example.storemanagement.service;

import com.example.storemanagement.dto.projection.ClientPublic;
import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.dto.projection.StaffPublic;
import com.example.storemanagement.dto.request.UpsertClientRequest;
import com.example.storemanagement.enity.Client;
import com.example.storemanagement.enity.Staff;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.ClientRepository;
import com.example.storemanagement.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final StoreRepository storeRepository;

    @Autowired
    private ImageService imageService;

    public ClientService(ClientRepository clientRepository,
                         StoreRepository storeRepository) {
        this.clientRepository = clientRepository;
        this.storeRepository = storeRepository;
    }

    public List<ClientPublic> getAllClientFromStore(Integer storeId) {
        List<Client> clients = clientRepository.findAllByStoreId(storeId);
        return clients.stream().map(ClientPublic::of).collect(Collectors.toList());
    }

    public ClientPublic createNewClient(Integer storeId, UpsertClientRequest upsertClientRequest, MultipartFile file) {

        Client client = new Client();
        client.setName(upsertClientRequest.getFullName());
        client.setEmail(upsertClientRequest.getEmail());
        client.setAddress(upsertClientRequest.getAddress());
        client.setPhone(upsertClientRequest.getPhone());
        client.setBirthday(upsertClientRequest.getBirthday());
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy store");
        });
        client.setStore(store);
        if (file != null) {
            client.setAvatar(imageService.uploadAvatar(file));
        }
        clientRepository.save(client);
            return ClientPublic.of(client);
        }

    public void uploadAvatarImage(MultipartFile file, Integer clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy nhân viên");
        });;
        client.setAvatar(imageService.uploadAvatar(file));
        clientRepository.save(client);
    }

    public List<ClientPublic> getSuggestClient(String name, Integer storeId) {
        List<Client> clientList = new ArrayList<>();
        List<Client> clients = clientRepository.findAllByStoreId(storeId);
        for (Client client : clients) {
            String lowercaseClientName = client.getName().toLowerCase();
            if (lowercaseClientName.contains(name)) {
                clientList.add(client);
            }
        }
        return clientList.stream().map(ClientPublic::of).collect(Collectors.toList());
    }

    public ClientPublic updateClientInfo(UpsertClientRequest upsertClientRequest) {
        Client client = clientRepository.findById(upsertClientRequest.getClientId()).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy khách hàng");
        });
        String fullName = upsertClientRequest.getFullName();
        if (fullName != null) {
            client.setName(fullName);
        }
        String email = upsertClientRequest.getEmail();
        if (email != null) {
            client.setEmail(email);
        }
        String phone = upsertClientRequest.getPhone();
        if (phone != null) {
            client.setPhone(phone);
        }
        String birthday = upsertClientRequest.getBirthday();
        if (birthday != null) {
            client.setBirthday(birthday);
        }
        clientRepository.save(client);
        return ClientPublic.of(client);
    }

    public void deleteClient(Integer clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy khách hàng");
        });
        clientRepository.delete(client);
    }
}
