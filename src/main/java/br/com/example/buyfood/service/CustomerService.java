package br.com.example.buyfood.service;

import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.CustomerRequestDto;
import br.com.example.buyfood.model.dto.response.CustomerResponseDto;
import br.com.example.buyfood.model.entity.CustomerEntity;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.model.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerService {

    private final ModelMapper modelMapper;

    private final CustomerRepository customerRepository;

    public CustomerService(ModelMapper modelMapper, CustomerRepository customerRepository) {
        this.modelMapper = modelMapper;
        this.customerRepository = customerRepository;
    }

    public List<CustomerResponseDto> getCustomerList() {
        return customerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CustomerResponseDto getCustomer(Long id) {
        return customerRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    public CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto) {
        CustomerEntity customerEntity = convertToEntity(customerRequestDto);
        return convertToDto(customerRepository.save(customerEntity));
    }

    public void updateCustomer(Long id, CustomerRequestDto customerRequestDto) {
        getCustomerById(id);
        CustomerEntity customerEntity = convertToEntity(customerRequestDto);
        customerEntity.setId(id);
        customerRepository.save(customerEntity);
    }

    public void deleteCustomer(Long id) {
        CustomerEntity customerEntity = getCustomerById(id);
        customerEntity.setStatus(RegisterStatus.DISABLED.getValue());
        customerRepository.save(customerEntity);
    }

    private CustomerEntity getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    private CustomerResponseDto convertToDto (CustomerEntity customerEntity) {
        return modelMapper.map(customerEntity, CustomerResponseDto.class);
    }

    private CustomerEntity convertToEntity (CustomerRequestDto customerRequestDto) {
        return modelMapper.map(customerRequestDto, CustomerEntity.class);
    }
}
