package com.example.domain.products.model;

import com.example.domain.suppliers.model.MSupplier;

import lombok.Data;

@Data
public class ProductWithSupplier {
	
	private MProduct product;
	private MSupplier supplier; 

}
