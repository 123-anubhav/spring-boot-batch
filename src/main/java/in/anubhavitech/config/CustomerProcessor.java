package in.anubhavitech.config;

import org.springframework.batch.item.ItemProcessor;

import in.anubhavitech.entity.Customer;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(Customer item) throws Exception {

		// logic
		System.out.println("CustomerProcessor.process()");

		return item;
	}

}