package in.anubhavitech.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import in.anubhavitech.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Serializable> {

}