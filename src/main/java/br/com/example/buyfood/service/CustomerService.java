package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.CustomerRequestDto;
import br.com.example.buyfood.model.dto.response.CustomerResponseDto;
import br.com.example.buyfood.model.entity.CustomerEntity;
import br.com.example.buyfood.model.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CustomerRepository customerRepository;

    public List<CustomerResponseDto> getCustomerList(Integer status) {
        if (status == null){
            return customerRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getCustomerListByStatus(RegisterStatus.ENABLED);
                case 0: {
                    return getCustomerListByStatus(RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
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

    private List<CustomerResponseDto> getCustomerListByStatus(RegisterStatus enabled) {
        return customerRepository.findAllByStatus(enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CustomerResponseDto convertToDto (CustomerEntity customerEntity) {
        return modelMapper.map(customerEntity, CustomerResponseDto.class);
    }

    private CustomerEntity convertToEntity (CustomerRequestDto customerRequestDto) {
        return modelMapper.map(customerRequestDto, CustomerEntity.class);
    }
}
